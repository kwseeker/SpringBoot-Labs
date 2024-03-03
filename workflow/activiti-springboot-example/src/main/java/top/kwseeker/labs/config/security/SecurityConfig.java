//package top.kwseeker.labs.config.security;
//
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .csrf().ignoringAntMatchers("/druid/*")
//                // 配置请求地址的权限
//                .and()
//                .authorizeRequests()
//                    .anyRequest().permitAll()
//                //    .antMatchers("/test/demo").permitAll() // 所有用户可访问
//                //    .antMatchers("/test/admin").hasRole("ADMIN") // 需要 ADMIN 角色
//                //    .antMatchers("/test/normal").access("hasRole('ROLE_NORMAL')") // 需要 NORMAL 角色。
//                //    // 任何请求，访问的用户都需要经过认证
//                //    .anyRequest().authenticated()
//                .and()
//                // 设置 Form 表单登陆
//                .formLogin()
//                    .permitAll() // 所有用户可访问
//                .and()
//                // 配置退出相关
//                .logout()
//                    .permitAll(); // 所有用户可访问
//    }
//}