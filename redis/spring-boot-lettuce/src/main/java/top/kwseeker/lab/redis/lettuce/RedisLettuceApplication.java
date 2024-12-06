package top.kwseeker.lab.redis.lettuce;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * Redis increment 本身是线程安全的包括返回值，
 * 网上有些文章说 incr 和 decr 命令不是线程安全的，不能用于秒杀场景，这种说法不对；
 * 只是使用方法有问题；
 * 比如判断以消耗商品数量返回值是否大于总库存决定是否可以继续秒杀商品；这种操作属于典型的先查到本地后更新的操作，是这个操作有并发安全问题，而不是 incr 命令本身的问题。
 */
@SpringBootApplication
public class RedisLettuceApplication implements CommandLineRunner {

    //private static final Logger log = LoggerFactory.getLogger(RedisLettuceApplication.class);

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

        //Set<Long> values = new HashSet<>();   //这么写也是有线程安全问题的，并发往 HashSet 中添加不同的元素也是有线程安全问题的，同时插入两个值很可能导致其中一个值插入失败
        Set<Long> values = Collections.synchronizedSet(new HashSet<>());

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
                    if (values.contains(incr)) {
                        System.out.println("重复的返回值：" + incr);
                    } else {
                        values.add(incr);
                    }
                }
                latch.countDown();
            }).start();
        }
        latch.await();

        Object sum = redisTemplate.opsForValue().get(key);
        System.out.println("sum=" + sum);               //sum=1000000
        System.out.println("size=" + values.size());    //size=1000000
    }
}
