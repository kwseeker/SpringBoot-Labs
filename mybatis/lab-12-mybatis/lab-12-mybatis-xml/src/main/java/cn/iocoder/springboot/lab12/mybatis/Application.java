package cn.iocoder.springboot.lab12.mybatis;

import cn.iocoder.springboot.lab12.mybatis.dataobject.UserDO;
import cn.iocoder.springboot.lab12.mybatis.mapper.UserMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
@MapperScan(basePackages = "cn.iocoder.springboot.lab12.mybatis.mapper")
public class Application implements CommandLineRunner {

    @Resource
    private UserMapper userMapper;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * 这个测试研究非事务控制下，两个线程其中一个线程在另一个线程中两次查询间修改数据，
     * 为何一级缓存能在第二次查询获取最新数据
     */
    @Override
    public void run(String... args) throws Exception {
        Thread thread1 = new Thread(() -> {
            UserDO user = new UserDO();
            user.setId(10001);
            user.setUsername("arvin");
            user.setPassword("123456"); //修改密码
            int ret = userMapper.updateById(user);
            System.out.println("reset ret: " + ret);

            user = userMapper.selectById(10001);
            System.out.println("user: " + user);
            try {
                Thread.sleep(2000); //等待另一个线程修改完密码
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            user = userMapper.selectById(10001);
            System.out.println("user: " + user);
        });
        Thread thread2 = new Thread(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            UserDO user = new UserDO();
            user.setId(10001);
            user.setUsername("arvin");
            user.setPassword("111111"); //修改密码
            int ret = userMapper.updateById(user);
            System.out.println("update ret: " + ret);
        });
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
    }
}
