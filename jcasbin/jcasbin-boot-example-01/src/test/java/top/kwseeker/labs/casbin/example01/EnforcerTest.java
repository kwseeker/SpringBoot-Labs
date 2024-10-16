package top.kwseeker.labs.casbin.example01;

import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.util.EnforceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class EnforcerTest {

    @Test
    public void test() {
        String modelFile = Objects.requireNonNull(this.getClass().getResource("/rbac_model1.conf")).getFile();
        String policyFile = Objects.requireNonNull(this.getClass().getResource("/policy1.csv")).getFile();
        Enforcer enforcer = new Enforcer(modelFile, policyFile);

        boolean ret1 = enforcer.enforce("10005", "/sysA/config/bz1", "POST");
        boolean ret2 = enforcer.enforce("10006", "/sysA/config/bz1", "POST");
        boolean ret3 = enforcer.enforce("10007", "/sysA/config/bz1", "POST");
        Assertions.assertTrue(ret1);
        Assertions.assertFalse(ret2);
        Assertions.assertFalse(ret3);

        //EnforceContext context = new EnforceContext("2", "", "2", "");
        EnforceContext context = new EnforceContext("2");
        boolean ret4 = enforcer.enforce(context, "10005", "/sysA/config/bz1", "POST");
        boolean ret5 = enforcer.enforce(context, "10006", "/sysA/config/bz1", "POST");
        boolean ret6 = enforcer.enforce(context, "10007", "/sysA/config/bz1", "POST");
        Assertions.assertFalse(ret4);
        Assertions.assertTrue(ret5);
        Assertions.assertTrue(ret6);
    }
}
