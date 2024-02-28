package top.kwseeker.labs.activiti.simple01;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;

public class MyEventListener implements ActivitiEventListener {

    @Override
    public void onEvent(ActivitiEvent event) {
        //看看这个简单测试的工作流中都出现了哪些事件，支持的事件类型参考 ActivitiEventType
        System.out.println("Event received: " + event.getType());
    }

    /**
     * 其实是是否忽略异常（某些不严重的异常可以忽略）的意思
     */
    @Override
    public boolean isFailOnException() {
        return false;
    }
}
