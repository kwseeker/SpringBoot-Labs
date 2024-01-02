package top.kwseeker.spring.basic.beans.factorybean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.kwseeker.spring.basic.beans.factorybean.pojo.Person;

@Configuration
public class CarConfiguration {

    @Bean
    public CarFactoryBean carFactoryBean(){
        CarFactoryBean cfb = new CarFactoryBean();
        cfb.setMake("Honda");
        cfb.setYear(1984);
        return cfb;
    }

    //@Bean
    //public Person aPerson(){
    //    Person person = new Person();
    //    person.setCar(carFactoryBean().getObject());
    //    return person;
    //}
}
