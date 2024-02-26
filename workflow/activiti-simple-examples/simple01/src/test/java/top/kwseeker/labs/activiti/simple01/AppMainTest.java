package top.kwseeker.labs.activiti.simple01;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppMainTest {

    @Test
    public void testCreateProcessEngine() {
        //工作流引擎接口
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        assert processEngine != null;
        ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();

        assertNotNull(processEngineConfiguration);
    }
}