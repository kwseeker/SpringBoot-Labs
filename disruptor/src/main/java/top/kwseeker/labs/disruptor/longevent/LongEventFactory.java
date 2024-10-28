package top.kwseeker.labs.disruptor.longevent;

import com.lmax.disruptor.EventFactory;

// tag::example[]
public class LongEventFactory implements EventFactory<LongEvent>
{
    @Override
    public LongEvent newInstance()
    {
        return new LongEvent();
    }
}
// end::example[]