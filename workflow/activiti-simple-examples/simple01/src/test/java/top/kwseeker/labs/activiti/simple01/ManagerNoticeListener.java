package top.kwseeker.labs.activiti.simple01;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * 用于提醒管理人员审批的监听器，监听任务创建事件
 */
public class ManagerNoticeListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("task event name: " + delegateTask.getEventName());
        System.out.println("exec something reminding management to check ...");
    }
}
