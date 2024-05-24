package top.kwseeker.labs;

import org.junit.jupiter.api.Test;
import top.kwseeker.labs.jdk.ProgrammerEnhance;

import java.lang.reflect.Proxy;

/**
 * 测试JDK动态代理可以作用在哪些方法上
 */
public class JDKDynamicProxyTest {

    @Test
    public void testProxyPublicMethod() {
        Person person = new Person("Arvin");
        ProgrammerEnhance handler = new ProgrammerEnhance(person);
        Human proxyInstance = (Human) Proxy.newProxyInstance(
                person.getClass().getClassLoader(),
                new Class<?>[]{Human.class},    //仅仅能增强接口类中的方法
                handler);

        proxyInstance.introduce();
        proxyInstance.work();
    }
}
