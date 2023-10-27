package cn.iocoder.springcloudnetflix.labx02.ribbondemo.consumer.config;

import org.springframework.context.annotation.Bean;

public class BeanLoadTestConfiguration {

    @Bean
    public String name() {
        System.out.println("load bean: name");
        return "Arvin";
    }
}
