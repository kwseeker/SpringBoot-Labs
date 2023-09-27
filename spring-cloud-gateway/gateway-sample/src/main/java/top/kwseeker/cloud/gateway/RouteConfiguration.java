package top.kwseeker.cloud.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfiguration {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        System.out.println("custom RouteLocator ...");
        return builder.routes()
                .route(r -> r.path("/echo/**")
                        .uri("http://localhost:8081"))
                .route(r -> r.path("/time/**")
                        .uri("http://localhost:8082"))
                .route(r -> r.path("/**")
                        .uri("http://localhost:8081"))
                .build();
    }
}
