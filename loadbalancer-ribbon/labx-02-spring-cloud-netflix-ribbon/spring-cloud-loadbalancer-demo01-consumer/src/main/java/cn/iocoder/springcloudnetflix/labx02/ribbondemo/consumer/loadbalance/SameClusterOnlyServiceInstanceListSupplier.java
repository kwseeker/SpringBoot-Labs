package cn.iocoder.springcloudnetflix.labx02.ribbondemo.consumer.loadbalance;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.DelegatingServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SameClusterOnlyServiceInstanceListSupplier extends DelegatingServiceInstanceListSupplier {

    //服务实例ServiceInstance 元数据 metadata 中表示集群信息的 key
    private static final String META_CLUSTER = "nacos.cluster";
    private static final String NACOS_NATIVE_CLUSTER_NAME = "spring.cloud.nacos.discovery.cluster-name";

    private Environment env;

    public SameClusterOnlyServiceInstanceListSupplier(ServiceInstanceListSupplier delegate, Environment env) {
        super(delegate);
        this.env = env;
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        return getDelegate().get().map(this::filteredByZone);
    }

    private List<ServiceInstance> filteredByZone(List<ServiceInstance> serviceInstances) {
        String zone = env.getProperty(NACOS_NATIVE_CLUSTER_NAME);
        if (!StringUtils.hasText(zone)) {
            //return Collections.emptyList();
            return serviceInstances;
        }

        List<ServiceInstance> filteredInstances = new ArrayList<>();
        for (ServiceInstance serviceInstance : serviceInstances) {
            String instanceZone = getZone(serviceInstance);
            assert zone != null;
            if (zone.equalsIgnoreCase(instanceZone)) {
                filteredInstances.add(serviceInstance);
            }
        }

        return filteredInstances.size() > 0 ? filteredInstances : serviceInstances;
    }

    //读取实例的 zone，没有配置则为 null
    private String getZone(ServiceInstance serviceInstance) {
        Map<String, String> metadata = serviceInstance.getMetadata();
        if (metadata == null) {
            return null;
        }
        return metadata.get(META_CLUSTER);
    }
}