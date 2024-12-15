package top.kwseeker.springboot.labs.netty.rocketmq.remoting;

import io.netty.channel.Channel;
import top.kwseeker.springboot.labs.netty.rocketmq.common.SemaphoreReleaseOnlyOnce;
import top.kwseeker.springboot.labs.netty.rocketmq.common.exception.RemotingException;
import top.kwseeker.springboot.labs.netty.rocketmq.common.exception.RemotingSendRequestException;
import top.kwseeker.springboot.labs.netty.rocketmq.common.exception.RemotingTimeoutException;
import top.kwseeker.springboot.labs.netty.rocketmq.protocol.RemotingCommand;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

// 当前 Netty 实例向对端发送请求后的 Future 对象
public class ResponseFuture {

    //请求使用的通道
    private final Channel channel;
    //请求ID
    private final int opaque;
    //请求命令对象
    private final RemotingCommand request;
    //请求超时时间，比如 1000ms
    private final long timeoutMillis;
    //成功或失败后回调，请求处理超时后会快速失败会回调operationFail方法
    private final InvokeCallback invokeCallback;
    private final SemaphoreReleaseOnlyOnce once;

    //ResponseFuture对象创建时间，用于计算请求处理时长
    private final long beginTimestamp = System.currentTimeMillis();
    //对端的响应命令对象
    private volatile RemotingCommand responseCommand;
    //保证回调只执行一次，从处理流程看，还是可能出现并发
    private final AtomicBoolean executeCallbackOnlyOnce = new AtomicBoolean(false);
    //是否发送请求成功
    private volatile boolean sendRequestOK = true;
    //请求发送失败的原因
    private volatile Throwable cause;
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    public ResponseFuture(Channel channel, int opaque, long timeoutMillis, InvokeCallback invokeCallback) {
        this(channel, opaque, null, timeoutMillis, invokeCallback);
    }

    public ResponseFuture(Channel channel, int opaque, RemotingCommand request, long timeoutMillis,
                          InvokeCallback invokeCallback) {
        this(channel, opaque, request, timeoutMillis, invokeCallback, null);
    }

    public ResponseFuture(Channel channel, int opaque, RemotingCommand request, long timeoutMillis,
                          InvokeCallback invokeCallback, SemaphoreReleaseOnlyOnce once) {
        this.channel = channel;
        this.opaque = opaque;
        this.request = request;
        this.timeoutMillis = timeoutMillis;
        this.invokeCallback = invokeCallback;
        this.once = once;
    }



    public void executeInvokeCallback() {
        if (invokeCallback != null) {
            if (this.executeCallbackOnlyOnce.compareAndSet(false, true)) {
                RemotingCommand response = getResponseCommand();
                if (response != null) {
                    invokeCallback.operationSucceed(response);
                } else {
                    if (!isSendRequestOK()) {
                        invokeCallback.operationFail(
                                new RemotingSendRequestException(channel.remoteAddress().toString(), getCause()));
                    } else if (isTimeout()) {
                        invokeCallback.operationFail(
                                new RemotingTimeoutException(channel.remoteAddress().toString(), getTimeoutMillis(), getCause()));
                    } else {
                        invokeCallback.operationFail(
                                new RemotingException(getRequestCommand().toString(), getCause()));
                    }
                }
                invokeCallback.operationComplete(this);
            }
        }
    }

    public boolean isTimeout() {
        long diff = System.currentTimeMillis() - this.beginTimestamp;
        return diff > this.timeoutMillis;
    }

    public RemotingCommand waitResponse(final long timeoutMillis) throws InterruptedException {
        this.countDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS);
        return this.responseCommand;
    }

    public RemotingCommand getRequestCommand() {
        return request;
    }

    public void putResponse(final RemotingCommand responseCommand) {
        this.responseCommand = responseCommand;
        this.countDownLatch.countDown();
    }

    public void release() {
        //if (this.once != null) {
        //    this.once.release();
        //}
    }

    public Channel getChannel() {
        return channel;
    }

    public int getOpaque() {
        return opaque;
    }

    public RemotingCommand getRequest() {
        return request;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public long getBeginTimestamp() {
        return beginTimestamp;
    }

    public InvokeCallback getInvokeCallback() {
        return invokeCallback;
    }

    public RemotingCommand getResponseCommand() {
        return responseCommand;
    }

    public void setResponseCommand(RemotingCommand responseCommand) {
        this.responseCommand = responseCommand;
    }

    public boolean isSendRequestOK() {
        return sendRequestOK;
    }

    public void setSendRequestOK(boolean sendRequestOK) {
        this.sendRequestOK = sendRequestOK;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }
}
