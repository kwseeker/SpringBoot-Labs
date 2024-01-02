package top.kwseeker.spring.basic.beans.circular;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ServiceB {

    @Resource
    //@Lazy
    private ServiceA serviceA;

    public ServiceB() {
        System.out.println("construct ServiceB");
    }

    public void callA() {
        System.out.println("ServiceB#callA(): " + serviceA.doSomething());
    }

    public String doSomething() {
        return "ServiceB doSomething ...";
    }
}
