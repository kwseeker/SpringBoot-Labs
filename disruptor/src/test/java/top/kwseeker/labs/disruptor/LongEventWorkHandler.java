//package top.kwseeker.labs.disruptor;
//
//import com.lmax.disruptor.WorkHandler;
//import top.kwseeker.labs.disruptor.longevent.LongEvent;
//
//public class LongEventWorkHandler implements WorkHandler<LongEvent> {
//
//    private final int handlerId;
//
//    public LongEventWorkHandler(int handlerId) {
//        this.handlerId = handlerId;
//    }
//
//    @Override
//    public void onEvent(LongEvent event) throws Exception {
//        System.out.println("[workHandler:" + handlerId + "] Event: " + event);
//    }
//}
