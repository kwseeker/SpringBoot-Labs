package top.kwseeker.labs.activiti.simple01;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class QuickStartTest {

    /**
     * 测试通过加载 activiti.cfg.xml 通过典型方式创建工作流引擎
     */
    @Test
    public void testCreateProcessEngine() {
        //工作流引擎接口
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        assert processEngine != null;

        ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();
        assertNotNull(processEngineConfiguration);

        //databaseSchemaUpdate create-drop 模式需要执行 close() 才会删除数据表
        processEngine.close();
    }

    /**
     * 测试加载BPMN文件并创建工作流实例
     */
    @Test
    public void testProcessInstanceHandling() {
        ProcessEngine processEngine = null;
        try {
            processEngine = ProcessEngines.getDefaultProcessEngine();
            assert processEngine != null;

            //加载BPMN文件创建 Deployment
            RepositoryService repositoryService = processEngine.getRepositoryService();
            Deployment deployment = repositoryService.createDeployment()
                    .addClasspathResource("vocation-request.bpmn20.xml")
                    .disableSchemaValidation()
                    .deploy();

            ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
            long count = processDefinitionQuery.count();
            System.out.println("process definition count: " + count);
            //assertEquals(1, count);

            //1 模拟员工提交请假申请：创建一个请假审批的工作流实例
            RuntimeService runtimeService = processEngine.getRuntimeService();
            //工作流属性
            Map<String, Object> vocationVariables = new HashMap<>();
            vocationVariables.put("employeeName", "Arvin Lee");
            vocationVariables.put("numberOfDays", 2);
            vocationVariables.put("reason", "I need a break!");
            //variables.put("startDate", "2024-01-01 12:00");
            //创建工作流实例
            ProcessInstance processInstance = runtimeService
                    .startProcessInstanceByKey("vacationRequest", vocationVariables);
            long count2=runtimeService.createProcessInstanceQuery().count();
            assertEquals(1, count2);

            //2 模拟主管审批（拒绝）
            //TODO：通过监听器向主管发送审批提醒消息
            //主管查询自己待审批任务（假设主管属于management用户组）
            TaskService taskService = processEngine.getTaskService();
            List<Task> tasks = taskService.createTaskQuery()
                    .taskCandidateGroup("management").list();   //按候选人分组查询，BPMN文件中通过potentialOwner为此任务默认设置了潜在所有用户组
            Task task = tasks.get(0);
            System.out.println("请假详情：" + task.getDescription());
            //拒绝请假申请
            Map<String, Object> denyVariables = new HashMap<>();
            denyVariables.put("vacationApproved", "false");
            denyVariables.put("comments", "We have a tight deadline!");
            taskService.complete(task.getId(), denyVariables);

            //3 模拟请假请求被拒绝后，申请人收到拒绝信息(比如通过Listener)后重新提交申请
            //先查看被拒后的UserTask，通过 activiti:assignee="${employeeName}" 指定为了发起人
            Task currentTask = taskService.createTaskQuery()
                    .taskName("Modify vacation request")
                    .taskAssignee("Arvin Lee")
                    .singleResult();
            assertNotNull(currentTask);
            //重新提交
            Map<String, Object> resendVariables = new HashMap<>();
            resendVariables.put("numberOfDays", 2);
            resendVariables.put("reason", "I really need a break!");
            resendVariables.put("resendRequest", "true");
            resendVariables.put("startDate", "2024-01-01 12:00");
            taskService.complete(currentTask.getId(), resendVariables);

            //4 模拟主管二次审批（同意）
            //.taskId("22")    //这里的Id和任务定义中的ID不是同一个概念
            tasks = taskService.createTaskQuery()
                    .taskName("Handle Request for Vacation")
                    .taskCandidateGroup("management")
                    .list();
            task = tasks.get(0);
            assertNotNull(task);
            //同意
            Map<String, Object> agreeVariables = new HashMap<>();
            agreeVariables.put("vacationApproved", "true");
            agreeVariables.put("comments", "OK!");
            taskService.complete(task.getId(), agreeVariables);

            //5 模拟请假请求被同意后，执行 Java Service Task，发送邮件通知
            //由于 Java Service Task 是自动任务，不需要编码处理

            //没有 Terminate End Event 的话，可以看到会一直循环，因为 None End Event 不会终止流程实例
            while (!processInstance.isEnded()) {
                Thread.yield();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            //databaseSchemaUpdate create-drop 模式需要执行 close() 才会删除数据表
            if (processEngine != null) {
                processEngine.close();
            }
        }
    }
}