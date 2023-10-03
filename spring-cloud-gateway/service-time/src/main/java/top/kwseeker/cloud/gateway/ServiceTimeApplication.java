package top.kwseeker.cloud.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * -Dserver.port=8092
 */
@EnableDiscoveryClient
@SpringBootApplication
public class ServiceTimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceTimeApplication.class, args);
    }
}
