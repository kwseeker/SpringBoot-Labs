package top.kwseeker.labs.disruptor.longevent;

import com.lmax.disruptor.EventHandler;

// tag::example[]
public class LongEventHandler implements EventHandler<LongEvent>
{
    private int handlerId;

    public LongEventHandler(int handlerId) {
        this.handlerId = handlerId;
    }

    @Override
    public void onEvent(LongEvent event, long sequence, boolean endOfBatch)
    {
        System.out.println("[handler:" + handlerId + "] Event: " + event);
    }
}
// end::example[]