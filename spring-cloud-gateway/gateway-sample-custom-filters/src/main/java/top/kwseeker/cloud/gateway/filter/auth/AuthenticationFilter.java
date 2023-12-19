package top.kwseeker.cloud.gateway.filter.auth;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import top.kwseeker.cloud.common.core.KeyValue;
import top.kwseeker.cloud.common.pojo.CommonResult;
import top.kwseeker.cloud.common.util.CacheUtils;
import top.kwseeker.cloud.common.util.json.JsonUtils;
import top.kwseeker.cloud.user.api.oauth2.OAuth2TokenApi;
import top.kwseeker.cloud.user.api.oauth2.dto.OAuth2AccessTokenCheckRespDTO;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;

/**
 * 用户认证过滤器
 */
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final Long DEFAULT_TENANT_ID = 1L;
    private static final LoginUser LOGIN_USER_EMPTY = new LoginUser();
    private static final TypeReference<CommonResult<OAuth2AccessTokenCheckRespDTO>> CHECK_RESULT_TYPE_REFERENCE
            = new TypeReference<CommonResult<OAuth2AccessTokenCheckRespDTO>>() {};

    //spring-webflux WebClient
    private final WebClient webClient;
    //本地缓存， TODO 替换为分布式缓存 或 换JWT
    //租户ID -> Token
    private final LoadingCache<KeyValue<Long, String>, LoginUser> loginUserCache = CacheUtils.buildAsyncReloadingCache(
            Duration.ofMinutes(1),
            new CacheLoader<KeyValue<Long, String>, LoginUser>() {
                @Override
                public LoginUser load(KeyValue<Long, String> tokenKv) {
                    String body = checkAccessToken(tokenKv.getKey(), tokenKv.getValue()).block();
                    return buildUser(body);
                }
            });


    public AuthenticationFilter(ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        // Q：为什么不使用 OAuth2TokenApi 进行调用？
        // A1：Spring Cloud OpenFeign 官方未内置 Reactive 的支持 https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/#reactive-support
        // A2：校验 Token 的 API 需要使用到 header[tenant-id] 传递租户编号，暂时不想编写 RequestInterceptor 实现
        // 因此，这里采用 WebClient，通过 lbFunction 实现负载均衡
        this.webClient = WebClient.builder().filter(lbFunction).build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1 防伪造：LOGIN_USER_HEADER 是要经过校验之后获取并放到header中的，如果开始就有是伪造的
        SecurityFrameworkUtils.removeLoginUser(exchange);

        //2 从请求头提取token
        String token = SecurityFrameworkUtils.obtainAuthorization(exchange);
        //没有token, 继续 filter, 未登陆游客访问
        if (StrUtil.isEmpty(token)) {
            return chain.filter(exchange);
        }

        //3 使用token从缓存中获取用户信息，为空则请求用户中心用token获取用户信息并缓存
        return getLoginUser(exchange, token)
                .defaultIfEmpty(LOGIN_USER_EMPTY)
                .flatMap(user -> {
                    if (user == LOGIN_USER_EMPTY) {
                        return chain.filter(exchange);
                    }

                    //用户ID被放到请求的ATTR MAP中
                    SecurityFrameworkUtils.setLoginUser(exchange, user);
                    //将 user 设置到 login-user 的请求头，使用 json 存储值
                    ServerWebExchange newExchange = exchange.mutate()
                            .request(builder -> SecurityFrameworkUtils.setLoginUserHeader(builder, user)).build();
                    return chain.filter(newExchange);
                });
    }

    /**
     * Token校验并获取用户信息
     */
    private Mono<LoginUser> getLoginUser(ServerWebExchange exchange, String token) {
        KeyValue<Long, String> cacheKey = new KeyValue<Long, String>().setKey(DEFAULT_TENANT_ID).setValue(token);
        LoginUser localUser = loginUserCache.getIfPresent(cacheKey);
        if (localUser != null) {
            return Mono.just(localUser);
        }

        // 缓存不存在，则请求远程服务
        return checkAccessToken(DEFAULT_TENANT_ID, token)
                .flatMap((Function<String, Mono<LoginUser>>) body -> {
                    LoginUser remoteUser = buildUser(body);
                    if (remoteUser != null) {
                        // 非空，则进行缓存
                        loginUserCache.put(cacheKey, remoteUser);
                        return Mono.just(remoteUser);
                    }
                    return Mono.empty();
                });
    }

    private Mono<String> checkAccessToken(Long tenantId, String token) {
        return webClient.get()
                .uri(OAuth2TokenApi.URL_CHECK, uriBuilder -> uriBuilder.queryParam("accessToken", token).build())
                //.headers(httpHeaders -> WebFrameworkUtils.setTenantIdHeader(tenantId, httpHeaders)) // 设置租户的 Header
                .retrieve()
                .bodyToMono(String.class);
    }

    private LoginUser buildUser(String body) {
        // 处理结果，结果不正确
        CommonResult<OAuth2AccessTokenCheckRespDTO> result = JsonUtils.parseObject(body, CHECK_RESULT_TYPE_REFERENCE);
        if (result == null) {
            return null;
        }
        if (result.isError()) {
            // 特殊情况：令牌已经过期（code = 401），需要返回 LOGIN_USER_EMPTY，避免 Token 一直因为缓存，被误判为有效
            if (Objects.equals(result.getCode(), HttpStatus.UNAUTHORIZED.value())) {
                return LOGIN_USER_EMPTY;
            }
            return null;
        }

        // 创建登录用户
        OAuth2AccessTokenCheckRespDTO tokenInfo = result.getData();
        return new LoginUser().setId(tokenInfo.getUserId());
                //.setUserType(tokenInfo.getUserType())
                //.setTenantId(tokenInfo.getTenantId())
                //.setScopes(tokenInfo.getScopes());
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
