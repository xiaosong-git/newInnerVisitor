package com.xiaosong.common.activiti;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CNL on 2021-01-26.
 */
public class VisitorProcess {


    /**
     * 提交审批
     *
     * @param userId     申请人ID
     * @param userType   审批人类型0 ：普通蓝卡员工，  1 : 金卡红卡，和蓝卡的领导
     * @param assigneeId 审批人ID
     * @return
     */
    public static String createNewProcess(String userId, int userType, String assigneeId) {
        String processId = null;
        //部署一个流程
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService rs = engine.getRepositoryService();
        Deployment deploy = rs.createDeployment().addClasspathResource("processes/visitorApprove.bpmn").deploy();
        ProcessDefinition pd = rs.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
        //启动流程服务
        RuntimeService runtimeService = engine.getRuntimeService();

        //启动当前流程
        ProcessInstance pi = runtimeService.startProcessInstanceById(pd.getId());
        processId = pi.getId();
        //当前流程id
        System.out.println(processId);
        //任务服务
        TaskService taskService = engine.getTaskService();
        //当前流程的任务
        Task task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
        Map<String, Object> map = new HashMap<>();
        //用来判断当前流程是否通过，流程图中定义的判断条件  flag
        map.put("userType", userType);
        task.setOwner(userId);
        task.setAssignee(assigneeId);
        //完成当前节点任务，flag值用于进行判断
        taskService.complete(task.getId(), map);
        System.out.println("提交申请:任务id" + task.getId() + "____  流程实例ID:" + processId);

        return processId;

    }


    /**
     * 审批
     *
     * @param processId 流程ID
     * @param flag      审批结果
     * @param userId    审批人ID
     */
    public static boolean approve(String processId, boolean flag, String userId) {
        //部署一个流程
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        //获取第二个节点信息
        Task task1 = taskService.createTaskQuery().processInstanceId(processId).singleResult();

        if (task1 != null) {
            System.out.println("流程实例ID:" + task1.getProcessInstanceId() + "___任务ID:" + task1.getId() + "____任务名称:" + task1.getName());
            Map<String, Object> map1 = new HashMap<>();
            //用来判断当前流程是否通过，流程图中定义的判断条件  flag
            map1.put("flag", flag);
            task1.setAssignee(userId);
            //完成当前节点任务，flag值用于进行判断
            taskService.complete(task1.getId(), map1);
        }
        //查看是否还有下一级
        Task task2 = taskService.createTaskQuery().processInstanceId(processId).singleResult();
        return task2 == null ? flag : false;
    }


}
