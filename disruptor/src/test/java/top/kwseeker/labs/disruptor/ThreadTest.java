package top.kwseeker.labs.disruptor;

import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadTest {

    /**
     * ！！！ CPU核心会被占用到 100%
     */
    @Test
    public void testOnSpinWait() {
        while (true) {
            Thread.onSpinWait();
        }
    }

    @Test
    public void testReentrantLockCondition() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition processorNotifyCondition = lock.newCondition();

        Runnable work = () -> {
            while (true) {
                lock.lock();
                try {
                    System.out.println(Thread.currentThread().getName() + " waiting");
                    processorNotifyCondition.await();
                    System.out.println(Thread.currentThread().getName() + " has been waken up");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    lock.unlock();
                }
            }
        };
        new Thread(work, "thread-1").start();
        new Thread(work, "thread-2").start();

        while (true) {
            Thread.sleep(1000);
            lock.lock();
            try {
                //Thread.sleep(1000);       //TODO: 为什么获取锁后执行 sleep 会导致后面唤醒失败？
                System.out.println("signalAll()");
                processorNotifyCondition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }
}
