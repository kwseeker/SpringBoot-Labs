package top.kwseeker.lab.grpc.lognet.common.filter;

import io.grpc.*;

public class GrpcClientFilter implements ClientInterceptor {

    public static final Metadata.Key<String> EXAM_META_DATA = Metadata.Key.of("exam-meta", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <R, P> ClientCall<R, P> interceptCall(MethodDescriptor<R, P> methodDescriptor, CallOptions callOptions, Channel channel) {
        System.out.println("执行GRPC客户端调用拦截GrpcClientFilter");
        String[] clazzNameAndMethod = methodDescriptor.getFullMethodName().split("/");
        System.out.println("class: " + clazzNameAndMethod[0]);
        System.out.println("method: " + clazzNameAndMethod[1]);

        return new ForwardingClientCall.SimpleForwardingClientCall<R, P>(channel.newCall(methodDescriptor, callOptions)) {
            @Override
            public void start(final Listener<P> responseListener, final Metadata headers) {
                System.out.println("headers = " + headers);
                headers.put(EXAM_META_DATA, "test_example_meta_data");
                //...
                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<P>(responseListener) {
                    public void onClose(final Status status, final Metadata trailers) {
                        System.out.println("response status: " + status.getCode());
                        //...
                        super.onClose(status, trailers);
                    } }, headers);
            }
        };
    }
}
