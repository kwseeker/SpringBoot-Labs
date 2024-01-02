package top.kwseeker.spring.basic.beans.circular;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
//@EnableAspectJAutoProxy
public class Application {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Application.class, args);

        ServiceA serviceA = context.getBean("serviceA", ServiceA.class);
        serviceA.callB();
        ServiceB serviceB = context.getBean("serviceB", ServiceB.class);
        serviceB.callA();
    }
}
