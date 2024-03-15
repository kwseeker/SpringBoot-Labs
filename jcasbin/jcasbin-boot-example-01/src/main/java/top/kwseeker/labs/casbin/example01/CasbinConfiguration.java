package top.kwseeker.labs.casbin.example01;

import org.casbin.annotation.CasbinDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class CasbinConfiguration {

    @Bean
    @CasbinDataSource
    public DataSource casbinDataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:mysql://127.0.0.1:3306/casbin-lab?characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=Asia/Shanghai&useSSL=false")
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .username("root")
                .password("123456").build();
    }
}
