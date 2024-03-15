package top.kwseeker.labs.casbin.example01;

import org.casbin.jcasbin.main.Enforcer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Resource
    private Enforcer enforcer;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
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
        //p, ROLE_ADMIN, /user/*, (POST|GET|PUT)
        //p, ROLE_LEADER, /user/manage, (POST|GET)
        //p, ROLE_LEADER, /user/info, (POST|GET|PUT)
        //p, ROLE_NORMAL, /user/info, (POST|GET)

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
    }
}
