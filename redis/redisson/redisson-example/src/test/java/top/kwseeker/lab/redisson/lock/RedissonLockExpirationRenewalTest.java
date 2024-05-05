package top.kwseeker.lab.redisson.lock;

import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import top.kwseeker.lab.redisson.RedisLocalDockerTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RedissonLockExpirationRenewalTest extends RedisLocalDockerTest {

    private static final String LOCK_KEY = "LOCK_KEY";
    public static final long LOCK_WATCHDOG_TIMEOUT = 1_000L;

    /**
     * 测试到期后
     * @throws InterruptedException
     */
    @Test
    public void testExpirationRenewalIsWorkingAfterTimeout() throws InterruptedException {
        //GenericContainer<?> redis = createRedis();
        //redis.start();

        //新建一个单机模式连接
        //Config c = createConfig(redis);
        Config c = createConfig();
        c.setLockWatchdogTimeout(LOCK_WATCHDOG_TIMEOUT);    //看门狗超时时间1s
        RedissonClient redisson = Redisson.create(c);

        RLock lock = redisson.getLock(LOCK_KEY);
        lock.lock();
        try {
            // force expiration renewal error
            //restart(redis); //重启Redis服务容器，会抛异常，不重启则不会
            // wait for timeout
            Thread.sleep(LOCK_WATCHDOG_TIMEOUT * 2);
        } finally {
            //断言调用unlock()时会抛出IllegalMonitorStateException异常
            assertThatThrownBy(lock::unlock).isInstanceOf(IllegalMonitorStateException.class);
        }

        RLock lock2 = redisson.getLock(LOCK_KEY);
        lock2.lock();
        try {
            // wait for timeout
            Thread.sleep(LOCK_WATCHDOG_TIMEOUT * 2);
        } finally {
            lock2.unlock();
        }

        redisson.shutdown();
        //redis.stop();
    }

}
