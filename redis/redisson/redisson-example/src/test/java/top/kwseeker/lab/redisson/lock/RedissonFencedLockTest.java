package top.kwseeker.lab.redisson.lock;

import org.junit.jupiter.api.Test;
import org.redisson.api.RFencedLock;
import top.kwseeker.lab.redisson.RedisLocalDockerTest;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class RedissonFencedLockTest extends RedisLocalDockerTest {

    private volatile int count = 0;

    @Test
    public void testLockUnlock() throws InterruptedException {
        count = 0;

        RFencedLock lock = redisson.getFencedLock("flock");
        ExecutorService es = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100000; i++) {
            es.submit(() -> {
                Long token = lock.lockAndGetToken(1000, TimeUnit.MILLISECONDS);
                if (token != null) {
                    try {
                            Long currentToken = lock.getToken();
                            if (Objects.equals(currentToken, token)) {
                                count++;
                            }
                    } finally {
                        lock.unlock();
                    }
                }
            });
        }

        System.out.println("---");
        //Thread.sleep(10000);    //TODO: 不延迟一下，会报下面错误，Redisson客户端连接中报的异常，什么原因
        //2024.05.16 19:09:18.196 ERROR rejectedExecution : Failed to submit a listener notification task. Event loop shut down?
        //java.util.concurrent.RejectedExecutionException: event executor terminated
        //at io.netty.util.concurrent.SingleThreadEventExecutor.reject(SingleThreadEventExecutor.java:934)
        es.shutdown();
        try {
            boolean b = es.awaitTermination(100, TimeUnit.SECONDS);// 等待所有任务完成
        } catch (InterruptedException e) {
            // 处理中断异常
        }

        assertThat(count).isEqualTo(100000);
    }

    @Test
    public void testTokenIncrease() {
        RFencedLock lock = redisson.getFencedLock("lock");
        Long token1 = lock.lockAndGetToken();
        assertThat(token1).isEqualTo(1);
        lock.unlock();
        assertThat(token1).isEqualTo(1);

        Long token2 = lock.lockAndGetToken();
        assertThat(token2).isEqualTo(2);
        lock.unlock();

        lock.lock();
        assertThat(lock.getToken()).isEqualTo(3);
        lock.unlock();

        lock.lock(10, TimeUnit.SECONDS);
        assertThat(lock.getToken()).isEqualTo(4);
        lock.unlock();

        Long token4 = lock.tryLockAndGetToken();
        assertThat(token4).isEqualTo(5);
    }

}
