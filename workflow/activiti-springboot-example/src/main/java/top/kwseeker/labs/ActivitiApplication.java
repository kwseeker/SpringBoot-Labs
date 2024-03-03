package top.kwseeker.labs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.engine.RepositoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import top.kwseeker.labs.config.security.SecurityUtil;

import javax.annotation.Resource;

/**
 * 单文件模拟工作流从模型创建、发布、到执行、处理、结束的流程
 */
//@Configuration
//@ComponentScan
//@EnableAutoConfiguration
//使用自定义数据源所以需要删除默认的，Activiti7默认整合进去了SpringSecurity,所以需要做相关配置
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ActivitiApplication implements CommandLineRunner {

    @Resource
    private SecurityUtil securityUtil;
    //ProcessEngineAutoConfiguration 从这个自动配置类中可以看到主要API类的Bean都创建了，所以这里可以直接注入
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private ObjectMapper objectMapper;

    public static void main(String[] args) {
        SpringApplication.run(ActivitiApplication.class, args);
    }

    /**
     * 实现参考单元测试部分
     */
    @Override
    public void run(String... strings) throws Exception {
        //1 创建并保存工作流模型（Model）https://www.activiti.org/userguide/#_models

        //2 在线编辑器读取、加载、修改、保存

        //3 模型导出

        //4 模型发布与删除

        //5 查看已经部署的模型列表、BPMN文件、图片

        //6 启动流程实例

        //7 处理流程实例
    }
}
