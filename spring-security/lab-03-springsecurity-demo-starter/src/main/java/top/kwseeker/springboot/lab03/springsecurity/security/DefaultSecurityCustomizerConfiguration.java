package top.kwseeker.springboot.lab03.springsecurity.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import top.kwseeker.lab.security.config.AuthorizeRequestsCustomizer;

/**
 * 定制请求过滤规则
 */
@Configuration(proxyBeanMethods = false, value = "defaultSecurityConfiguration")
public class DefaultSecurityCustomizerConfiguration {

    @Bean("defaultAuthorizeRequestCustomizer")
    public AuthorizeRequestsCustomizer authorizeRequestsCustomizer() {
        return new AuthorizeRequestsCustomizer() {
            @Override
            public void customize(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
                // Swagger 接口文档, 不需要过滤
                registry.antMatchers("/v3/api-docs/**").permitAll()     // 元数据
                        .antMatchers("/swagger-ui.html").permitAll();   // Swagger UI
                // Druid 监控接口
                registry.antMatchers("/druid/**").anonymous();
                // Spring Boot Actuator 的安全配置接口
                registry.antMatchers("/actuator").anonymous()
                        .antMatchers("/actuator/**").anonymous();
                // RPC 服务的安全配置
                registry.antMatchers(getRpcPrefix() + "/**").permitAll();
            }
        };
    }
}
