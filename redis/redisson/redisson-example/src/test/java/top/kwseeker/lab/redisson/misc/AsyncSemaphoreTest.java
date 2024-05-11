package top.kwseeker.lab.redisson.misc;

import org.junit.jupiter.api.Test;
import org.redisson.misc.AsyncSemaphore;

public class AsyncSemaphoreTest {

    @Test
    public void testAsyncSemaphore() throws InterruptedException {
        AsyncSemaphore semaphore = new AsyncSemaphore(1);
        Thread thread1 = new Thread(() -> {
            //thenAccept()只有完成前面的任务（acquire()返回的CompletableFuture result有值）才会回调
            semaphore.acquire().thenAccept(c -> {
                System.out.println("thread1 acquired semaphore at " + System.currentTimeMillis());
                try {
                    //模拟业务处理
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                semaphore.release();
            });
        });
        Thread thread2 = new Thread(() -> {
            semaphore.acquire().thenAccept(c -> {
                System.out.println("thread2 acquired semaphore at " + System.currentTimeMillis());
                try {
                    //模拟业务处理
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                semaphore.release();
            });
        });
        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }
}
