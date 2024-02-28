package top.kwseeker.labs.activiti.simple01;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * 使用自定义Java Service Task代替默认的邮件任务 Email Task
 */
public class SendEmailServiceTask implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        //这里实现自定义的邮件任务
        System.out.printf("send email to %s\n", execution.getVariable("employeeName"));
        System.out.println("邮件详情: " + execution.getVariables());
    }
}
