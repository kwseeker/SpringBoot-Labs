package cn.iocoder.springcloudnetflix.labx02.ribbondemo.consumer.config;

import cn.iocoder.springcloudnetflix.labx02.ribbondemo.consumer.loadbalance.RoundRobinWithRequestSeparatedPositionLoadBalancer;
import cn.iocoder.springcloudnetflix.labx02.ribbondemo.consumer.loadbalance.SameClusterOnlyServiceInstanceListSupplier;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.ConditionalOnReactiveDiscoveryEnabled;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager;
import org.springframework.cloud.loadbalancer.core.CachingServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.DiscoveryClientServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.cloud.loadbalancer.support.LoadBalancerEnvironmentPropertyUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

@ConditionalOnReactiveDiscoveryEnabled
public class MyLoadBalancerConfiguration {

    private static final int REACTIVE_SERVICE_INSTANCE_SUPPLIER_ORDER = 193827465;

    //响应式实现 如果用阻塞式类似新增个Bean
    @Bean
    @ConditionalOnBean(ReactiveDiscoveryClient.class)
    @ConditionalOnMissingBean
    @Conditional(SameClusterOnlyConfigurationCondition.class)
    @Order(REACTIVE_SERVICE_INSTANCE_SUPPLIER_ORDER)
    public ServiceInstanceListSupplier sameClusterOnlyServiceInstanceListSupplier(ConfigurableApplicationContext context) {
        ReactiveDiscoveryClient discoveryClient = context.getBean(ReactiveDiscoveryClient.class);
        Environment env = context.getEnvironment();
        ObjectProvider<LoadBalancerCacheManager> cacheManagerProvider = context.getBeanProvider(LoadBalancerCacheManager.class);
        //装饰器模式
        ServiceInstanceListSupplier supplier = new DiscoveryClientServiceInstanceListSupplier(discoveryClient, env);
        supplier = new SameClusterOnlyServiceInstanceListSupplier(supplier, env);
        if (cacheManagerProvider.getIfAvailable() != null) {
            supplier = new CachingServiceInstanceListSupplier(supplier, cacheManagerProvider.getIfAvailable());
        }
        return supplier;
    }

    //@Bean
    //@ConditionalOnMissingBean
    //public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(
    //        Environment environment,
    //        LoadBalancerClientFactory loadBalancerClientFactory
    //        //Tracer tracer
    //) {
    //    System.out.println("load bean: reactorServiceInstanceLoadBalancer");
    //    String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
    //    return new RoundRobinWithRequestSeparatedPositionLoadBalancer(
    //            loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
    //            name
    //            //tracer
    //    );
    //}

    @Bean
    @ConditionalOnMissingBean
    public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(
            Environment environment,
            ServiceInstanceListSupplier serviceInstanceListSupplier
            //Tracer tracer
    ) {
        System.out.println("load bean: reactorServiceInstanceLoadBalancer");
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new RoundRobinWithRequestSeparatedPositionLoadBalancer(
                serviceInstanceListSupplier,
                name
                //tracer
        );
    }

    //测试@LoadBalancerClients引入的配置类的Bean是否会被父上下文加载
    @Bean
    public String anotherName() {
        System.out.println("load bean: anotherName");
        return "Lee";
    }

    static class SameClusterOnlyConfigurationCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return LoadBalancerEnvironmentPropertyUtils.equalToOrMissingForClientOrDefault(context.getEnvironment(),
                    "configurations", "same-cluster-only");
        }
    }
}
