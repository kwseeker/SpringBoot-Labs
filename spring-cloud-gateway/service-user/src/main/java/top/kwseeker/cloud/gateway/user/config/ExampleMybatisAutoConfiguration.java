package top.kwseeker.cloud.gateway.user.config;

import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration
@MapperScan(value = "${example.info.base-package}", annotationClass = Mapper.class,
        lazyInitialization = "${mybatis.lazy-initialization:false}") // Mapper 懒加载，目前仅用于单元测试
public class ExampleMybatisAutoConfiguration {

    //@Bean
    //public MybatisPlusInterceptor mybatisPlusInterceptor() {
    //    MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
    //    mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor()); // 分页插件
    //    return mybatisPlusInterceptor;
    //}

    //@Bean
    //public MetaObjectHandler defaultMetaObjectHandler(){
    //    return new DefaultDBFieldHandler(); // 自动填充参数类
    //}

    //@Bean
    //@ConditionalOnProperty(prefix = "mybatis-plus.global-config.db-config", name = "id-type", havingValue = "INPUT")
    //public IKeyGenerator keyGenerator(ConfigurableEnvironment environment) {
    //    DbType dbType = IdTypeEnvironmentPostProcessor.getDbType(environment);
    //    if (dbType != null) {
    //        switch (dbType) {
    //            case POSTGRE_SQL:
    //                return new PostgreKeyGenerator();
    //            case ORACLE:
    //            case ORACLE_12C:
    //                return new OracleKeyGenerator();
    //            case H2:
    //                return new H2KeyGenerator();
    //            case KINGBASE_ES:
    //                return new KingbaseKeyGenerator();
    //            case DM:
    //                return new DmKeyGenerator();
    //        }
    //    }
    //    // 找不到合适的 IKeyGenerator 实现类
    //    throw new IllegalArgumentException(StrUtil.format("DbType{} 找不到合适的 IKeyGenerator 实现类", dbType));
    //}

}