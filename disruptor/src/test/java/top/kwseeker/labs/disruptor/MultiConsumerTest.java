package top.kwseeker.labs.disruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.junit.jupiter.api.Test;
import top.kwseeker.labs.disruptor.longevent.LongEvent;
import top.kwseeker.labs.disruptor.longevent.LongEventFactory;
import top.kwseeker.labs.disruptor.longevent.LongEventHandler;
import top.kwseeker.labs.disruptor.longevent.legacy.LongEventProducer;

import java.nio.ByteBuffer;

public class MultiConsumerTest {

    /**
     * 多消费者竞争消费
     * 需要切换成 3.x 版本
     */
    @Test
    public void testCompeteConsume() throws InterruptedException {
        //LongEventFactory factory = new LongEventFactory();
        //
        //int bufferSize = 1024;
        //Disruptor<LongEvent> disruptor = new Disruptor<>(factory, bufferSize, DaemonThreadFactory.INSTANCE);
        //disruptor.handleEventsWithWorkerPool(new LongEventWorkHandler(1), new LongEventWorkHandler(2));
        //disruptor.start();
        //
        //RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        //LongEventProducer producer = new LongEventProducer(ringBuffer);
        //ByteBuffer bb = ByteBuffer.allocate(8);
        //for (long l = 0; l < 10; l++) {
        //    bb.putLong(0, l);
        //    producer.onData(bb);
        //    //Thread.sleep(1000);
        //}
    }

    /**
     * 多消费者顺序消费
     */
    @Test
    public void testOrderlyConsume() {
        Disruptor<LongEvent> disruptor = new Disruptor<>(new LongEventFactory(), 1024, DaemonThreadFactory.INSTANCE);
        disruptor.handleEventsWith(new LongEventHandler(1))
                .then(new LongEventHandler(2))
                .then(new LongEventHandler(3));
        disruptor.start();
        disruptor.publishEvent((event, sequence) -> {
            System.out.println("set sequence:" + sequence + ", event.value:" + 10L);
            event.set(10L);
        });
    }

    /**
     * 多消费者菱形消费
     */
    @Test
    public void testDiamondConsume() {
        Disruptor<LongEvent> disruptor = new Disruptor<>(new LongEventFactory(), 1024, DaemonThreadFactory.INSTANCE);
        disruptor.handleEventsWith(new LongEventHandler(1), new LongEventHandler(2))
                .then(new LongEventHandler(3));
        disruptor.start();
        disruptor.publishEvent((event, sequence) -> {
            System.out.println("set sequence:" + sequence + ", event.value:" + 10L);
            event.set(10L);
        });
    }

    /**
     * 链式并行消费
     * 即多条消费者链并行消费，就是设置多组顺序消费者
     */
    @Test
    public void testConcurrentOrderlyConsume() {
        Disruptor<LongEvent> disruptor = new Disruptor<>(new LongEventFactory(), 1024, DaemonThreadFactory.INSTANCE);

        disruptor.handleEventsWith(new LongEventHandler(11))
                .then(new LongEventHandler(12));
        disruptor.handleEventsWith(new LongEventHandler(21))
                .then(new LongEventHandler(22));

        disruptor.start();
        disruptor.publishEvent((event, sequence) -> {
            System.out.println("set sequence:" + sequence + ", event.value:" + 10L);
            event.set(10L);
        });
    }

    @Test
    public void testBatchRewind() {
        Disruptor<LongEvent> disruptor = new Disruptor<>(new LongEventFactory(), 1024, DaemonThreadFactory.INSTANCE);

        //disruptor.handleEventsWith(new SimpleBatchRewindStrategy(), new RewindableEventHandler<LongEvent>() {
        disruptor.handleEventsWith(new EventuallyGiveUpBatchRewindStrategy(3), new RewindableEventHandler<LongEvent>() {
            @Override
            public void onEvent(LongEvent event, long sequence, boolean endOfBatch) throws RewindableException, Exception {
                System.out.println("Event: " + event + ", sequence: " + sequence + ", endOfBatch:" + endOfBatch);
                if (sequence == 3) {
                    throw new RewindableException(null);
                }
            }
        });
        disruptor.start();

        for (int i = 0; i < 5; i++) {
            disruptor.publishEvent((event, sequence) -> {
                System.out.println("set sequence:" + sequence + ", event.value:" + 10L);
                event.set(10L);
            });
        }
    }
}
