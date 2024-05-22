package top.kwseeker.lab.grpc.lognet.common.client;

import io.grpc.stub.AbstractStub;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
public class GrpcClient {

    /**
     * 同步调用
     *
     * @param <T> T         返回值类型
     * @param abstractStub  RPC客户端实例（继承AbstractStub）
     * @param method String
     * @param param Object
     * @param clazz clazz
     * @return t T
     */
    public <T> T syncInvoke(final AbstractStub abstractStub, final String method, final Object param, final Class<T> clazz) {
        for (Method m : abstractStub.getClass().getMethods()) {
            if (m.getName().equals(method)) {
                try {
                    T res = (T) m.invoke(abstractStub, m.getParameterTypes()[0].cast(param));
                    System.out.println("res=" + res);
                    return res;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    System.out.println("failed to invoke grpc server");
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }
}
