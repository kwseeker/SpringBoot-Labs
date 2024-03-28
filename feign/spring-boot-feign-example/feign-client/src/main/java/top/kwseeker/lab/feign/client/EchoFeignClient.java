package top.kwseeker.lab.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "feign-server")
public interface EchoFeignClient {

    @GetMapping("/echo/{name}")
    String echo(@PathVariable("name") String name);
}
