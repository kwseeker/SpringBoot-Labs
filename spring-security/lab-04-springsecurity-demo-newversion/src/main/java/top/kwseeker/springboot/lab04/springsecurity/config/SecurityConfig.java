package top.kwseeker.springboot.lab04.springsecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import top.kwseeker.springboot.lab04.springsecurity.config.jwt.JwtTokenOncePerRequestFilter;
import top.kwseeker.springboot.lab04.springsecurity.config.jwt.JwtTokenProvider;

import javax.annotation.Resource;

/**
 * 从 5.7.0 开始不再推荐使用 Adapter 配置安全策略，体检直接创建 SecurityFilterChain 实例
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private InvalidAuthenticationEntryPoint invalidAuthenticationEntryPoint;


    @Bean
    public PasswordEncoder passwordEncoder() {
        //return new BCryptPasswordEncoder();
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用basic明文验证
                .httpBasic().disable()
                // 前后端分离架构不需要csrf保护
                .csrf().disable()
                // 禁用默认登录页
                .formLogin().disable()
                // 禁用默认登出页
                .logout().disable()
                // 设置异常的EntryPoint，如果不设置，默认使用Http403ForbiddenEntryPoint
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(invalidAuthenticationEntryPoint))
                // 前后端分离是无状态的，不需要session了，直接禁用。
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        // 允许所有OPTIONS请求
                        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 允许直接访问授权登录接口
                        .antMatchers(HttpMethod.POST, "/authenticate").permitAll()
                        .antMatchers(HttpMethod.POST, "/refresh_token").permitAll()
                        // 允许 SpringMVC 的默认错误地址匿名访问
                        .antMatchers("/error").permitAll()
                        // 其他所有接口必须有Authority信息，Authority在登录成功后的UserDetailsImpl对象中默认设置“ROLE_USER”
                        //.requestMatchers("/**").hasAnyAuthority("ROLE_USER")
                        // 允许任意请求被已登录用户访问，不检查Authority
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                // 加我们自定义的过滤器，替代UsernamePasswordAuthenticationFilter
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 调用loadUserByUsername获得UserDetail信息，在AbstractUserDetailsAuthenticationProvider里执行用户状态检查
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
         //DaoAuthenticationProvider 从自定义的 userDetailsService.loadUserByUsername 方法获取UserDetails
        authProvider.setUserDetailsService(userDetailsService);
        // 设置密码编辑器
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * 登录时需要调用AuthenticationManager.authenticate执行一次校验
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public JwtTokenOncePerRequestFilter authenticationJwtTokenFilter() {
        return new JwtTokenOncePerRequestFilter();
    }
}
