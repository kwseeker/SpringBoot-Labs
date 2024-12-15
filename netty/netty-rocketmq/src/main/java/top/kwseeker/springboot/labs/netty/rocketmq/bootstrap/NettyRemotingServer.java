package top.kwseeker.springboot.labs.netty.rocketmq.bootstrap;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.springboot.labs.netty.rocketmq.remoting.NettyRemotingAbstract;
import top.kwseeker.springboot.labs.netty.rocketmq.remoting.handler.NettyDecoder;
import top.kwseeker.springboot.labs.netty.rocketmq.remoting.handler.NettyEncoder;
import top.kwseeker.springboot.labs.netty.rocketmq.common.RemotingHelper;
import top.kwseeker.springboot.labs.netty.rocketmq.common.ThreadFactoryImpl;
import top.kwseeker.springboot.labs.netty.rocketmq.common.constant.LoggerName;
import top.kwseeker.springboot.labs.netty.rocketmq.protocol.RemotingCommand;
import top.kwseeker.springboot.labs.netty.rocketmq.bootstrap.config.NettyServerConfig;
import top.kwseeker.springboot.labs.netty.rocketmq.remoting.handler.NettyConnectManageHandler;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * RocketMQ中封装的Netty服务端的简化版
 * 从RocketMQ中提取
 */
public class NettyRemotingServer extends NettyRemotingAbstract implements RemotingServer {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.ROCKETMQ_REMOTING_NAME);

    public static final String HANDSHAKE_HANDLER_NAME = "handshakeHandler";

    private final ServerBootstrap serverBootstrap;
    private final EventLoopGroup eventLoopGroupSelector;
    private final EventLoopGroup eventLoopGroupBoss;
    private DefaultEventExecutorGroup defaultEventExecutorGroup;
    //用于处理请求响应的固定数量线程池(使用此线程池执行回调)
    private final ExecutorService publicExecutor;
    private NettyServerConfig nettyServerConfig;

    private NettyEncoder encoder;
    // 主要是在连接空闲超时以及pipeline中处理出现异常时关闭连接
    private NettyConnectManageHandler connectionManageHandler;
    // 用于业务处理的 ChannelHandler
    private NettyServerHandler serverHandler;

    private final HashedWheelTimer timer = new HashedWheelTimer(r -> new Thread(r, "ServerHouseKeepingService"));
    protected AtomicBoolean isShuttingDown = new AtomicBoolean(false);

    // 记录当前服务器启动的所有服务器实例，Port -> NettyRemotingAbstract
    private final ConcurrentMap<Integer/*Port*/, NettyRemotingAbstract> remotingServerTable = new ConcurrentHashMap<>();

    public NettyRemotingServer(final NettyServerConfig nettyServerConfig) {
        super(nettyServerConfig.getServerAsyncSemaphoreValue());
        this.serverBootstrap = new ServerBootstrap();
        this.nettyServerConfig = nettyServerConfig;
        //this.eventLoopGroupBoss = new EpollEventLoopGroup(1, new ThreadFactoryImpl("NettyEPOLLBoss_"));
        this.eventLoopGroupBoss = new NioEventLoopGroup(1, new ThreadFactoryImpl("NettyNIOBoss_"));
        this.eventLoopGroupSelector = new NioEventLoopGroup(nettyServerConfig.getServerSelectorThreads(), new ThreadFactoryImpl("NettyServerNIOSelector_"));
        this.publicExecutor = buildPublicExecutor(nettyServerConfig);
    }

    @Override
    public void start() {
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(nettyServerConfig.getServerWorkerThreads(),
                new ThreadFactoryImpl("NettyServerCodecThread_"));

        //实例化可共享的ChannelHandler
        prepareSharableHandlers();

        serverBootstrap.group(this.eventLoopGroupBoss, this.eventLoopGroupSelector)
                //.channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .localAddress(new InetSocketAddress(this.nettyServerConfig.getBindAddress(),
                        this.nettyServerConfig.getListenPort()))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        configChannel(ch);
                    }
                });
        //用于根据 NettyServerConfig 为 serverBootstrap 设置额外的配置，
        //比如 SO_SNDBUF SO_RCVBUF WRITE_BUFFER_WATER_MARK ALLOCATOR
        addCustomConfig(serverBootstrap);

        try {
            // bind() 自动选择端口
            ChannelFuture sync = serverBootstrap.bind().sync();
            InetSocketAddress addr = (InetSocketAddress) sync.channel().localAddress();
            if (0 == nettyServerConfig.getListenPort()) {
                this.nettyServerConfig.setListenPort(addr.getPort());
            }
            log.info("RemotingServer started, listening {}:{}", this.nettyServerConfig.getBindAddress(),
                    this.nettyServerConfig.getListenPort());
            this.remotingServerTable.put(this.nettyServerConfig.getListenPort(), this);
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Failed to bind to %s:%d", nettyServerConfig.getBindAddress(),
                    nettyServerConfig.getListenPort()), e);
        }

        //if (this.channelEventListener != null) {
        //    this.nettyEventExecutor.start();
        //}

        //每隔一秒扫描一次，将超时（执行超过1s）的请求直接失败并从 responseTable 中清除
        TimerTask timerScanResponseTable = new TimerTask() {
            @Override
            public void run(Timeout timeout) {
                try {
                    NettyRemotingServer.this.scanResponseTable();
                } catch (Throwable e) {
                    log.error("scanResponseTable exception", e);
                } finally {
                    timer.newTimeout(this, 1000, TimeUnit.MILLISECONDS);
                }
            }
        };
        this.timer.newTimeout(timerScanResponseTable, 1000 * 3, TimeUnit.MILLISECONDS);

        // 每秒打印一次流量统计
        //scheduledExecutorService.scheduleWithFixedDelay(() -> {
        //    try {
        //        NettyRemotingServer.this.printRemotingCodeDistribution();
        //    } catch (Throwable e) {
        //        TRAFFIC_LOGGER.error("NettyRemotingServer print remoting code distribution exception", e);
        //    }
        //}, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void shutdown() {
        try {
            if (nettyServerConfig.isEnableShutdownGracefully()
                    && isShuttingDown.compareAndSet(false, true)) {
                Thread.sleep(Duration.ofSeconds(nettyServerConfig.getShutdownWaitTimeSeconds()).toMillis());
            }

            this.timer.stop();

            this.eventLoopGroupBoss.shutdownGracefully();
            this.eventLoopGroupSelector.shutdownGracefully();
            if (this.defaultEventExecutorGroup != null) {
                this.defaultEventExecutorGroup.shutdownGracefully();
            }
        } catch (Exception e) {
            log.error("NettyRemotingServer shutdown exception, ", e);
        }
    }

    @Override
    public int localListenPort() {
        return this.nettyServerConfig.getListenPort();
    }

    @Override
    public ExecutorService getCallbackExecutor() {
        return this.publicExecutor;
    }

    private ExecutorService buildPublicExecutor(NettyServerConfig nettyServerConfig) {
        int publicThreadNums = nettyServerConfig.getServerCallbackExecutorThreads();
        if (publicThreadNums <= 0) {
            publicThreadNums = 4;
        }

        return Executors.newFixedThreadPool(publicThreadNums, new ThreadFactoryImpl("NettyServerPublicExecutor_"));
    }

    private void prepareSharableHandlers() {
        encoder = new NettyEncoder();
        connectionManageHandler = new NettyConnectManageHandler();
        serverHandler = new NettyServerHandler();
    }

    // 配置 Pipeline
    protected ChannelPipeline configChannel(SocketChannel ch) {
        return ch.pipeline()
                //.addLast(defaultEventExecutorGroup, HANDSHAKE_HANDLER_NAME, new HandshakeHandler())
                .addLast(defaultEventExecutorGroup,
                        encoder,
                        new NettyDecoder(),
                        //distributionHandler,  // RemotingCodeDistributionHandler RocketMQ 用这个 ChannelHandler 统计命令请求响应次数
                        new IdleStateHandler(0, 0,
                                nettyServerConfig.getServerChannelMaxIdleTimeSeconds()),
                        connectionManageHandler,
                        serverHandler
                );
    }

    private void addCustomConfig(ServerBootstrap childHandler) {
        if (nettyServerConfig.getServerSocketSndBufSize() > 0) {
            log.info("server set SO_SNDBUF to {}", nettyServerConfig.getServerSocketSndBufSize());
            childHandler.childOption(ChannelOption.SO_SNDBUF, nettyServerConfig.getServerSocketSndBufSize());
        }
        if (nettyServerConfig.getServerSocketRcvBufSize() > 0) {
            log.info("server set SO_RCVBUF to {}", nettyServerConfig.getServerSocketRcvBufSize());
            childHandler.childOption(ChannelOption.SO_RCVBUF, nettyServerConfig.getServerSocketRcvBufSize());
        }
        if (nettyServerConfig.getWriteBufferLowWaterMark() > 0 && nettyServerConfig.getWriteBufferHighWaterMark() > 0) {
            log.info("server set netty WRITE_BUFFER_WATER_MARK to {},{}",
                    nettyServerConfig.getWriteBufferLowWaterMark(), nettyServerConfig.getWriteBufferHighWaterMark());
            childHandler.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(
                    nettyServerConfig.getWriteBufferLowWaterMark(), nettyServerConfig.getWriteBufferHighWaterMark()));
        }
        if (nettyServerConfig.isServerPooledByteBufAllocatorEnable()) {
            childHandler.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        }
    }

    /**
     * 业务处理 ChannelHandler
     */
    @ChannelHandler.Sharable
    public class NettyServerHandler extends SimpleChannelInboundHandler<RemotingCommand> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RemotingCommand msg) {
            int localPort = RemotingHelper.parseSocketAddressPort(ctx.channel().localAddress());
            NettyRemotingAbstract remotingAbstract = NettyRemotingServer.this.remotingServerTable.get(localPort);
            if (localPort != -1 && remotingAbstract != null) {
                // 处理消息
                remotingAbstract.processMessageReceived(ctx, msg);
                return;
            }
            // The related remoting server has been shutdown, so close the connected channel
            RemotingHelper.closeChannel(ctx.channel());
        }

        /**
         * 当通道底层网络缓冲区已满（比如因为对端接收速度慢、连接问题等导致）通道会变为不可写；反之会变为可写
         */
        @Override
        public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
            Channel channel = ctx.channel();
            if (channel.isWritable()) { // 通道变为可写
                if (!channel.config().isAutoRead()) {
                    channel.config().setAutoRead(true); //开启自动读，即继续读取数据
                    log.info("Channel[{}] turns writable, bytes to buffer before changing channel to un-writable: {}",
                            RemotingHelper.parseChannelRemoteAddr(channel), channel.bytesBeforeUnwritable());
                }
            } else {                    // 通道变为不可写
                channel.config().setAutoRead(false);    //关闭自动读，即不会读取数据
                log.warn("Channel[{}] auto-read is disabled, bytes to drain before it turns writable: {}",
                        RemotingHelper.parseChannelRemoteAddr(channel), channel.bytesBeforeWritable());
            }
            super.channelWritabilityChanged(ctx);
        }
    }
}
