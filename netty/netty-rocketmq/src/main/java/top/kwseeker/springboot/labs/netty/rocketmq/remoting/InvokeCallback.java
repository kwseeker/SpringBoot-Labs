package top.kwseeker.springboot.labs.netty.rocketmq.remoting;

import top.kwseeker.springboot.labs.netty.rocketmq.protocol.RemotingCommand;

public interface InvokeCallback {
    /**
     * This method is expected to be invoked after {@link #operationSucceed(RemotingCommand)}
     * or {@link #operationFail(Throwable)}
     *
     * @param responseFuture the returned object contains response or exception
     */
    void operationComplete(final ResponseFuture responseFuture);

    default void operationSucceed(final RemotingCommand response) {
    }

    default void operationFail(final Throwable throwable) {
    }
}