package top.kwseeker.labs.casbin.example01;

import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class CasbinApplication implements CommandLineRunner {

    @Value("${spring.profiles.active}")
    private String activeProfile;
    @Resource
    private Enforcer enforcer;

    public static void main(String[] args) {
        SpringApplication.run(CasbinApplication.class, args);
    }

    //https://casbin.org/editor/
    @Override
    public void run(String... args) throws Exception {
        assert enforcer != null;

        //g, admin, ROLE_ADMIN
        //g, admin, ROLE_LEADER
        //g, admin, ROLE_NORMAL
        //g, kwseeker, ROLE_LEADER
        //g, kwseeker, ROLE_NORMAL
        //g, arvin, ROLE_NORMAL

        //测试前删除所有p策略
        //p, ROLE_ADMIN, /user/*, (POST|GET|PUT)
        //p, ROLE_LEADER, /user/manage, (POST|GET)
        //p, ROLE_LEADER, /user/info, (POST|GET|PUT)
        //p, ROLE_NORMAL, /user/info, (POST|GET)
        List<String[]> policies = new ArrayList<>(4);

        //开启 Redis Watcher 后，开启两个测试进程，其中一个进程每次循环添加一条策略，另一个进程不添加策略
        //观察两个进程策略的执行结果
        if ("dev1".equals(activeProfile)) {
            policies.add(new String[]{"ROLE_ADMIN", "/user/*", "(POST|GET|PUT)"});
            policies.add(new String[]{"ROLE_LEADER", "/user/manage", "(POST|GET)"});
            policies.add(new String[]{"ROLE_LEADER", "/user/info", "(POST|GET|PUT)"});
            policies.add(new String[]{"ROLE_NORMAL", "/user/info", "(POST|GET)"});

            for (String[] policy : policies) {
                verify();
                enforcer.addPolicy(policy); //会同步保存到数据库

                //即JCasbin的模型和策略数据会在系统初始化时加载到内存，权限校验时并不会和数据库交互
                Thread.sleep(3000);
            }
        } else {
            while (true) {
                verify();
                Thread.sleep(3000);
            }
        }
    }

    private void verify() {
        boolean effect1 = enforcer.enforce("admin", "/user/manage", "GET");
        boolean effect2 = enforcer.enforce("admin", "/user/manage", "PUT");
        boolean effect3 = enforcer.enforce("admin", "/user/manage", "POST");
        boolean effect4 = enforcer.enforce("admin", "/user/info", "PUT");
        boolean effect5 = enforcer.enforce("kwseeker", "/user/manage", "PUT");  //false
        boolean effect6 = enforcer.enforce("kwseeker", "/user/manage", "POST");
        boolean effect7 = enforcer.enforce("kwseeker", "/user/info", "PUT");
        boolean effect8 = enforcer.enforce("kwseeker", "/user/info", "POST");
        boolean effect9 = enforcer.enforce("arvin", "/user/manage", "GET"); //false
        boolean effect10 = enforcer.enforce("arvin", "/user/info", "PUT");  //false
        boolean effect11 = enforcer.enforce("arvin", "/user/info", "GET");
        System.out.println(effect1 + ", " + effect2 + ", " + effect3 + ", " + effect4 + ", " + effect5 + ", "
                + effect6 + ", " + effect7 + ", " + effect8 + ", " + effect9 + ", " + effect10 + ", " + effect11);
    }
}
