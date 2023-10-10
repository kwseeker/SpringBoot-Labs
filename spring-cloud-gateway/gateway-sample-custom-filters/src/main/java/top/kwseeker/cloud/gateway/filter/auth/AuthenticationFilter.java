package top.kwseeker.cloud.gateway.filter.auth;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 用户认证过滤器
 */
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1 从请求头提取token

        //2 使用token从缓存中获取用户信息，为空则请求用户中心用token获取用户信息并缓存

        return null;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
