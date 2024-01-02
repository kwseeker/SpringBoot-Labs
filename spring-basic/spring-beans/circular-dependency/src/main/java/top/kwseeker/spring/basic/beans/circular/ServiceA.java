package top.kwseeker.spring.basic.beans.circular;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ServiceA {

    @Resource
    //@Lazy
    private ServiceB serviceB;

    public ServiceA() {
        System.out.println("construct ServiceA");
    }

    public void callB() {
        System.out.println("ServiceA#callB(): " + serviceB.doSomething());
    }

    public String doSomething() {
        return "ServiceA doSomething ...";
    }
}
