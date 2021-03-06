package com.xiaosong.common.activiti;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CNL on 2021-01-26.
 */
public class VisitorProcess {


    /**
     * 提交访客审批
     *
     * @param userId     申请人ID
     * @param userType   审批人类型0 ：普通蓝卡员工，  1 : 金卡红卡，和蓝卡的领导 车辆审批岗
     * @param assigneeId 审批人ID
     * @return
     */
    public static String createNewProcess(String userId, Integer userType, String assigneeId) {
        return createNewProcess("processes/visitorApprove.bpmn",userId,userType,assigneeId);
    }


    /**
     * 提交车辆审批
     *
     * @param userId     申请人ID
     * @param userType   审批人类型0 ：普通蓝卡员工，  1 : 金卡红卡，和蓝卡的领导 车辆审批岗
     * @param assigneeId 审批人ID
     * @return
     */
    public static String createNewCarProcess(String userId, Integer userType, String assigneeId) {
        return createNewProcess("processes/carApprove.bpmn",userId,userType,assigneeId);
    }


    /**
     * 提交审批
     *
     * @param userId     申请人ID
     * @param userType   审批人类型0 ：普通蓝卡员工，  1 : 金卡红卡，和蓝卡的领导 车辆审批岗
     * @param assigneeId 审批人ID
     * @return
     */
    private static String createNewProcess(String resource,String userId, Integer userType, String assigneeId) {

        userType = userType==null?0:userType;
        String processId = null;
        //部署一个流程
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService rs = engine.getRepositoryService();
        Deployment deploy = rs.createDeployment().addClasspathResource(resource).deploy();
        ProcessDefinition pd = rs.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
        //启动流程服务
        RuntimeService runtimeService = engine.getRuntimeService();
        Map<String, Object> map = new HashMap<>();
        //用来判断当前流程是否通过，流程图中定义的判断条件  flag
        map.put("assignee", userId);
        //启动当前流程
        ProcessInstance pi = runtimeService.startProcessInstanceById(pd.getId(),map);
        processId = pi.getId();
        //当前流程id
        System.out.println(processId);
        //任务服务
        TaskService taskService = engine.getTaskService();
        //当前流程的任务
        Task task = taskService.createTaskQuery().processInstanceId(processId).singleResult();

        //用来判断当前流程是否通过，流程图中定义的判断条件  flag
        map.put("userType", userType);
        map.put("assignee", assigneeId);
        //完成当前节点任务，flag值用于进行判断
        taskService.complete(task.getId(), map);
        System.out.println("提交申请:任务id" + task.getId() + "____  流程实例ID:" + processId);
//
//        task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
//        taskService.complete(task.getId(), map);

        return processId;

    }



    /**
     * 审批
     *
     * @param processId 流程ID
     * @param flag      审批结果
     * @param userId    审批人ID
     */
    public static boolean approve(String processId, boolean flag, String assignee,Integer userType,String userId) {

        userType = userType==null?0:userType;
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
            map1.put("assignee", assignee);
            map1.put("userType",userType);
            //完成当前节点任务，flag值用于进行判断,如果委托人为空那么就是还没有指定，任务未完成
            if((assignee!=null || !flag || userType ==1 )&&(userId.equals(task1.getAssignee()))) {
                taskService.complete(task1.getId(), map1);
            }
        }
        //查看是否还有下一级
        Task task2 = taskService.createTaskQuery().processInstanceId(processId).singleResult();
        return task2 == null ? false : true;
    }



    /**
     * 审批
     *
     * @param processId 流程ID
     * @param flag      审批结果
     * @param userId    审批人ID
     *                  assignee 委托人
     */
    public static boolean approveCar(String processId, boolean flag, String assignee,Integer userType,String userId) {
        userType = userType==null?0:userType;
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
            map1.put("assignee", assignee);
            map1.put("userType",userType);
            //完成当前节点任务，flag值用于进行判断,如果委托人为空那么就是还没有指定，任务未完成
            if((assignee!=null || !flag || userType ==2) &&(userId.equals(task1.getAssignee()))){
                taskService.complete(task1.getId(), map1);
            }
        }
        //查看是否还有下一级
        Task task2 = taskService.createTaskQuery().processInstanceId(processId).singleResult();
        return task2 == null ? false : true;
    }



    /**
     * 设置审批人
     *
     * @param processId 流程ID
     * @param userId    审批人ID
     */
    public static void setAssignee(String processId, String userId) {
        //部署一个流程
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        //获取第二个节点信息
        Task task1 = taskService.createTaskQuery().processInstanceId(processId).singleResult();
        taskService.setAssignee(task1.getId(),userId);
    }


    /**
     * 获取待审批任务
     * @param userId    审批人ID
     */
    public static List<String> getTaskList(String userId) {
		//部署一个流程
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		//任务服务
		TaskService taskService = engine.getTaskService();
		List<Task> list = taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().list();
        List<String> ids =new ArrayList<>();

        for(Task task : list)
        {
            ids.add(task.getProcessInstanceId());
        }
		return ids;
    }



    /**
     * 查询已审批任务列表。
     * @param userId 用户
     * @return 已处理任务列表
     */
    public static List<String> getDoneTasks(String userId) {

        //部署一个流程
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        //任务服务
        HistoryService historyService = engine.getHistoryService();

        List<HistoricTaskInstance> taskList  = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(userId)
                .finished()
                .list();
        List<String> ids =new ArrayList<>();

        for(HistoricTaskInstance task : taskList)
        {
            ids.add(task.getProcessInstanceId());
        }
        return ids;
    }


    /**
     * 根据流程号获取所有审批人。
     * @param pid 流程号
     * @return 获取所有审批人
     */
    public static List<String> getAllAssignee(String pid) {

        //部署一个流程
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        //任务服务
        HistoryService historyService = engine.getHistoryService();

        List<HistoricTaskInstance> taskList  = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(pid)
                .finished()
                .list();
        List<String> ids =new ArrayList<>();

        for(HistoricTaskInstance task : taskList)
        {
            ids.add(task.getAssignee());
        }
        return ids;
    }


}
