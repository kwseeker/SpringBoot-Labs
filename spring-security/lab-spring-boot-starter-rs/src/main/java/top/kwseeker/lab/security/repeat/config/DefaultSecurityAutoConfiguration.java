package top.kwseeker.lab.security.repeat.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

@AutoConfiguration
@EnableConfigurationProperties(SecurityProperties.class)
public class DefaultSecurityAutoConfiguration {

    @Resource
    private SecurityProperties securityProperties;

    @Bean
    //@ConditionalOnBean(GlobalExceptionHandler.class)
    public TokenAuthenticationFilter authenticationTokenFilter() {
        return new TokenAuthenticationFilter();
    }
}
