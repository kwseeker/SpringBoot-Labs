package top.kwseeker.labs;

import net.sf.cglib.proxy.Enhancer;
import org.junit.jupiter.api.Test;
import top.kwseeker.labs.cglib.SingerEnhance;

import java.lang.reflect.InvocationTargetException;

public class CGLibDynamicProxyTest {

    /**
     * 可以增强除 private 外的方法
     */
    @Test
    public void testProxyMethod() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Person.class);
        enhancer.setCallback(new SingerEnhance());
        enhancer.setStrategy(new PrinterGeneratorStrategy());
        Person proxyInstance = (Person) enhancer.create(new Class[]{String.class}, new String[]{"Arvin"});

        proxyInstance.introduce();
        proxyInstance.work();
        proxyInstance.outerMethod();
        proxyInstance.protectedMethod();
        String name = proxyInstance.getName();
        //从输出的字节码看这个代理类至少包装了2层代理，最外层代理类没有被增强类的方法
        //Class<? extends Person> aClass = proxyInstance.getClass();
        //Method staticMethod = aClass.getMethod("staticMethod");
        //staticMethod.invoke(null);
    }

    /**
     * 对子类增强，默认也会增强父类方法
     */
    @Test
    public void testParentMethod() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Man.class);
        enhancer.setCallback(new SingerEnhance());
        enhancer.setStrategy(new PrinterGeneratorStrategy());
        Man proxyInstance = (Man) enhancer.create(new Class[]{String.class}, new String[]{"Arvin"});

        proxyInstance.alive();
        proxyInstance.manMethod();
        proxyInstance.introduce();
        proxyInstance.work();
        proxyInstance.outerMethod();
        proxyInstance.protectedMethod();
    }
}
