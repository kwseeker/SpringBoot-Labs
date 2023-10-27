package cn.iocoder.springcloudnetflix.labx02.ribbondemo.consumer.config;

import com.alibaba.cloud.nacos.loadbalancer.NacosLoadBalancerClientConfiguration;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@LoadBalancerClients(defaultConfiguration = NacosLoadBalancerClientConfiguration.class)
public class MyLoadBalancerAutoConfiguration {
}
