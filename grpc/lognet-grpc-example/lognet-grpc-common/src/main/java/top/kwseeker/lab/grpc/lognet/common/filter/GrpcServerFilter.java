package top.kwseeker.lab.grpc.lognet.common.filter;

import io.grpc.*;

public class GrpcServerFilter implements ServerInterceptor {

    @Override
    public <R, P> ServerCall.Listener<R> interceptCall(ServerCall<R, P> serverCall,
                                                                 Metadata metadata,
                                                                 ServerCallHandler<R, P> serverCallHandler) {
        System.out.println("执行GRPC服务端对请求拦截GrpcServerFilter");
        System.out.println("metadata = " + metadata);
        return serverCallHandler.startCall(new ForwardingServerCall.SimpleForwardingServerCall<R, P>(serverCall) {
            @Override
            public void sendMessage(final P message) {
                //...
                System.out.println("RPC Sever response: " + message);
                super.sendMessage(message);
            }
        }, metadata);
    }
}
