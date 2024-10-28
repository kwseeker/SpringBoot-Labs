package top.kwseeker.labs.disruptor.longevent.legacy;

// tag::example[]

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import top.kwseeker.labs.disruptor.longevent.LongEvent;
import top.kwseeker.labs.disruptor.longevent.LongEventFactory;
import top.kwseeker.labs.disruptor.longevent.LongEventHandler;

import java.nio.ByteBuffer;

public class LongEventMain {

    public static void main(String[] args) throws Exception {
        LongEventFactory factory = new LongEventFactory();

        int bufferSize = 4;
        Disruptor<LongEvent> disruptor =
                new Disruptor<>(factory, bufferSize, DaemonThreadFactory.INSTANCE);
        disruptor.handleEventsWith(new LongEventHandler(1), new LongEventHandler(2));
        disruptor.handleEventsWith(new LongEventHandler(3));
        disruptor.start();

        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        LongEventProducer producer1 = new LongEventProducer(ringBuffer);
        LongEventProducer producer2 = new LongEventProducer(ringBuffer);
        createAndStartProducerThread(producer1);
        createAndStartProducerThread(producer2);
    }

    private static void createAndStartProducerThread(LongEventProducer producer1) {
        new Thread(() -> {
            try {
                ByteBuffer bb = ByteBuffer.allocate(8);
                for (long l = 0; true; l++) {
                    bb.putLong(0, l);
                    producer1.onData(bb);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
// end::example[]