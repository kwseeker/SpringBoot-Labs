package cn.iocoder.springcloudalibaba.labx04.sentineldemo.provider;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class RateLimiterControllerTest {

    private static final int maxQueueingTimeMs = 100;
    private static final double count = 2;

    private static final AtomicLong latestPassedTime = new AtomicLong(-1);

    /**
     * RateLimiterController canPass() 不保证线程安全
     */
    @Test
    public void testCanPass() throws InterruptedException {
        //创建两个线程并发测试
        CountDownLatch latch = new CountDownLatch(2);
        new Thread(() -> {
            try {
                latch.await();
                boolean b = canPass(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "thread-1").start();
        latch.countDown();

        new Thread(() -> {
            try {
                latch.await();
                boolean b = canPass(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "thread-2").start();
        latch.countDown();

        Thread.sleep(10000);
    }

    private boolean canPass(int acquireCount) throws InterruptedException {
        if (acquireCount <= 0) {
            return true;
        }
        if (count <= 0) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        //每放出一个令牌大概需要的时间ms，count是规则中配置的隔一段时间放出令牌的数量
        //这里测试500ms放出一个
        long costTime = Math.round(1.0 * (acquireCount) / count * 1000);
        //从上次获取令牌之后下次放出令牌的时间
        //latestPassedTime 是 AtomicLong 类型
        long expectedTime = costTime + latestPassedTime.get();
        //=== 为了容易展示并发问题这里放大get set间隔 ===
        //Thread.sleep(50);
        if (expectedTime <= currentTime) {  //已经有放出一个可用令牌，这里好像有并发问题
            latestPassedTime.set(currentTime);
            System.out.println("Thread: " + Thread.currentThread().getName() + " set latestPassedTime = " + currentTime);
            return true;
        } else {  //还没有发出可用令牌
            // 先判断是否在可等待时间范围内
            long waitTime = costTime + latestPassedTime.get() - System.currentTimeMillis();
            if (waitTime > maxQueueingTimeMs) { //没有在范围内
                return false;   //拒绝请求
            } else {    //在范围内
                long oldTime = latestPassedTime.addAndGet(costTime); //获取下个令牌发放时间
                try {
                    waitTime = oldTime - System.currentTimeMillis();
                    if (waitTime > maxQueueingTimeMs) {
                        latestPassedTime.addAndGet(-costTime);
                        return false;
                    }
                    //睡眠等待 waitTime ms 后默认获取到新发放的令牌
                    if (waitTime > 0) {
                        Thread.sleep(waitTime);
                    }
                    return true;
                } catch (InterruptedException ignored) {
                }
            }
        }
        return false;
    }
}
