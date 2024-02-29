package top.kwseeker.labs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 单文件模拟
 */
//@Configuration
//@ComponentScan
//@EnableAutoConfiguration
//使用自定义数据源所以需要删除默认的，Activiti7默认整合进去了SpringSecurity,所以需要做相关配置
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ActivitiApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ActivitiApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        //1 创建工作流模型（Model）

    }
}
