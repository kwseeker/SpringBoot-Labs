package top.kwseeker.lab.redis.lettuce;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * Redis increment 本身是线程安全的，只是多线程并发 increment, 这个 increment() 返回值是不安全的，可能出现返回值相同
 * 网上有些文章说 incr 和 decr 命令不是线程安全的，不能用于秒杀场景，这种说法不对；
 * 只是 increment() 返回值不是线程安全的，从下面测试可以看到方法返回值可能相同
 */
@SpringBootApplication
public class RedisLettuceApplication implements CommandLineRunner {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public static void main(String[] args) {
        SpringApplication.run(RedisLettuceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String key = "test:redisTemplate:incr";
        Boolean delete = redisTemplate.delete(key);
        assert Boolean.TRUE.equals(delete);

        Set<Long> values = new HashSet<>();

        //CountDownLatch latch = new CountDownLatch(1000);
        CountDownLatch latch = new CountDownLatch(100);
        //CountDownLatch latch = new CountDownLatch(10);
        //for (int i = 0; i < 1000; i++) {
        for (int i = 0; i < 100; i++) {
        //for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                //for (int j = 0; j < 1000; j++) {
                for (int j = 0; j < 10000; j++) {
                //for (int j = 0; j < 100000; j++) {
                    Long incr = redisTemplate.opsForValue().increment(key);
                    //System.out.println(incr);
                    values.add(incr);
                }
                latch.countDown();
            }).start();
        }
        latch.await();

        Object sum = redisTemplate.opsForValue().get(key);
        System.out.println("sum=" + sum);               //sum=1000000
        System.out.println("size=" + values.size());    //size=997832， 里面有些返回值相同，返回值不可靠，（Jedis Lettuce Redisson 都是这样）
    }
}
