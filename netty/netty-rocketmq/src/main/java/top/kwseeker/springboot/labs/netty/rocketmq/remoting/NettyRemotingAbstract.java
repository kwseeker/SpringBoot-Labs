package top.kwseeker.springboot.labs.netty.rocketmq.remoting;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.springboot.labs.netty.rocketmq.common.RemotingHelper;
import top.kwseeker.springboot.labs.netty.rocketmq.common.SemaphoreReleaseOnlyOnce;
import top.kwseeker.springboot.labs.netty.rocketmq.common.constant.LoggerName;
import top.kwseeker.springboot.labs.netty.rocketmq.common.exception.RemotingSendRequestException;
import top.kwseeker.springboot.labs.netty.rocketmq.common.exception.RemotingTimeoutException;
import top.kwseeker.springboot.labs.netty.rocketmq.common.exception.RemotingTooMuchRequestException;
import top.kwseeker.springboot.labs.netty.rocketmq.protocol.RemotingCommand;
import top.kwseeker.springboot.labs.netty.rocketmq.protocol.RemotingSysResponseCode;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public abstract class NettyRemotingAbstract {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.ROCKETMQ_REMOTING_NAME);

    // 信号量限流
    protected final Semaphore semaphoreAsync;

    // 存储当前节点向对端发出的请求的 Future 对象，如果请求超时未响应会直接失败处理（参考 scanResponseTable）并回调失败处理
    // 请求ID opaque -> ResponseFuture
    protected final ConcurrentMap<Integer /* opaque */, ResponseFuture> responseTable =
            new ConcurrentHashMap<>(256);

    public NettyRemotingAbstract(final int permitsAsync) {
        this.semaphoreAsync = new Semaphore(permitsAsync, true);
    }

    public void processMessageReceived(ChannelHandlerContext ctx, RemotingCommand msg) {
        if (msg != null) {
            switch (msg.getType()) {
                case REQUEST_COMMAND:
                    processRequestCommand(ctx, msg);
                    break;
                case RESPONSE_COMMAND:
                    processResponseCommand(ctx, msg);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 处理对方的请求
     * @param cmd 请求解码后的 RemotingCommand
     */
    public void processRequestCommand(final ChannelHandlerContext ctx, final RemotingCommand cmd) {
        log.info("process REQUEST_COMMAND, cmd = {}", cmd.toString());
        // RocketMQ 中这一步会先检查服务器是否关闭、是否服务器太忙处理不过来，
        // 检查没有问题会将命令分发给对应的 Processor 处理，每一个命令都有对应的 Processor 和 线程池，会创建任务提交给线程池执行
        //final RequestTask requestTask = new RequestTask(run, ctx.channel(), cmd);
        //pair.getObject2().submit(requestTask);
        final int opaque = cmd.getOpaque();

        // 但是命令的处理不是这里研究的目标，这里我们直接响应
        //final RemotingCommand response = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SYSTEM_BUSY,
        //        "[OVERLOAD]system busy, start flow control for a while");
        final RemotingCommand response = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS,
                "处理完成");
        response.setOpaque(opaque);
        writeResponse(ctx.channel(), cmd, response);
    }

    /**
     * 处理对方的响应
     * @param cmd 对方的响应解码后的 RemotingCommand
     */
    public void processResponseCommand(final ChannelHandlerContext ctx, final RemotingCommand cmd) {
        log.info("process RESPONSE_COMMAND");
        final int opaque = cmd.getOpaque();
        final ResponseFuture responseFuture = responseTable.get(opaque);
        if (responseFuture != null) {
            responseFuture.setResponseCommand(cmd);
            responseTable.remove(opaque);   //清理掉避免被超时定时处理大的定时任务重复处理

            if (responseFuture.getInvokeCallback() != null) {
                //执行响应处理回调
                executeInvokeCallback(responseFuture);
            } else {
                responseFuture.putResponse(cmd);
                responseFuture.release();
            }
        } else {    //出现这种情况基本是响应超时，ResponseFuture对象已经被超时处理定时任务给清理了
            log.warn("receive response, cmd={}, but not matched any request, address={}",
                    cmd, RemotingHelper.parseChannelRemoteAddr(ctx.channel()));
        }
    }

    private void executeInvokeCallback(final ResponseFuture responseFuture) {
        boolean runInThisThread = false;
        ExecutorService executor = this.getCallbackExecutor();
        if (executor != null && !executor.isShutdown()) {
            try {
                executor.submit(() -> {
                    try {
                        responseFuture.executeInvokeCallback();
                    } catch (Throwable e) {
                        log.warn("execute callback in executor exception, and callback throw", e);
                    } finally {
                        responseFuture.release();
                    }
                });
            } catch (Exception e) {
                runInThisThread = true;
                log.warn("execute callback in executor exception, maybe executor busy", e);
            }
        } else {
            runInThisThread = true;
        }

        if (runInThisThread) {
            try {
                responseFuture.executeInvokeCallback();
            } catch (Throwable e) {
                log.warn("executeInvokeCallback Exception", e);
            } finally {
                responseFuture.release();
            }
        }
    }

    public void scanResponseTable() {
        final List<ResponseFuture> rfList = new LinkedList<>();
        Iterator<Map.Entry<Integer, ResponseFuture>> it = this.responseTable.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ResponseFuture> next = it.next();
            ResponseFuture rep = next.getValue();

            if ((rep.getBeginTimestamp() + rep.getTimeoutMillis() + 1000) <= System.currentTimeMillis()) {
                rep.release();
                it.remove();
                rfList.add(rep);
                log.warn("remove timeout request, " + rep);
            }
        }

        for (ResponseFuture rf : rfList) {
            try {
                executeInvokeCallback(rf);
            } catch (Throwable e) {
                log.warn("scanResponseTable, operationComplete Exception", e);
            }
        }
    }

    public abstract ExecutorService getCallbackExecutor();

    /**
     * 写响应，响应数据也封装为 RemotingCommand，先序列化再编码最后发送
     * @param request 请求经过解码反序列化后的数据对象
     * @param response 要响应的数据对象
     */
    public static void writeResponse(Channel channel, RemotingCommand request, RemotingCommand response) {
        writeResponse(channel, request, response, null);
    }

    public static void writeResponse(Channel channel, RemotingCommand request, RemotingCommand response,
                                     Consumer<Future<?>> callback) {
        if (response == null) {
            return;
        }
        // RocketMQ 使用 OpenTelemetry 做链路追踪和监控，这里不需要先忽略掉
        //AttributesBuilder attributesBuilder = RemotingMetricsManager.newAttributesBuilder()
        //        .put(LABEL_IS_LONG_POLLING, request.isSuspended())
        //        .put(LABEL_REQUEST_CODE, RemotingHelper.getRequestCodeDesc(request.getCode()))
        //        .put(LABEL_RESPONSE_CODE, RemotingHelper.getResponseCodeDesc(response.getCode()));
        //if (request.isOnewayRPC()) {
        //    attributesBuilder.put(LABEL_RESULT, RESULT_ONEWAY);
        //    RemotingMetricsManager.rpcLatency.record(request.getProcessTimer().elapsed(TimeUnit.MILLISECONDS), attributesBuilder.build());
        //    return;
        //}

        response.setOpaque(request.getOpaque());
        response.markResponseType();
        try {
            channel.writeAndFlush(response).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.debug("Response[request code: {}, response code: {}, opaque: {}] is written to channel{}",
                            request.getCode(), response.getCode(), response.getOpaque(), channel);
                } else {
                    log.error("Failed to write response[request code: {}, response code: {}, opaque: {}] to channel{}",
                            request.getCode(), response.getCode(), response.getOpaque(), channel, future.cause());
                }
                //attributesBuilder.put(LABEL_RESULT, RemotingMetricsManager.getWriteAndFlushResult(future));
                //RemotingMetricsManager.rpcLatency.record(request.getProcessTimer().elapsed(TimeUnit.MILLISECONDS), attributesBuilder.build());
                if (callback != null) {
                    callback.accept(future);
                }
            });
        } catch (Throwable e) {
            log.error("process request over, but response failed", e);
            log.error(request.toString());
            log.error(response.toString());
            //attributesBuilder.put(LABEL_RESULT, RESULT_WRITE_CHANNEL_FAILED);
            //RemotingMetricsManager.rpcLatency.record(request.getProcessTimer().elapsed(TimeUnit.MILLISECONDS), attributesBuilder.build());
        }
    }

    /**
     * 发送请求的实现
     * @param channel 连接通道
     * @param request 请求命令对象
     * @param timeoutMillis 请求超时时间
     */
    public CompletableFuture<ResponseFuture> invokeImpl(final Channel channel, final RemotingCommand request,
                                                        final long timeoutMillis) {
        String channelRemoteAddr = RemotingHelper.parseChannelRemoteAddr(channel);
        //doBeforeRpcHooks(channelRemoteAddr, request);
        //return invoke0(channel, request, timeoutMillis).whenComplete((v, t) -> {
        //    if (t == null) {
        //        doAfterRpcHooks(channelRemoteAddr, request, v.getResponseCommand());
        //    }
        //});
        return invoke0(channel, request, timeoutMillis);
    }

    protected CompletableFuture<ResponseFuture> invoke0(final Channel channel, final RemotingCommand request,
                                                        final long timeoutMillis) {
        CompletableFuture<ResponseFuture> future = new CompletableFuture<>();
        long beginStartTime = System.currentTimeMillis();
        final int opaque = request.getOpaque();

        // 信号量限流
        boolean acquired;
        try {
            acquired = this.semaphoreAsync.tryAcquire(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (Throwable t) {
            future.completeExceptionally(t);
            return future;
        }
        if (acquired) {
            // 用于释放获取的信号量，这里为何要使用 SemaphoreReleaseOnlyOnce？什么情况下有可能释放多次？
            final SemaphoreReleaseOnlyOnce once = new SemaphoreReleaseOnlyOnce(this.semaphoreAsync);
            long costTime = System.currentTimeMillis() - beginStartTime;
            // 超时直接异常结束
            if (timeoutMillis < costTime) {
                once.release(); //释放信号量
                future.completeExceptionally(new RemotingTimeoutException("invokeAsyncImpl call timeout"));
                return future;
            }

            AtomicReference<ResponseFuture> responseFutureReference = new AtomicReference<>();
            final ResponseFuture responseFuture = new ResponseFuture(channel, opaque, request, timeoutMillis - costTime,
                    new InvokeCallback() {
                        @Override
                        public void operationComplete(ResponseFuture responseFuture) {

                        }

                        @Override
                        public void operationSucceed(RemotingCommand response) {
                            future.complete(responseFutureReference.get());
                        }

                        @Override
                        public void operationFail(Throwable throwable) {
                            future.completeExceptionally(throwable);
                        }
                    }, once);
            responseFutureReference.set(responseFuture);
            // 请求缓存到 responseTable, 用于防止超时的扫描任务
            this.responseTable.put(opaque, responseFuture);
            try {
                channel.writeAndFlush(request).addListener((ChannelFutureListener) f -> {
                    if (f.isSuccess()) {
                        responseFuture.setSendRequestOK(true);
                        return;
                    }
                    //消息发送失败的异常处理，主要是回调前面注册的 InvokeCallback 方法
                    requestFail(opaque);
                    log.warn("send a request command to channel <{}> failed.", RemotingHelper.parseChannelRemoteAddr(channel));
                });
                return future;
            } catch (Exception e) {
                responseTable.remove(opaque);
                responseFuture.release();
                log.warn("send a request command to channel <" + RemotingHelper.parseChannelRemoteAddr(channel) + "> Exception", e);
                future.completeExceptionally(new RemotingSendRequestException(RemotingHelper.parseChannelRemoteAddr(channel), e));
                return future;
            }
        } else {
            if (timeoutMillis <= 0) {
                future.completeExceptionally(new RemotingTooMuchRequestException("invokeAsyncImpl invoke too fast"));
            } else {
                String info =
                        String.format("invokeAsyncImpl tryAcquire semaphore timeout, %dms, waiting thread nums: %d semaphoreAsyncValue: %d",
                                timeoutMillis,
                                this.semaphoreAsync.getQueueLength(),
                                this.semaphoreAsync.availablePermits()
                        );
                log.warn(info);
                future.completeExceptionally(new RemotingTimeoutException(info));
            }
            return future;
        }
    }

    private void requestFail(final int opaque) {
        ResponseFuture responseFuture = responseTable.remove(opaque);
        if (responseFuture != null) {
            responseFuture.setSendRequestOK(false);
            responseFuture.putResponse(null);
            try {
                executeInvokeCallback(responseFuture);
            } catch (Throwable e) {
                log.warn("execute callback in requestFail, and callback throw", e);
            } finally {
                responseFuture.release();
            }
        }
    }
}
