package top.kwseeker.labs.cglib;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class SingerEnhance implements MethodInterceptor {

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        if (method.getName().equals("work")) {
            System.out.println("do singing");
            return null;
        }

        System.out.println("before method:" + method.getName());
        Object object = methodProxy.invokeSuper(o, args);
        System.out.println("after method return: " + object);
        return object;
    }
}
