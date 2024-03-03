package top.kwseeker.labs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.repository.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.kwseeker.labs.activiti.ModelMetaInfoKey;
import top.kwseeker.labs.config.security.SecurityUtil;
import top.kwseeker.labs.util.StringUtils;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@SpringBootTest
public class LeaveProcessTest {

    @Resource
    private SecurityUtil securityUtil;
    //ProcessEngineAutoConfiguration 从这个自动配置类中可以看到主要API类的Bean都创建了，所以这里可以直接注入
    @Resource
    private ProcessEngine processEngine;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private ProcessDiagramGenerator processDiagramGenerator;

    //1 创建并保存工作流模型（Model）https://www.activiti.org/userguide/#_models
    @Test
    public void testCreateAndSaveModel() throws JsonProcessingException {
        ModelQuery modelQuery = repositoryService.createModelQuery();
        long modelCount = modelQuery.count();
        System.out.println(">>>>>>> model count before save: " + modelCount);

        ObjectNode modelMeta = objectMapper.createObjectNode();
        modelMeta.put(ModelMetaInfoKey.MODEL_DESCRIPTION, "");
        createAndSaveModel("人事管理", "leave", "请假申请流程", modelMeta, 1);

        modelCount = modelQuery.count();
        System.out.println(">>>>>>> model count after save: " + modelCount);
    }

    //2 在线编辑器读取、加载、修改、保存
    @Test
    public void testMockOnlineEditor() {
        ModelQuery modelQuery = repositoryService.createModelQuery();
        Model model = modelQuery.modelKey("leave").singleResult();

        //  读取
        ObjectNode editorJson = getEditorJson(model.getId());
        if (editorJson != null) {
            System.out.printf(">>>>>>> modelId=%s, editor json: %s \n", model.getId(), editorJson);
        }

        //  模拟保存，保存前手动改下 resourceId
        String editorSourceContent = readResourceFile("leave.json");
        String editorSourceExtraContent = readResourceFile("svg.xml");
        saveModelEditorSource(model.getId(), editorSourceContent, editorSourceExtraContent);
    }

    //3 模型导出，导出为 BPMN20 xml 文件
    @Test
    public void testExportModelEditorSource() throws IOException {
        String modelId = "027908f7-d70c-11ee-8587-92b27a8e8076";
        byte[] modelData = repositoryService.getModelEditorSource(modelId);
        JsonNode jsonNode = objectMapper.readTree(modelData);
        //json格式转BPMN格式, 借助 activiti-json-converter
        BpmnModel bpmnModel = (new BpmnJsonConverter()).convertToBpmnModel(jsonNode);
        byte[] xmlBytes = new BpmnXMLConverter().convertToXML(bpmnModel);
        String bpmnFilename = bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
        try (ByteArrayInputStream in = new ByteArrayInputStream(xmlBytes);
             OutputStream out = Files.newOutputStream(Paths.get(bpmnFilename))) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    //4 模型发布与删除
    @Test
    public void testDeployModel() throws IOException {
        String modelId = "027908f7-d70c-11ee-8587-92b27a8e8076";
        Model model = repositoryService.getModel(modelId);
        //repositoryService.getBpmnModel();   //Model BpmnModel 什么区别？
        byte[] modelEditorSource = repositoryService.getModelEditorSource(modelId);
        JsonNode jsonNode = objectMapper.readTree(modelEditorSource);
        //因为这里存储的是json格式，而Activiti发布只识别BPMN20 xml格式，需要先转换一下
        BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(jsonNode);
        //执行部署
        //会插入 ACT_RE_PROCDEF ACT_RE_DEPLOYMENT ACT_GE_BYTEARRAY(即将转换后的BPMN20 xml文件也插入了表中)
        repositoryService.createDeployment()
                .name(model.getName())          //流程名称
                .category(model.getCategory())  //流程分类
                .key(model.getKey())            //流程key
                .addBpmnModel(model.getKey() + ".bpmn20.xml", bpmnModel)
                .deploy();

        //查看下已经部署的流程
        List<Deployment> ds = repositoryService.createDeploymentQuery().list();
        List<ProcessDefinition> pds = repositoryService.createProcessDefinitionQuery().list();
    }

    @Test
    public void testRemoveModel() throws IOException {
        String modelId = "027908f7-d70c-11ee-8587-92b27a8e8076";
        repositoryService.deleteModel(modelId);
    }

    //5 查看已经部署的模型列表(流程定义列表)、BPMN文件、图片
    @Test
    public void testGetProcessDeployed() throws IOException {
        String pdk = "leave";
        String pdn = "请假申请流程";

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(pdk)
                .processDefinitionName(pdn);
        int size = processDefinitionQuery.list().size();
        // 查的 ACT_RE_PROCDEF 表
        List<ProcessDefinition> processDefinitions = processDefinitionQuery.listPage(0, 10);

        // 查看部署后存储的BPMN资源文件内容输出到标准输出
        ProcessDefinition processDefinition = processDefinitions.get(0);
        String deploymentId = processDefinition.getDeploymentId();
        String resourceName = processDefinition.getResourceName(); //比如："leave.bpmn20.xml"
        try (InputStream resourceAsStream = repositoryService.getResourceAsStream(deploymentId, resourceName)) {
            System.out.println("process definition resource content: ");
            printInputStream(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // BPMN内容转图片
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        if (processDiagramGenerator != null) {
            //默认输出的是SVG xml文件
            try (InputStream inputStream = processDiagramGenerator.generateDiagram(bpmnModel, Collections.emptyList())) {
                printInputStream(inputStream);
            }
        }
    }

    //6 启动流程实例
    @Test
    public void testStartProcessInstance() {
        //首先是获取当前用户身份, 通过用户管理或安全模块获取用户ID等
        String me = "admin";

        String key = "leave";
        //businessKey: 是唯一标识一个ProcessInstance的字符串，这里演示随便使用UUID实现
        String businessKey = UUID.randomUUID().toString();
        //流程实例启动业务传参，实际可能有很多参数，比如首先需要提交给部门领导审批，所以创建实例前需要查询申请人所属部门以及部门领导
        Map<String, Object> variables = new HashMap<>();
        variables.put("applyuserid", me);  //请假申请人
        variables.put("deptleader", me);   //指定请假审批人，为方便测试这里设置为同一人
        //启动流程实例
        System.out.printf("启动一个请假审批流程：key=%s, businessKey=%s\n", key, businessKey);
        runtimeService.startProcessInstanceByKey(key, businessKey, variables);

        //查看所有已经启动的流程实例
        List<ProcessInstance> activeProcessInstances = runtimeService.createProcessInstanceQuery()
                .active()
                .list();
    }

    //7 模拟处理流程实例, 需要人工处理的是UserTask
    //  部门审批（UserTask）
    @Test
    public void testDeptHandleProcessInstance() {
        //首先是获取当前用户身份, 通过用户管理或安全模块获取
        String me = "admin";

        System.out.printf("部门领导审批：deptleader=%s\n", me);
        //查询自己(假设是"admin")待审批的流程用户任务, 也可能查询自己所属组待审批的用户任务（暂略）
        //然后返回到前端页面展示
        List<Task> tasks = taskService.createTaskQuery()
                //.taskAssignee(me)
                .taskCandidateOrAssigned(me, Collections.emptyList())
                //可能有时需要有限查某个紧急任务，可以多加些过滤条件
                //.taskName("")
                //.processDefinitionName("")
                .list();

        Task task = tasks.get(0);
        //审批，通常是在额外的页面和请求中进行
        Map<String, Object> variables = new HashMap<>();
        variables.put("deptleaderapprove", "true"); //部门领导审批通过
        variables.put("comment", "同意请假");   //部门领导审批意见
        variables.put("hr", "admin");          //二级审批人（HR审批）
        taskService.complete(task.getId(), variables);
    }

    //  HR审批（UserTask）
    @Test
    public void testHrHandleProcessInstance() {
        //模拟HR审批
        String me = "admin";

        System.out.printf("HR审批：hr=%s\n", me);
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateOrAssigned(me, Collections.emptyList())
                .list();

        Task task = tasks.get(0);
        //还可以查看下审批办理时间轴
        String processInstanceId = taskService.createTaskQuery()
                .taskId(task.getId()).singleResult().getProcessInstanceId();
        List<HistoricActivityInstance> history = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .activityType("userTask")
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list();

        //HR审批
        Map<String, Object> variables = new HashMap<>();
        variables.put("hrapprove", "true");
        variables.put("comment", "同意请假2");
        taskService.complete(task.getId(), variables);
    }

    //  申请人销假（UserTask）
    @Test
    public void testApplierHandleProcessInstance() {
        String me = "admin";

        System.out.printf("申请人销假：hr=%s\n", me);
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateOrAssigned(me, Collections.emptyList())
                .list();
        Task task = tasks.get(0);

        //审批办理时间轴
        String processInstanceId = taskService.createTaskQuery()
                .taskId(task.getId()).singleResult().getProcessInstanceId();
        List<HistoricActivityInstance> history = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .activityType("userTask")
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list();

        taskService.complete(task.getId());
    }

    /**
     * 保存在 ACT_RE_MODEL 表, Model还有其他一些属性。
     *
     * @param category 模型分类
     * @param key      模型标识，业务中常规定不能相同，不过 ACT_RE_MODEL 此字段（KEY_）并没有唯一约束
     * @param name     模型名称
     * @param version  模型版本
     */
    private Model createAndSaveModel(String category, String key, String name,
                                     ObjectNode metaInfo, Integer version) throws JsonProcessingException {
        ModelQuery modelQuery = repositoryService.createModelQuery();
        List<Model> list = modelQuery.modelKey(key).list();
        if (!list.isEmpty()) {
            System.out.println("model key " + key + " exist!");
            return null;
        }
        Model model = repositoryService.newModel();
        model.setCategory(category);
        model.setKey(key);
        model.setName(name);
        model.setMetaInfo(metaInfo.toString());
        model.setVersion(version);
        repositoryService.saveModel(model);
        addModelEditorSource(model);
        return model;
    }

    /**
     * 保存这个空的模型文件有什么用，必须现在就创建么？
     */
    private void addModelEditorSource(Model model) throws JsonProcessingException {
        HashMap<String, Object> content = new HashMap<>();
        content.put("resourceId", model.getId());
        HashMap<String, String> properties = new HashMap<>();
        properties.put("process_id", model.getKey());
        properties.put("name", model.getName());
        properties.put("category", model.getCategory());
        content.put("properties", properties);
        HashMap<String, String> stencilset = new HashMap<>();
        stencilset.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        content.put("stencilset", stencilset);
        // 保存模型文件到 ACT_GE_BYTEARRAY 表
        repositoryService.addModelEditorSource(model.getId(), objectMapper.writeValueAsBytes(content));
    }

    /**
     * 在线编辑器读取的是JSON数据
     */
    private ObjectNode getEditorJson(String modelId) {
        ObjectNode modelNode = null;
        Model model = repositoryService.getModel(modelId);
        if (model == null) {
            return null;
        }
        try {
            if (StringUtils.isNotEmpty(model.getMetaInfo())) {
                modelNode = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
            } else {
                modelNode = objectMapper.createObjectNode();
                modelNode.put(ModelMetaInfoKey.MODEL_NAME, model.getName());
            }
            modelNode.put(ModelMetaInfoKey.MODEL_ID, model.getId());
            ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(
                    new String(repositoryService.getModelEditorSource(model.getId()), StandardCharsets.UTF_8));
            modelNode.put("model", editorJsonNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return modelNode;
    }

    private String readResourceFile(String file) {
        StringBuilder contentBuilder = new StringBuilder();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return contentBuilder.toString();
    }

    /**
     * 保存json流程定义
     */
    private void saveModelEditorSource(String modelId, String jsonXml, String svgXml) {
        try {
            //更新Model信息，这里只更新版本，其他先不管
            Model model = repositoryService.getModel(modelId);
            //ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
            //modelJson.put(ModelMetaInfoKey.MODEL_NAME, name);
            //modelJson.put(ModelMetaInfoKey.MODEL_DESCRIPTION, description);
            //model.setMetaInfo(modelJson.toString());
            //model.setName(name);
            //model.setDeploymentId(null);
            Integer version = model.getVersion();
            version++;
            model.setVersion(version);
            repositoryService.saveModel(model);

            //更新ModelEditorSource, 可以直接存储json格式的流程定义？
            repositoryService.addModelEditorSource(model.getId(), jsonXml.getBytes(StandardCharsets.UTF_8));

            //svgXml转PNG
            InputStream svgStream = new ByteArrayInputStream(svgXml.getBytes(StandardCharsets.UTF_8));
            TranscoderInput input = new TranscoderInput(svgStream);
            PNGTranscoder transcoder = new PNGTranscoder();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outStream);
            transcoder.transcode(input, output);
            final byte[] result = outStream.toByteArray();
            repositoryService.addModelEditorSourceExtra(model.getId(), result);
            outStream.close();
        } catch (Exception e) {
            throw new ActivitiException("Error saving model", e);
        }
    }

    // 将 InputStream 内容输出到标准输出
    private void printInputStream(InputStream inputStream) {
        try (InputStreamReader isr = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(isr)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
