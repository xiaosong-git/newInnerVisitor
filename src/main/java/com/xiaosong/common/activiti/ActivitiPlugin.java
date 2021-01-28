package com.xiaosong.common.activiti;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.DbKit;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.*;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;


public class ActivitiPlugin implements IPlugin {

	public static ProcessEngine processEngine = null;
    private static ProcessEngineConfiguration processEngineConfiguration = null;
	private boolean isStarted = false;
	@Override
	public boolean start(){
		try {
			createProcessEngine();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean stop() {
		ProcessEngines.destroy(); 
		isStarted = false;
		return true;
	}

	private Boolean createProcessEngine() throws Exception{
		if (isStarted) {
			return true;
		}
		StandaloneProcessEngineConfiguration conf = (StandaloneProcessEngineConfiguration) ProcessEngineConfiguration
		.createStandaloneProcessEngineConfiguration();
//		conf.setDatabaseSchema("cwbase35_9999");
		conf.setDataSource(DbKit.getConfig().getDataSource())
		.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
		.setDbHistoryUsed(true);
		conf.setDatabaseSchemaUpdate("true");
//		conf.setTransactionsExternallyManaged(true); // 使用托管事务工厂
		conf.setTransactionFactory(new ActivitiTransactionFactory());
		ActivitiPlugin.processEngine = conf.buildProcessEngine();
		isStarted = true;
		//开启流程引擎
		System.out.println("启动流程引擎.......");

		String pid = VisitorProcess.createNewProcess("1000",1,"1001");
		VisitorProcess.approve(pid,true,"1001");

		//部署一个流程
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		//任务服务
		TaskService taskService = engine.getTaskService();
		taskService.createTaskQuery().taskAssignee("1001").orderByTaskCreateTime().desc().list();

		return isStarted;
	}

	// 开启流程服务引擎
	public static ProcessEngine buildProcessEngine() {
		if (processEngine == null) {
			if (processEngineConfiguration != null) {
				processEngine = processEngineConfiguration.buildProcessEngine();
			}
		}
		return processEngine;
	}
	
	
	/**
	 * 创建新模型
	 * @throws UnsupportedEncodingException 
	 * */
	public void createModel(ProcessEngine pe) throws UnsupportedEncodingException{
		RepositoryService repositoryService = pe.getRepositoryService();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.put("stencilset", stencilSetNode);
        Model modelData = repositoryService.newModel();

        ObjectNode modelObjectNode = objectMapper.createObjectNode();
        modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, "模型名称");
        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        String description = StringUtils.defaultString("模型描述信息");
        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
        modelData.setMetaInfo(modelObjectNode.toString());
        modelData.setName("模型名称");
        modelData.setKey(StringUtils.defaultString("Urge"));

        repositoryService.saveModel(modelData);
        repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
	}
	/**
	 * 流程定义转模型
	 * */
	public void convertToModel(ProcessEngine pe,String processDefinitionId) throws Exception{
		RepositoryService repositoryService = pe.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
        InputStream bpmnStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),
                processDefinition.getResourceName());
        XMLInputFactory xif = XMLInputFactory.newInstance();
        InputStreamReader in = new InputStreamReader(bpmnStream, "UTF-8");
        XMLStreamReader xtr = xif.createXMLStreamReader(in);
        BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);
        BpmnJsonConverter converter = new BpmnJsonConverter();
        com.fasterxml.jackson.databind.node.ObjectNode modelNode = converter.convertToJson(bpmnModel);
        Model modelData = repositoryService.newModel();
        modelData.setKey(processDefinition.getKey());
        modelData.setName(processDefinition.getResourceName());
        modelData.setCategory(processDefinition.getDeploymentId());

        ObjectNode modelObjectNode = new ObjectMapper().createObjectNode();
        modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, processDefinition.getName());
        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, processDefinition.getDescription());
        modelData.setMetaInfo(modelObjectNode.toString());

        repositoryService.saveModel(modelData);

        repositoryService.addModelEditorSource(modelData.getId(), modelNode.toString().getBytes("utf-8"));

    }



    public void createProcess(String name,String resource)
	{

		ProcessEngine pe = ProcessEngines.getDefaultProcessEngine();
		pe.getRepositoryService()
				.createDeployment()
				.name(name)
				.addClasspathResource(resource)
				.deploy();

//		RepositoryService repositoryService = ActivitiPlugin.processEngine.getRepositoryService();
//		//获取流程定义查询对象
//		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
//		List<ProcessDefinition> list = processDefinitionQuery.list();
//		for (ProcessDefinition processDefinition : list) {
//			System.out.println("id="+processDefinition.getId());
//			System.out.println("name="+processDefinition.getName());
//		}

	}



	@Test
	public void test_02() throws Exception {
		createProcessEngine();
		RepositoryService repositoryService = ActivitiPlugin.processEngine.getRepositoryService();
		//获取流程定义查询对象
		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
		List<ProcessDefinition> list = processDefinitionQuery.list();
		for (ProcessDefinition processDefinition : list) {
			System.out.println("id="+processDefinition.getId());
			System.out.println("name="+processDefinition.getName());
		}
	}


	@Test
	public void start_process() throws Exception {

		//部署一个流程
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		RepositoryService rs = engine.getRepositoryService();
		Deployment deploy = rs.createDeployment().addClasspathResource("processes/visitorApprove.bpmn").deploy();
		ProcessDefinition pd = rs.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();

		//启动流程服务
		RuntimeService runtimeService = engine.getRuntimeService();

		//启动当前流程
		ProcessInstance pi = runtimeService.startProcessInstanceById(pd.getId());
		//当前流程id
		System.out.println(pi.getId());
		//任务服务
		TaskService taskService = engine.getTaskService();
		//当前流程的任务
		Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
		Map<String,Object> map=new HashMap<>();
		//用来判断当前流程是否通过，流程图中定义的判断条件  flag
		map.put("flag",false);
		//完成当前节点任务，flag值用于进行判断
		taskService.complete(task.getId(),map);
		System.out.println("完成第一个节点任务:任务id"+task.getId()+"____  流程实例ID:"+pi.getId());
		//获取第二个节点信息
		Task task1 = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
		System.out.println("流程实例ID:"+task1.getProcessInstanceId()+"___任务ID:"+task1.getId()+"____任务名称:"+task1.getName());
		Map<String,Object> map1=new HashMap<>();
		//用来判断当前流程是否通过，流程图中定义的判断条件  flag
		map1.put("flag",false);
		//完成当前节点任务，flag值用于进行判断
		taskService.complete(task1.getId(),map1);
		System.out.println("完成第二个节点任务:任务id"+task1.getId()+"____  流程实例ID:"+pi.getId());
		//获取第三个节点信息
		Task task2 = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
		System.out.println("流程实例ID:"+task2.getProcessInstanceId()+"___任务ID:"+task2.getId()+"____任务名称:"+task2.getName());
		taskService.complete(task2.getId());
		System.out.println("完成任务");
	}



	@Test
	public void getTaskList() throws Exception {

		//部署一个流程
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		//任务服务
		TaskService taskService = engine.getTaskService();

		taskService.createTaskQuery().taskAssignee("").orderByTaskCreateTime().desc().list();


	}

}