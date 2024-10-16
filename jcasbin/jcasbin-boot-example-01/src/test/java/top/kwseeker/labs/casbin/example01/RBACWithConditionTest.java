package top.kwseeker.labs.casbin.example01;

import org.casbin.jcasbin.main.Enforcer;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RBACWithConditionTest {

    // Casbin https://casbin.org/zh/docs/rbac-with-conditions
    @Test
    public void test() throws InterruptedException {
        String modelFile = Objects.requireNonNull(this.getClass().getResource("/rbac_with_cond.conf")).getFile();
        String policyFile = Objects.requireNonNull(this.getClass().getResource("/policy2.csv")).getFile();
        Enforcer e = new Enforcer(modelFile, policyFile);
        Function<String[], Boolean> timeMatchFunc = times -> {
            if (times.length != 2) {
                return false;
            }
            Date now = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                if (!times[0].equals("_")) {
                    Date start = formatter.parse(times[0]);
                    if (now.before(start)) {
                        return false;
                    }
                }
                if (!times[1].equals("_")) {
                    Date end = formatter.parse(times[1]);
                    if (now.after(end)) {
                        return false;
                    }
                }
                return true;
            } catch (ParseException ex) {
                return false;
            }
        };
        e.addNamedLinkConditionFunc("g", "alice", "data2_admin", timeMatchFunc);
        e.addNamedLinkConditionFunc("g", "alice", "data3_admin", timeMatchFunc);
        e.addNamedLinkConditionFunc("g", "alice", "data4_admin", timeMatchFunc);
        e.addNamedLinkConditionFunc("g", "alice", "data5_admin", timeMatchFunc);
        e.addNamedLinkConditionFunc("g", "alice", "data6_admin", timeMatchFunc);
        e.addNamedLinkConditionFunc("g", "alice", "data7_admin", timeMatchFunc);
        e.addNamedLinkConditionFunc("g", "alice", "data8_admin", timeMatchFunc);

        assertTrue(e.enforce("alice", "data1", "read"));
        assertFalse(e.enforce("alice", "data2", "write"));
        assertTrue(e.enforce("alice", "data3", "read"));
        assertTrue(e.enforce("alice", "data4", "write"));
        assertTrue(e.enforce("alice", "data5", "read"));
        assertFalse(e.enforce("alice", "data6", "write"));
        assertTrue(e.enforce("alice", "data7", "read"));
        assertFalse(e.enforce("alice", "data8", "write"));

        assertTrue(e.hasRoleForUser("alice", "data3_admin"));
        Thread.sleep(60*1000);  //等待一分钟等角色过期
        assertTrue(e.hasRoleForUser("alice", "data3_admin"));  //临时角色过期后不会自动删除
        assertFalse(e.enforce("alice", "data3", "read"));           //临时角色条件无效
        assertTrue(e.hasRoleForUser("alice", "data3_admin"));  //临时角色条件无效但还是不会删除
    }
}
