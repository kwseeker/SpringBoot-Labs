package cn.iocoder.springcloudnetflix.labx02.ribbondemo.consumer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@SpringBootApplication
public class DemoConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoConsumerApplication.class, args);
    }

    @Configuration
    public class RestTemplateConfiguration {

        @Bean
        @LoadBalanced
        @Qualifier("restTemplateWithLB")
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }

        @Bean
        @Qualifier("restTemplateWithoutLB")
        public RestTemplate restTemplateWithoutLoadBalanced() {
            return new RestTemplate();
        }
    }

    @Configuration
    //配置负载均衡使用固定的服务实例，由于引入了Nacos（Nacos有实现负载均衡规则且会自动配置加载）不再需要手动配置
    //@org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient(name = "demo-provider",
    //        configuration = SayHelloConfiguration.class)
    public class WebClientConfig {

        @LoadBalanced
        @Bean
        WebClient.Builder webClientBuilder() {
            return WebClient.builder();
        }

    }

    @RestController
    static class TestController {

        @Resource
        @Qualifier("restTemplateWithLB")
        private RestTemplate restTemplate;
        @Resource
        @Qualifier("restTemplateWithoutLB")
        private RestTemplate restTemplateWithoutLB;
        @Resource
        private LoadBalancerClient loadBalancerClient;

        @Resource
        private WebClient.Builder loadBalancedWebClientBuilder;
        @Resource
        private ReactorLoadBalancerExchangeFilterFunction lbFunction;

        @GetMapping("/hello")
        public String hello(String name) {
            // 获得服务 `demo-provider` 的一个实例
            ServiceInstance instance = loadBalancerClient.choose("demo-provider");
            // 发起调用
            String targetUrl = instance.getUri() + "/echo?name=" + name;
            //String response = restTemplate.getForObject(targetUrl, String.class); //带注解@LoadBalanced的RestTemplate会将ip当作服务名处理
            String response = restTemplateWithoutLB.getForObject(targetUrl, String.class);
            // 返回结果
            return "consumer:" + response;
        }

        @GetMapping("/hello02")
        public String hello02(String name) {
            // 直接使用 RestTemplate 调用服务 `demo-provider`
            String targetUrl = "http://demo-provider/echo?name=" + name;
            String response = restTemplate.getForObject(targetUrl, String.class);
            // 返回结果
            return "consumer:" + response;
        }

        @GetMapping("/hello03")
        public Mono<String> hello03(@RequestParam(value = "name", defaultValue = "Arvin") String name) {
            return loadBalancedWebClientBuilder.build()
                    .get().uri("http://demo-provider/echo?name=" + name)
                    .retrieve().bodyToMono(String.class)
                    .map(greeting -> String.format("%s, %s!", greeting, name));
        }

        @GetMapping("/hello04")
        public Mono<String> hello04(@RequestParam(value = "name", defaultValue = "Lee") String name) {
            return WebClient.builder()
                    .filter(lbFunction)
                    .build()
                    .get().uri("http://demo-provider/echo?name=" + name)
                    .retrieve().bodyToMono(String.class)
                    .map(greeting -> String.format("%s, %s!", greeting, name));
        }
    }

}
