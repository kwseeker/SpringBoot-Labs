package top.kwseeker.springboot.lab03.springsecurity.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<AFilter> aFilter() {
        AFilter aFilter = new AFilter();
        FilterRegistrationBean<AFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(aFilter);
        registration.setOrder(-101);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<ZFilter> zFilter() {
        ZFilter zFilter = new ZFilter();
        FilterRegistrationBean<ZFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(zFilter);
        registration.setOrder(Integer.MAX_VALUE);
        return registration;
    }
}
