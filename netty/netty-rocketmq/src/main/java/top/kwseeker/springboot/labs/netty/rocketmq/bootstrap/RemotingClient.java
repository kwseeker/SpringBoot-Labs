package top.kwseeker.springboot.labs.netty.rocketmq.bootstrap;

import top.kwseeker.springboot.labs.netty.rocketmq.common.exception.RemotingConnectException;
import top.kwseeker.springboot.labs.netty.rocketmq.common.exception.RemotingSendRequestException;
import top.kwseeker.springboot.labs.netty.rocketmq.common.exception.RemotingTimeoutException;
import top.kwseeker.springboot.labs.netty.rocketmq.common.exception.RemotingTooMuchRequestException;
import top.kwseeker.springboot.labs.netty.rocketmq.protocol.RemotingCommand;
import top.kwseeker.springboot.labs.netty.rocketmq.remoting.InvokeCallback;
import top.kwseeker.springboot.labs.netty.rocketmq.remoting.ResponseFuture;

import java.util.concurrent.CompletableFuture;

public interface RemotingClient extends RemotingService {

    void invokeAsync(final String addr, final RemotingCommand request, final long timeoutMillis,
                     final InvokeCallback invokeCallback) throws InterruptedException, RemotingConnectException,
            RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException;

    default CompletableFuture<RemotingCommand> invoke(final String addr, final RemotingCommand request,
                                                      final long timeoutMillis) {
        CompletableFuture<RemotingCommand> future = new CompletableFuture<>();
        try {
            invokeAsync(addr, request, timeoutMillis, new InvokeCallback() {
                @Override
                public void operationComplete(ResponseFuture responseFuture) {
                }

                @Override
                public void operationSucceed(RemotingCommand response) {
                    future.complete(response);
                }

                @Override
                public void operationFail(Throwable throwable) {
                    future.completeExceptionally(throwable);
                }
            });
        } catch (Throwable t) {
            future.completeExceptionally(t);
        }
        return future;
    }
}