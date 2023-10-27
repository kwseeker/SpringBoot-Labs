package cn.iocoder.springcloudnetflix.labx02.ribbondemo.consumer.loadbalance;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 为了解决原始算法多个调用并发可能导致一个请求失败重试到相同的实例
 * 比如一共两个实例，请求A请求实例1失败(position=0)，然后请求B请求实例2(position=1), 然后请求A重试结果position还是0还是请求的有问题的实例
 * 解决方法：给每个请求维护一个单独position计数，起始值是 requestId % serviceInstanceSize，不过这还是轮询么，感觉也可能是随机，由 sleuth requestId 生成规则决定
 * 不过这个需要借助 sleuth 获取请求ID, 这里先暂时改回去
 */
public class RoundRobinWithRequestSeparatedPositionLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    private ServiceInstanceListSupplier serviceInstanceListSupplier;

    //每次请求算上重试不会超过1分钟，对于超过1分钟的，这种请求肯定比较重，不应该重试
    private final LoadingCache<Long, AtomicInteger> positionCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            //随机初始值，防止每次都是从第一个开始调用
            .build(k -> new AtomicInteger(ThreadLocalRandom.current().nextInt(0, 1000)));
    private final String serviceId;
    //private final Tracer tracer;
    //暂时改回去
    private AtomicInteger position = new AtomicInteger(0);

    public RoundRobinWithRequestSeparatedPositionLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                                                              String serviceId) {
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        this.serviceId = serviceId;
        //this.tracer = tracer;
    }

    //public RoundRobinWithRequestSeparatedPositionLoadBalancer(ServiceInstanceListSupplier serviceInstanceListSupplier, String serviceId, Tracer tracer) {
    public RoundRobinWithRequestSeparatedPositionLoadBalancer(ServiceInstanceListSupplier serviceInstanceListSupplier, String serviceId) {
        this.serviceInstanceListSupplier = serviceInstanceListSupplier;
        this.serviceId = serviceId;
        //this.tracer = tracer;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        if (serviceInstanceListSupplier == null) {
            serviceInstanceListSupplier = serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new);
        }
        return serviceInstanceListSupplier.get().next().map(serviceInstances -> getInstanceResponse(serviceInstances));
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> serviceInstances) {
        if (serviceInstances.isEmpty()) {
            System.out.println("No servers available for service: " + this.serviceId);
            return new EmptyResponse();
        }
        return getInstanceResponseByRoundRobin(serviceInstances);
    }

    private Response<ServiceInstance> getInstanceResponseByRoundRobin(List<ServiceInstance> serviceInstances) {
        if (serviceInstances.isEmpty()) {
            System.out.println("No servers available for service: " + this.serviceId);
            return new EmptyResponse();
        }
        ////为了解决原始算法不同调用并发可能导致一个请求重试相同的实例
        //Span currentSpan = tracer.currentSpan();
        //if (currentSpan == null) {
        //    currentSpan = tracer.newTrace();
        //}
        //long l = currentSpan.context().traceId();
        //AtomicInteger seed = positionCache.get(l);
        //int s = seed.getAndIncrement();
        //int pos = s % serviceInstances.size();
        //System.out.printf("position %d, seed: %d, instances count: %d", pos, s, serviceInstances.size());
        //return new DefaultResponse(serviceInstances.stream()
        //        //实例返回列表顺序可能不同，为了保持一致，先排序再取
        //        .sorted(Comparator.comparing(ServiceInstance::getInstanceId))
        //        .collect(Collectors.toList()).get(pos));
        int pos = position.getAndIncrement() % serviceInstances.size();
        return new DefaultResponse(serviceInstances.stream()
                        //实例返回列表顺序可能不同，为了保持一致，先排序再取
                        .sorted(Comparator.comparing(ServiceInstance::getInstanceId))
                        .collect(Collectors.toList()).get(pos));
    }
}