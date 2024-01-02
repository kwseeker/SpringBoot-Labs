package top.kwseeker.spring.basic.beans.circular;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MyAspect {

    @Before("execution(* top.kwseeker.spring.basic.beans.circular.Service*.doSomething(..))")
    public void beforeAdvice() {
        System.out.println("Before method execution");
    }
}
