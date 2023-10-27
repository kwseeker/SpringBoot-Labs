package cn.iocoder.springcloudnetflix.labx02.ribbondemo.consumer.config;

import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import(BeanLoadTestConfiguration.class)
@LoadBalancerClients(defaultConfiguration = MyLoadBalancerConfiguration.class)
public class MyLoadBalancerAutoConfiguration {
}
