package com.xiaosong.common.activiti;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.jfinal.plugin.activerecord.DbKit;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;

public class InitProcessEngine extends HttpServlet {
	private static final long serialVersionUID = 715456159702221404L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		StandaloneProcessEngineConfiguration conf = (StandaloneProcessEngineConfiguration) ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
		//		conf.setDatabaseSchema("root");
		conf.setDataSource(DbKit.getConfig().getDataSource()).setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE).setDbHistoryUsed(true);
		conf.setTransactionFactory(new ActivitiTransactionFactory());
		ActivitiPlugin.processEngine = conf.buildProcessEngine();
		//自启动完成

		//部署流程定义
		ProcessEngine pe = ProcessEngines.getDefaultProcessEngine();
		pe.getRepositoryService()
				.createDeployment()
				.name("测试审批")
				.addClasspathResource("/resources/processes/visitorApprove.bpmn")
				.deploy();
	}

}