package top.kwseeker.lab.security.config;

import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import top.kwseeker.lab.security.core.authentication.check.PreAuthenticatedAspect;
import top.kwseeker.lab.security.core.authentication.context.TransmittableThreadLocalSecurityContextHolderStrategy;
import top.kwseeker.lab.security.core.authentication.filter.TokenAuthenticationFilter;
import top.kwseeker.lab.security.core.authentication.oauth2.OAuth2TokenApi;
import top.kwseeker.lab.security.core.authentication.permission.PermissionApi;
import top.kwseeker.lab.security.core.authentication.service.SecurityFrameworkService;
import top.kwseeker.lab.security.core.authentication.service.SecurityFrameworkServiceImpl;
import top.kwseeker.lab.security.core.exception.handler.GlobalExceptionHandler;
import top.kwseeker.lab.security.core.authentication.filter.handler.AccessDeniedHandlerImpl;
import top.kwseeker.lab.security.core.authentication.filter.handler.AuthenticationEntryPointImpl;

import javax.annotation.Resource;

@AutoConfiguration
@EnableConfigurationProperties(SecurityProperties.class)
public class DefaultSecurityAutoConfiguration {

    @Resource
    private SecurityProperties securityProperties;

    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    /**
     * Token 认证过滤器 Bean
     */
    @Bean
    //@ConditionalOnBean(GlobalExceptionHandler.class)
    public TokenAuthenticationFilter authenticationTokenFilter(GlobalExceptionHandler globalExceptionHandler,
                                                               OAuth2TokenApi oauth2TokenApi) {
        return new TokenAuthenticationFilter(securityProperties, globalExceptionHandler, oauth2TokenApi);
    }

    /**
     * 拦截用户未登录请求的切面 Bean
     */
    @Bean
    public PreAuthenticatedAspect preAuthenticatedAspect() {
        return new PreAuthenticatedAspect();
    }

    /**
     * 认证失败处理类 Bean
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPointImpl();
    }

    /**
     * 权限不够处理器 Bean
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }

    /**
     * Spring Security 加密器
     * 考虑到安全性，这里采用 BCryptPasswordEncoder 加密器
     *
     * @see <a href="http://stackabuse.com/password-encoding-with-spring-security/">Password Encoding with Spring Security</a>
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(securityProperties.getPasswordEncoderLength());
    }


    @Bean("ss") // 使用 Spring Security 的缩写，方便使用
    public SecurityFrameworkService securityFrameworkService(PermissionApi permissionApi) {
        return new SecurityFrameworkServiceImpl(permissionApi);
    }

    /**
     * 声明调用 {@link SecurityContextHolder#setStrategyName(String)} 方法，
     * 设置使用 {@link TransmittableThreadLocalSecurityContextHolderStrategy} 作为 Security 的上下文策略
     */
    @Bean
    public MethodInvokingFactoryBean securityContextHolderMethodInvokingFactoryBean() {
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setTargetClass(SecurityContextHolder.class);
        methodInvokingFactoryBean.setTargetMethod("setStrategyName");
        methodInvokingFactoryBean.setArguments(TransmittableThreadLocalSecurityContextHolderStrategy.class.getName());
        return methodInvokingFactoryBean;
    }
}
