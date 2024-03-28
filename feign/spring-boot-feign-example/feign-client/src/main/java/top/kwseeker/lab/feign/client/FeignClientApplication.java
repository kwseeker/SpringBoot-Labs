package top.kwseeker.lab.feign.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import javax.annotation.Resource;

@EnableFeignClients
@SpringBootApplication
public class FeignClientApplication implements CommandLineRunner {

    @Resource
    private EchoFeignClient echoFeignClient;

    public static void main(String[] args) {
        SpringApplication.run(FeignClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String result = echoFeignClient.echo("Arvin");
        System.out.println("Feign server response: " + result);
    }
}
