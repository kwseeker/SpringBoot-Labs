package top.kwseeker.springboot.labs.netty.rocketmq.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.springboot.labs.netty.rocketmq.bootstrap.config.NettyClientConfig;
import top.kwseeker.springboot.labs.netty.rocketmq.common.RemotingHelper;
import top.kwseeker.springboot.labs.netty.rocketmq.common.ThreadFactoryImpl;
import top.kwseeker.springboot.labs.netty.rocketmq.common.constant.LoggerName;
import top.kwseeker.springboot.labs.netty.rocketmq.common.exception.RemotingConnectException;
import top.kwseeker.springboot.labs.netty.rocketmq.common.exception.RemotingSendRequestException;
import top.kwseeker.springboot.labs.netty.rocketmq.common.exception.RemotingTimeoutException;
import top.kwseeker.springboot.labs.netty.rocketmq.common.exception.RemotingTooMuchRequestException;
import top.kwseeker.springboot.labs.netty.rocketmq.protocol.RemotingCommand;
import top.kwseeker.springboot.labs.netty.rocketmq.protocol.ResponseCode;
import top.kwseeker.springboot.labs.netty.rocketmq.remoting.InvokeCallback;
import top.kwseeker.springboot.labs.netty.rocketmq.remoting.NettyRemotingAbstract;
import top.kwseeker.springboot.labs.netty.rocketmq.remoting.ResponseFuture;
import top.kwseeker.springboot.labs.netty.rocketmq.remoting.handler.NettyConnectManageHandler;
import top.kwseeker.springboot.labs.netty.rocketmq.remoting.handler.NettyDecoder;
import top.kwseeker.springboot.labs.netty.rocketmq.remoting.handler.NettyEncoder;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class NettyRemotingClient extends NettyRemotingAbstract implements RemotingClient {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.ROCKETMQ_REMOTING_NAME);
    private static final long LOCK_TIMEOUT_MILLIS = 3000;

    private final NettyClientConfig nettyClientConfig;
    private final Bootstrap bootstrap = new Bootstrap();
    // 客户端给 Selector 使用的线程池
    private final EventLoopGroup eventLoopGroupWorker;
    // 客户端给 Pipeline 使用的线程池
    private EventExecutorGroup defaultEventExecutorGroup;

    // 处理请求的响应数据的线程池
    private final ExecutorService publicExecutor;
    // 处理请求响应回调的线程池，如果没有设置就使用 publicExecutor
    private ExecutorService callbackExecutor;
    // 定时器扫描超时请求
    private final HashedWheelTimer timer = new HashedWheelTimer(r -> new Thread(r, "ClientHouseKeepingService"));

    // 存储到对端Server的连接信息， addr -> ChannelWrapper
    private final ConcurrentMap<String /* addr */, ChannelWrapper> channelTables = new ConcurrentHashMap<>();
    // 存储到对端Server的连接信息， Channel -> ChannelWrapper
    private final ConcurrentMap<Channel, ChannelWrapper> channelWrapperTables = new ConcurrentHashMap<>();
    // 用于创建到某个地址的连接时避免重复创建连接（双重检查锁），这个名字起得怪怪的还以为有多个锁（每个地址一把锁）可能Rocket开发人员在这里预留的待优化的点
    private final Lock lockChannelTables = new ReentrantLock();

    public NettyRemotingClient(final NettyClientConfig nettyClientConfig) {
        this(nettyClientConfig, null, null);
    }

    public NettyRemotingClient(final NettyClientConfig nettyClientConfig,
                               final EventLoopGroup eventLoopGroup,
                               final EventExecutorGroup eventExecutorGroup) {
        super(nettyClientConfig.getClientAsyncSemaphoreValue());
        this.nettyClientConfig = nettyClientConfig;
        //加载 SOCKS 代理
        //this.loadSocksProxyJson();

        int publicThreadNums = nettyClientConfig.getClientCallbackExecutorThreads();
        if (publicThreadNums <= 0) {
            publicThreadNums = 4;
        }
        this.publicExecutor = Executors.newFixedThreadPool(publicThreadNums,
                new ThreadFactoryImpl("NettyClientPublicExecutor_"));

        if (eventLoopGroup != null) {
            this.eventLoopGroupWorker = eventLoopGroup;
        } else {
            this.eventLoopGroupWorker = new NioEventLoopGroup(1, new ThreadFactoryImpl("NettyClientSelector_"));
        }
        this.defaultEventExecutorGroup = eventExecutorGroup;

        // TLS 支持，暂略， 对于面向C端的客户端还是需要支持的，TODO
        //if (nettyClientConfig.isUseTLS()) {
        //    try {
        //        sslContext = TlsHelper.buildSslContext(true);
        //        log.info("SSL enabled for client");
        //    } catch (IOException e) {
        //        log.error("Failed to create SSLContext", e);
        //    } catch (CertificateException e) {
        //        log.error("Failed to create SSLContext", e);
        //        throw new RuntimeException("Failed to create SSLContext", e);
        //    }
        //}
    }

    @Override
    public void start() {
        if (this.defaultEventExecutorGroup == null) {
            this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(
                    nettyClientConfig.getClientWorkerThreads(),
                    new ThreadFactoryImpl("NettyClientWorkerThread_"));
        }

        Bootstrap handler = this.bootstrap.group(this.eventLoopGroupWorker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, nettyClientConfig.getConnectTimeoutMillis())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //if (nettyClientConfig.isUseTLS()) {
                        //    if (null != sslContext) {
                        //        pipeline.addFirst(defaultEventExecutorGroup, "sslHandler", sslContext.newHandler(ch.alloc()));
                        //        log.info("Prepend SSL handler");
                        //    } else {
                        //        log.warn("Connections are insecure as SSLContext is null!");
                        //    }
                        //}
                        ch.pipeline().addLast(
                                nettyClientConfig.isDisableNettyWorkerGroup() ? null : defaultEventExecutorGroup,
                                new NettyEncoder(),
                                new NettyDecoder(),
                                new IdleStateHandler(0, 0, nettyClientConfig.getClientChannelMaxIdleTimeSeconds()),
                                new NettyConnectManageHandler(),
                                new NettyClientHandler());
                    }
                });
        if (nettyClientConfig.getClientSocketSndBufSize() > 0) {
            log.info("client set SO_SNDBUF to {}", nettyClientConfig.getClientSocketSndBufSize());
            handler.option(ChannelOption.SO_SNDBUF, nettyClientConfig.getClientSocketSndBufSize());
        }
        if (nettyClientConfig.getClientSocketRcvBufSize() > 0) {
            log.info("client set SO_RCVBUF to {}", nettyClientConfig.getClientSocketRcvBufSize());
            handler.option(ChannelOption.SO_RCVBUF, nettyClientConfig.getClientSocketRcvBufSize());
        }
        if (nettyClientConfig.getWriteBufferLowWaterMark() > 0 && nettyClientConfig.getWriteBufferHighWaterMark() > 0) {
            log.info("client set netty WRITE_BUFFER_WATER_MARK to {},{}",
                    nettyClientConfig.getWriteBufferLowWaterMark(), nettyClientConfig.getWriteBufferHighWaterMark());
            handler.option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(
                    nettyClientConfig.getWriteBufferLowWaterMark(), nettyClientConfig.getWriteBufferHighWaterMark()));
        }
        if (nettyClientConfig.isClientPooledByteBufAllocatorEnable()) {
            handler.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        }

        //nettyEventExecutor.start();
        TimerTask timerTaskScanResponseTable = new TimerTask() {
            @Override
            public void run(Timeout timeout) {
                try {
                    NettyRemotingClient.this.scanResponseTable();
                } catch (Throwable e) {
                    log.error("scanResponseTable exception", e);
                } finally {
                    timer.newTimeout(this, 1000, TimeUnit.MILLISECONDS);
                }
            }
        };
        this.timer.newTimeout(timerTaskScanResponseTable, 1000 * 3, TimeUnit.MILLISECONDS);
    }

    @Override
    public void shutdown() {
        try {
            this.timer.stop();

            for (String addr : this.channelTables.keySet()) {
                this.channelTables.get(addr).close();
            }
            this.channelWrapperTables.clear();
            this.channelTables.clear();

            this.eventLoopGroupWorker.shutdownGracefully();
            if (this.defaultEventExecutorGroup != null) {
                this.defaultEventExecutorGroup.shutdownGracefully();
            }
        } catch (Exception e) {
            log.error("NettyRemotingClient shutdown exception, ", e);
        }

        if (this.publicExecutor != null) {
            try {
                this.publicExecutor.shutdown();
            } catch (Exception e) {
                log.error("NettyRemotingServer shutdown exception, ", e);
            }
        }
    }

    /**
     * 先判断到目标地址的连接是否存在，不存在则先创建连接，再发送请求
     * @param addr 连接的目标地址
     * @param request 请求
     * @param timeoutMillis 请求超时时间 ms
     */
    @Override
    public CompletableFuture<RemotingCommand> invoke(String addr, RemotingCommand request,
                                                     long timeoutMillis) {
        CompletableFuture<RemotingCommand> future = new CompletableFuture<>();
        try {
            final Channel channel = this.getAndCreateChannel(addr);
            if (channel != null && channel.isActive()) {
                return invokeImpl(channel, request, timeoutMillis)
                        .whenComplete((v, t) -> {
                            if (t == null) {
                                updateChannelLastResponseTime(addr);
                            }
                        })
                        .thenApply(ResponseFuture::getResponseCommand);
            } else { //连接已经被关闭，关闭channel
                this.closeChannel(addr, channel);
                future.completeExceptionally(new RemotingConnectException(addr));
            }
        } catch (Throwable t) {
            future.completeExceptionally(t);
        }
        return future;
    }

    /**
     * 通过 addr 中的ip、端口创建连接
     */
    private Channel getAndCreateChannel(final String addr) throws InterruptedException {
        //RocketMQ 中如果 addr 为空，会从命名服务中获取某个Broker的地址，进行连接
        //if (null == addr) {
        //    return getAndCreateNameserverChannel();
        //}

        ChannelWrapper cw = this.channelTables.get(addr);
        if (cw != null && cw.isOK()) {
            return cw.getChannel();
        }

        return this.createChannel(addr);
    }

    /**
     * 创建到 addr 的连接
     * 双重检查锁，避免对同一地址的Server重复创建连接
     */
    private Channel createChannel(final String addr) throws InterruptedException {
        // 这里的双重检查锁，其实没有必要，可以使用 CHM putIfAbsent(), 返回值如果有旧值直接返回旧值即可
        ChannelWrapper cw = this.channelTables.get(addr);
        if (cw != null && cw.isOK()) {
            return cw.getChannel();
        }
        if (this.lockChannelTables.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
            try {
                boolean createNewConnection;
                cw = this.channelTables.get(addr);
                if (cw != null) {   //不为null还需要检查连接是否活跃（isActive(), 不活跃需要创建新连接
                    if (cw.isOK()) {
                        return cw.getChannel();
                    } else if (!cw.getChannelFuture().isDone()) {
                        createNewConnection = false;
                    } else {
                        this.channelTables.remove(addr);
                        createNewConnection = true;
                    }
                } else {
                    createNewConnection = true;
                }
                // 创建新连接
                if (createNewConnection) {
                    String[] hostAndPort = getHostAndPort(addr);    //0：ip， 1：port
                    ChannelFuture channelFuture = fetchBootstrap(addr)
                            .connect(hostAndPort[0], Integer.parseInt(hostAndPort[1]));
                    log.info("createChannel: begin to connect remote host[{}] asynchronously", addr);
                    cw = new ChannelWrapper(addr, channelFuture);
                    this.channelTables.put(addr, cw);
                    this.channelWrapperTables.put(channelFuture.channel(), cw);
                }
            } catch (Exception e) {
                log.error("createChannel: create channel exception", e);
            } finally {
                this.lockChannelTables.unlock();
            }
        } else {
            log.warn("createChannel: try to lock channel table, but timeout, {}ms", LOCK_TIMEOUT_MILLIS);
        }

        if (cw != null) {
            return waitChannelFuture(addr, cw);
        }
        return null;
    }

    private void updateChannelLastResponseTime(final String addr) {
        if (addr == null) {
            log.warn("[updateChannelLastResponseTime] could not find address!!");
            return;
        }
        ChannelWrapper channelWrapper = this.channelTables.get(addr);
        if (channelWrapper != null && channelWrapper.isOK()) {
            channelWrapper.updateLastResponseTime();
        }
    }

    private Bootstrap fetchBootstrap(String addr) {
        // TODO RocketMQ 还支持动态代理，暂时忽略这部分
        //Map.Entry<String, SocksProxyConfig> proxyEntry = getProxy(addr);
        //if (proxyEntry == null) {
            return bootstrap;
        //}
        //String cidr = proxyEntry.getKey();
        //SocksProxyConfig socksProxyConfig = proxyEntry.getValue();
        //log.info("Netty fetch bootstrap, addr: {}, cidr: {}, proxy: {}",
        //        addr, cidr, socksProxyConfig != null ? socksProxyConfig.getAddr() : "");
        //
        //Bootstrap bootstrapWithProxy = bootstrapMap.get(cidr);
        //if (bootstrapWithProxy == null) {
        //    bootstrapWithProxy = createBootstrap(socksProxyConfig);
        //    Bootstrap old = bootstrapMap.putIfAbsent(cidr, bootstrapWithProxy);
        //    if (old != null) {
        //        bootstrapWithProxy = old;
        //    }
        //}
        //return bootstrapWithProxy;
    }

    /**
     * 等待连接建立成功
     */
    private Channel waitChannelFuture(String addr, ChannelWrapper cw) {
        ChannelFuture channelFuture = cw.getChannelFuture();
        if (channelFuture.awaitUninterruptibly(this.nettyClientConfig.getConnectTimeoutMillis())) {
            if (cw.isOK()) {
                log.info("createChannel: connect remote host[{}] success, {}", addr, channelFuture.toString());
                return cw.getChannel();
            } else {
                log.warn("createChannel: connect remote host[{}] failed, {}", addr, channelFuture.toString());
            }
        } else {
            log.warn("createChannel: connect remote host[{}] timeout {}ms, {}", addr, this.nettyClientConfig.getConnectTimeoutMillis(),
                    channelFuture.toString());
        }
        return null;
    }

    public void closeChannel(final String addr, final Channel channel) {
        if (null == channel) {
            return;
        }

        final String addrRemote = null == addr ? RemotingHelper.parseChannelRemoteAddr(channel) : addr;

        try {
            if (this.lockChannelTables.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                try {
                    boolean removeItemFromTable = true;
                    final ChannelWrapper prevCW = this.channelTables.get(addrRemote);

                    log.info("closeChannel: begin close the channel[{}] Found: {}", addrRemote, prevCW != null);

                    if (null == prevCW) {
                        log.info("closeChannel: the channel[{}] has been removed from the channel table before", addrRemote);
                        removeItemFromTable = false;
                    } else if (prevCW.getChannel() != channel) {
                        log.info("closeChannel: the channel[{}] has been closed before, and has been created again, nothing to do.",
                                addrRemote);
                        removeItemFromTable = false;
                    }

                    if (removeItemFromTable) {
                        ChannelWrapper channelWrapper = this.channelWrapperTables.remove(channel);
                        if (channelWrapper != null && channelWrapper.tryClose(channel)) {
                            this.channelTables.remove(addrRemote);
                        }
                        log.info("closeChannel: the channel[{}] was removed from channel table", addrRemote);
                    }

                    RemotingHelper.closeChannel(channel);
                } catch (Exception e) {
                    log.error("closeChannel: close the channel exception", e);
                } finally {
                    this.lockChannelTables.unlock();
                }
            } else {
                log.warn("closeChannel: try to lock channel table, but timeout, {}ms", LOCK_TIMEOUT_MILLIS);
            }
        } catch (InterruptedException e) {
            log.error("closeChannel exception", e);
        }
    }

    public void closeChannel(final Channel channel) {
        if (null == channel) {
            return;
        }

        try {
            if (this.lockChannelTables.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                try {
                    boolean removeItemFromTable = true;
                    ChannelWrapper prevCW = null;
                    String addrRemote = null;
                    for (Map.Entry<String, ChannelWrapper> entry : channelTables.entrySet()) {
                        String key = entry.getKey();
                        ChannelWrapper prev = entry.getValue();
                        if (prev.getChannel() != null) {
                            if (prev.getChannel() == channel) {
                                prevCW = prev;
                                addrRemote = key;
                                break;
                            }
                        }
                    }

                    if (null == prevCW) {
                        log.info("eventCloseChannel: the channel[{}] has been removed from the channel table before", addrRemote);
                        removeItemFromTable = false;
                    }

                    if (removeItemFromTable) {
                        ChannelWrapper channelWrapper = this.channelWrapperTables.remove(channel);
                        if (channelWrapper != null && channelWrapper.tryClose(channel)) {
                            this.channelTables.remove(addrRemote);
                        }
                        log.info("closeChannel: the channel[{}] was removed from channel table", addrRemote);
                        RemotingHelper.closeChannel(channel);
                    }
                } catch (Exception e) {
                    log.error("closeChannel: close the channel exception", e);
                } finally {
                    this.lockChannelTables.unlock();
                }
            } else {
                log.warn("closeChannel: try to lock channel table, but timeout, {}ms", LOCK_TIMEOUT_MILLIS);
            }
        } catch (InterruptedException e) {
            log.error("closeChannel exception", e);
        }
    }

    public CompletableFuture<ResponseFuture> invokeImpl(final Channel channel, final RemotingCommand request,
                                                        final long timeoutMillis) {
        long startTime = System.currentTimeMillis();
        //Stopwatch stopwatch = Stopwatch.createStarted();
        return super.invokeImpl(channel, request, timeoutMillis).thenCompose(responseFuture -> {
            RemotingCommand response = responseFuture.getResponseCommand();
            if (response.getCode() == ResponseCode.GO_AWAY) {   //连接断线后处理
                //如果允许重连，就调用 Bootstrap#connect() 方法重新连接
                if (nettyClientConfig.isEnableReconnectForGoAway()) {
                    ChannelWrapper channelWrapper = channelWrapperTables.computeIfPresent(channel, (channel0, channelWrapper0) -> {
                        try {
                            if (channelWrapper0.reconnect()) {
                                log.info("Receive go away from channel {}, recreate the channel", channel0);
                                channelWrapperTables.put(channelWrapper0.getChannel(), channelWrapper0);
                            }
                        } catch (Throwable t) {
                            log.error("Channel {} reconnect error", channelWrapper0, t);
                        }
                        return channelWrapper0;
                    });
                    if (channelWrapper != null) {
                        // 如果允许重试
                        if (nettyClientConfig.isEnableTransparentRetry()) {
                            //long duration = stopwatch.elapsed(TimeUnit.MILLISECONDS);
                            //stopwatch.stop();
                            long duration = System.currentTimeMillis() - startTime;
                            RemotingCommand retryRequest = RemotingCommand.createRequestCommand(request.getCode());
                            retryRequest.setBody(request.getBody());
                            Channel retryChannel;
                            if (channelWrapper.isOK()) {
                                retryChannel = channelWrapper.getChannel();
                            } else {
                                retryChannel = waitChannelFuture(channelWrapper.getChannelAddress(), channelWrapper);
                            }
                            if (retryChannel != null && channel != retryChannel) {
                                return super.invokeImpl(retryChannel, retryRequest, timeoutMillis - duration);
                            }
                        }
                    }
                }
            }
            return CompletableFuture.completedFuture(responseFuture);
        });
    }

    /**
     * 异步调用，响应通过 InvokeCallback 回调处理
     * 前面的 invoke() 方法既可以用于同步调用也可以用于异步调用
     * @param addr 对端Server地址
     * @param request 请求命令对象
     * @param timeoutMillis 请求超时对象
     * @param invokeCallback 响应回调处理
     */
    @Override
    public void invokeAsync(String addr, RemotingCommand request, long timeoutMillis, InvokeCallback invokeCallback)
            throws InterruptedException, RemotingConnectException, RemotingTooMuchRequestException, RemotingTimeoutException,
            RemotingSendRequestException {
        long beginStartTime = System.currentTimeMillis();
        final Channel channel = this.getAndCreateChannel(addr);
        String channelRemoteAddr = RemotingHelper.parseChannelRemoteAddr(channel);
        if (channel != null && channel.isActive()) {
            long costTime = System.currentTimeMillis() - beginStartTime;
            if (timeoutMillis < costTime) {
                throw new RemotingTooMuchRequestException("invokeAsync call the addr[" + channelRemoteAddr + "] timeout");
            }
            this.invokeAsyncImpl(channel, request, timeoutMillis - costTime, new InvokeCallbackWrapper(invokeCallback, addr));
        } else {
            this.closeChannel(addr, channel);
            throw new RemotingConnectException(addr);
        }
    }

    public void invokeAsyncImpl(final Channel channel, final RemotingCommand request, final long timeoutMillis,
                                final InvokeCallback invokeCallback) {
        invokeImpl(channel, request, timeoutMillis)
                .whenComplete((v, t) -> {
                    if (t == null) {
                        invokeCallback.operationComplete(v);
                    } else {
                        ResponseFuture responseFuture = new ResponseFuture(channel, request.getOpaque(), request, timeoutMillis, null, null);
                        responseFuture.setCause(t);
                        invokeCallback.operationComplete(responseFuture);
                    }
                })
                .thenAccept(responseFuture -> invokeCallback.operationSucceed(responseFuture.getResponseCommand()))
                .exceptionally(t -> {
                    invokeCallback.operationFail(t);
                    return null;
                });
    }

    @Override
    public ExecutorService getCallbackExecutor() {
        if (nettyClientConfig.isDisableCallbackExecutor()) {
            return null;
        }
        return callbackExecutor != null ? callbackExecutor : publicExecutor;
    }

    public void setCallbackExecutor(ExecutorService callbackExecutor) {
        this.callbackExecutor = callbackExecutor;
    }

    private String[] getHostAndPort(String address) {
        int split = address.lastIndexOf(":");
        return split < 0 ? new String[]{address} : new String[]{address.substring(0, split), address.substring(split + 1)};
    }

    class NettyClientHandler extends SimpleChannelInboundHandler<RemotingCommand> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RemotingCommand msg) throws Exception {
            processMessageReceived(ctx, msg);
        }
    }

    class ChannelWrapper {
        //
        private final ReentrantReadWriteLock lock;
        private ChannelFuture channelFuture;
        private ChannelFuture channelToClose;
        // 记录最后一次响应时间
        private long lastResponseTime;
        private volatile long lastReconnectTimestamp = 0L;
        // Channel连接的对端地址，包括 ip 和 端口, 格式：ip:port
        private final String channelAddress;

        public ChannelWrapper(String address, ChannelFuture channelFuture) {
            this.lock = new ReentrantReadWriteLock();
            this.channelFuture = channelFuture;
            this.lastResponseTime = System.currentTimeMillis();
            this.channelAddress = address;
        }

        public boolean isOK() {
            return getChannel() != null && getChannel().isActive();
        }

        public boolean isWritable() {
            return getChannel().isWritable();
        }

        private Channel getChannel() {
            return getChannelFuture().channel();
        }

        public ChannelFuture getChannelFuture() {
            lock.readLock().lock();
            try {
                return this.channelFuture;
            } finally {
                lock.readLock().unlock();
            }
        }

        public long getLastResponseTime() {
            return this.lastResponseTime;
        }

        public void updateLastResponseTime() {
            this.lastResponseTime = System.currentTimeMillis();
        }

        public String getChannelAddress() {
            return channelAddress;
        }

        public boolean reconnect() {
            if (lock.writeLock().tryLock()) {
                try {
                    // 最大重试间隔，超过该值，不再重连
                    if (lastReconnectTimestamp == 0L || System.currentTimeMillis() - lastReconnectTimestamp >
                            Duration.ofSeconds(nettyClientConfig.getMaxReconnectIntervalTimeSeconds()).toMillis()) {
                        channelToClose = channelFuture;
                        String[] hostAndPort = getHostAndPort(channelAddress);
                        channelFuture = fetchBootstrap(channelAddress)
                                .connect(hostAndPort[0], Integer.parseInt(hostAndPort[1]));
                        lastReconnectTimestamp = System.currentTimeMillis();
                        return true;
                    }
                } finally {
                    lock.writeLock().unlock();
                }
            }
            return false;
        }

        public boolean tryClose(Channel channel) {
            try {
                lock.readLock().lock();
                if (channelFuture != null) {
                    if (channelFuture.channel().equals(channel)) {
                        return true;
                    }
                }
            } finally {
                lock.readLock().unlock();
            }
            return false;
        }

        public void close() {
            try {
                lock.writeLock().lock();
                if (channelFuture != null) {
                    closeChannel(channelFuture.channel());
                }
                if (channelToClose != null) {
                    closeChannel(channelToClose.channel());
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    class InvokeCallbackWrapper implements InvokeCallback {

        private final InvokeCallback invokeCallback;
        private final String addr;

        public InvokeCallbackWrapper(InvokeCallback invokeCallback, String addr) {
            this.invokeCallback = invokeCallback;
            this.addr = addr;
        }

        @Override
        public void operationComplete(ResponseFuture responseFuture) {
            this.invokeCallback.operationComplete(responseFuture);
        }

        @Override
        public void operationSucceed(RemotingCommand response) {
            updateChannelLastResponseTime(addr);
            this.invokeCallback.operationSucceed(response);
        }

        @Override
        public void operationFail(final Throwable throwable) {
            this.invokeCallback.operationFail(throwable);
        }
    }
}
