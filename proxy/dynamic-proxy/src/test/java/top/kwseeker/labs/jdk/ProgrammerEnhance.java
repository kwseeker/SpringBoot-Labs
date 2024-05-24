package top.kwseeker.labs.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ProgrammerEnhance implements InvocationHandler {

    private Object target;

    public ProgrammerEnhance() {
    }

    public ProgrammerEnhance(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("work")) {
            System.out.println("do programming");
            return null;
        }
        return method.invoke(target, args);
    }
}
