package top.kwseeker.spring.basic.beans.factorybean;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(CarConfiguration.class);
        //Person aPerson = context.getBean("aPerson", Person.class);
        //System.out.println(aPerson);

        // & 用于获取 CarFactoryBean 本身
        //Object carFactoryBean = context.getBean("&carFactoryBean");
        Object aCar = context.getBean("carFactoryBean");
        System.out.println(aCar);
    }
}
