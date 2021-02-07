package com.xiaosong.common.api.visitorRecord;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Prop;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.xiaosong.MainConfig;
import com.xiaosong.common.activiti.VisitorProcess;
import com.xiaosong.common.api.base.MyBaseService;
import com.xiaosong.common.api.code.CodeMsg;
import com.xiaosong.common.api.code.CodeService;
import com.xiaosong.common.api.deptUser.DeptUserService;
import com.xiaosong.constant.UserPostConstant;
import com.xiaosong.common.api.userPost.UserPostService;
import com.xiaosong.common.api.websocket.*;
import com.xiaosong.common.web.blackUser.BlackUserService;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.constant.Constant;
import com.xiaosong.constant.MyRecordPage;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.*;
import com.xiaosong.constant.MyPage;
import com.xiaosong.param.ParamService;
import com.xiaosong.util.*;
import com.xiaosong.util.Base64;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @program: innerVisitor
 * @description: 访客记录
 * @author: cwf
 * @create: 2020-01-03 14:45
 **/
public class VisitorRecordService extends MyBaseService {
    Log log = Log.getLog(VisitorRecordService.class);
    public static final VisitorRecordService me = new VisitorRecordService();
    Prop p = MainConfig.p;
    private BlackUserService blackUserService = BlackUserService.me;
    private DeptUserService deptUserService = DeptUserService.me;

    /**
     * 根据 where 条件进行查询我的邀约，邀约我的，我的访问，访问我的判断
     * 增加条件判断，如果是外网则显示外网员工用户姓名
     */
    //id=visitorId 我的邀约 id=userId 邀约我的
    public Result invite(Long userId, Integer pageNum, Integer pageSize, Integer recordType, String condition) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        //查看的是对方的信息
        String otherMan = "userId";
        String inOutType = "vr.userType";
        if ("userId".equals(condition)) {
            otherMan = "visitorId";
            inOutType = "vr.visitorType";
        }
        String coloumSql = "SELECT vr.id,IF(u.realName IS NULL or u.realName=\"\",remarkName,u.realName) realName,u.phone,u.headImgUrl,\n" +
                "\tvr.visitDate,vr.visitTime,vr.userId,vr.visitorId,vr.reason,vr.cstatus,vr.dateType\n" +
                ",vr.startDate,vr.endDate,vr.answerContent,vr.orgCode,IF(vr.companyId is null,0,vr.companyId) companyId,vr.recordType,\n" +
                "vr.replyDate,vr.replyTime,vr.vitype,vr.replyUserId,vr.isReceive,o.org_name,if(" + inOutType + "='out',vr.exp1,d.dept_name) companyName,o.visitor_access_type accessType ";
        String from = " from " + TableList.VISITOR_RECORD + " vr\n" +
                "left join " + TableList.DEPT_USER + " u on u.id=vr." + otherMan + "\n" +
                "left join " + TableList.DEPT + " d on vr.companyId=d.id\n" +
                "left join  " + TableList.ORG + " o on vr.orgCode=o.org_code " +
                "where vr." + condition + "=" + userId + " and recordType=" + recordType;
        String oderBy = " ORDER BY startDate>NOW() desc,  IF(startDate > NOW(), FIELD(cstatus,'Cancle','applyFail',  'applySuccess','applyConfirm'), startDate ) desc,startDate desc,endDate";
        String totalRowSql = "select count(*) " + from;
        log.info(coloumSql + from + oderBy);
        //jfinal中的分页对象
        Page<Record> recordPage = Db.paginateByFullSql(pageNum, pageSize, totalRowSql, coloumSql + from + oderBy);
        List<Record> list = recordPage.getList();
        //查看未过期记录的条数
        String count = Db.queryStr("select count(*) num from " + TableList.VISITOR_RECORD + "  where visitorId = " + userId + " and cstatus='applyConfirm' and endDate>SYSDATE() and recordType=" + recordType + "  ");
        //转换为api接口对象
        MyRecordPage myPage = new MyRecordPage(apiList(list), pageNum, pageSize, recordPage.getTotalPage(), recordPage.getTotalRow());
        return ResultData.dataResultCount("success", "获取成功", myPage, count);
    }

    /**
     * websocket回应邀约/访问
     * 回应时将isReceive 字段改为'F' 表示对方未接收
     */
    public void visitReply(Session session, JSONObject msg) throws Exception {
        //根据Id获取需要更新的类容
        String replyDate = DateUtil.getCurDate();
        String replyTime = DateUtil.getCurTime();
        Long id = msg.getLong("id");
        //登入人
        String fromUserId = WebSocketEndPoint.me.getUserId(session.getQueryString());
        VVisitorRecord visitorRecord = VVisitorRecord.dao.findById(id);

        String cstatus = BaseUtil.objToStr(msg.get("cstatus"), null);
        String answerContent = BaseUtil.objToStr(msg.get("answerContent"), null);
        visitorRecord.setId(id).setCstatus(cstatus).setAnswerContent(answerContent)
                .setReplyDate(replyDate).setReplyTime(replyTime)
                .setReplyUserId(Long.valueOf(fromUserId)).setIsReceive("F");
        RemoteEndpoint.Async userRemote = session.getAsyncRemote();
        try {
            if (visitorRecord.update()) {
                userRemote.sendText(Result.ResultCodeType("success", "发送成功", "200", 3));
                //返回回消息
                String toUserId = msg.getString("toUserId");
                    VDeptUser toUser = VDeptUser.dao.findById(toUserId);
                    String notification_title = "预约访问信息提醒";
                    String deviceToken = BaseUtil.objToStr(toUser.get("deviceToken"), null);
                    String msg_content = "【朋悦比邻】您好，您有一条预约访问已审核，请登入app查收!";
                    String realName = BaseUtil.objToStr(toUser.get("realName"), "");
                    String startDate = BaseUtil.objToStr(visitorRecord.get("startDate"), "");
                    String isOnlineApp = BaseUtil.objToStr(toUser.get("isOnlineApp"), "F");
                    String phone = BaseUtil.objToStr(toUser.get("phone"), "0");
                    String visitorResult = "审核不成功";
                    if ("applySuccess".equals(cstatus)) {
                        visitorResult = "审核成功";
                    }
                CodeService.me.pushMsg(toUser, CodeMsg.MSG_VISITOR_PASS);
                    //CodeService.me.pushMsg(toUser, 3, visitorResult, realName, startDate, null);
                log.info("发送个推推送成功设备号{}", deviceToken);
                WebSocketSyncData.me.sendVisitorData();
//              }
            } else {
                userRemote.sendText(Result.ResultCodeType("success", "发送失败", "-1", 3));
            }
        } catch (Exception e) {
            e.printStackTrace();
            userRemote.sendText(Result.ResultCodeType("fail", "系统错误", "500", 3));
            return;
        }
    }

    /**
     * 通过接口回应邀约 1.更新访客日志表 2.发送推送给邀约人
     *
     * @author cwf
     * @date 2019/12/4 18:03
     */
    public Result visitReply(VVisitorRecord visitorRecord,String assignee) {
        try {
            String replyDate = DateUtil.getCurDate();
            String replyTime = DateUtil.getCurTime();
            VDeptUser vDeptUser =  VDeptUser.dao.findById(visitorRecord.getUserId());

            Integer id = BaseUtil.objToInteger(visitorRecord.get("id"), null);
            //登入人
            Long userId = BaseUtil.objToLong(visitorRecord.get("userId"), null);
            String cstatus = BaseUtil.objToStr(visitorRecord.get("cstatus"), null);
            String answerContent = BaseUtil.objToStr(visitorRecord.get("answerContent"), "");

            boolean hasCarAuth = UserPostService.me.checkPostAuth(userId, UserPostConstant.APPROVE_VISITOR_POST);
            if(!hasCarAuth)
            {
                return ResultData.unDataResult("fail", "没有访客审批权限");
            }

            //更新邀约信息
            if (id == null || userId == null || cstatus == null || vDeptUser ==null) {
                return Result.unDataResult("fail", "缺少参数");
            }
            visitorRecord.remove("userId").setReplyDate(replyDate).setReplyTime(replyTime).setIsReceive("F");
            VVisitorRecord vVisitorRecord = VVisitorRecord.dao.findById(visitorRecord.getId());
            if(vVisitorRecord == null)
            {
                return Result.unDataResult("fail", "记录不存在");
            }
            boolean flag = "applySuccess".equals(cstatus);
            boolean hasNext =VisitorProcess.approve(vVisitorRecord.getProcessId(),flag,assignee,deptUserService.getUserType(vDeptUser,0));
            boolean update = Db.tx(()->{
                String status = hasNext?"applyConfirm":cstatus;
                visitorRecord.setReplyDate(replyDate).setReplyTime(replyTime).setIsReceive("F");
                visitorRecord.setReplyUserId(userId);
                visitorRecord.setCstatus(status);
                boolean result = visitorRecord.update();

                if(hasNext && StringUtils.isNotEmpty(assignee))
                {
                    VDeptUser assigneeUser = VDeptUser.dao.findById(assignee);
                    CodeService.me.pushMsg(assigneeUser, CodeMsg.MSG_CAR_APPROVE);
                }
                else if(!hasNext)
                {
                    VDeptUser visitor = VDeptUser.dao.findById(vVisitorRecord.getUserId());
                    String content = CodeMsg.MSG_VISITOR_NOPASS;
                    if("applySuccess".equals(status)){
                        content =  CodeMsg.MSG_VISITOR_PASS;
                    }
                    CodeService.me.pushMsg(visitor, content);
                }


//                //随行人员同时更新
//                List<VVisitorRecord> entourages = VVisitorRecord.dao.find("select * from "+TableList.VISITOR_RECORD+" where pid =?",visitorRecord.getId());
//                for(VVisitorRecord record : entourages)
//                {
//                    record.setReplyDate(replyDate);
//                    record.setIsReceive("F");
//                    record.setReplyTime(replyTime);
//                    record.setAnswerContent(answerContent);
//                    record.setCstatus(status);
//                    record.update();
//                }
                return result;
            });

            String apply = "同意";
            if ("applyFail".equals(cstatus)) {
                apply = "拒绝";
            }
            if (update) {
                //返回回消息
    /*            Map<String, Object> visitorMap = Db.findById(TableList.VISITOR_RECORD, id).getColumns();
                Long toUserId = BaseUtil.objToLong(visitorMap.get("visitorId"), null);
                WebSocketEndPoint webSocketEndPoint = WebSocketMapUtil.get(String.valueOf(toUserId));
                System.out.println("用户" + toUserId + "是否在线：" + webSocketEndPoint);
                //发送websocket
                if (webSocketEndPoint != null) {
                    JSONObject obj = new JSONObject();
                    obj.put("orgName", "无");
                    obj.put("companyId", "无");
                    obj.put("fromUserId", userId);
                    Integer companyId = BaseUtil.objToInteger(visitorMap.get("companyId"), 0);
                    String orgCode = BaseUtil.objToStr(visitorMap.get("orgCode"), null);
                    visitorMap.remove("companyId");
                    visitorMap.remove("orgCode");
                    for (Map.Entry<String, Object> entry : visitorMap.entrySet()) {
                        if (entry.getValue() == null) {
                            visitorMap.put(entry.getKey(), "无");
                        }
                        obj.put(entry.getKey(), entry.getValue());
                    }
                    if (companyId != 0) {
                        Map<String, Object> comMap = Db.findById(TableList.DEPT, companyId).getColumns();
                        System.out.println(comMap);
                        obj.put("companyName", comMap.get("companyName"));
                    }
                    if (orgCode != null) {
                        String sql = "select org_name from " + TableList.ORG + " where org_code='" + orgCode + "'";
                        Map<String, Object> corgMap = Db.findFirst(sql).getColumns();
                        obj.put("orgName", corgMap.get("org_name"));
                    }
                    obj.put("type", Constant.MASSEGETYPE_REPLY);
                    obj.put("fromUserId", userId);
                    obj.put("toUserId", toUserId);
                    System.out.println("发送给toUser的消息为+" + obj);
                    RemoteEndpoint.Async asyncRemote = webSocketEndPoint.getSession().getAsyncRemote();
                    asyncRemote.sendText(obj.toJSONString());
                } else {
                    //发送推送
                    Map<String, Object> toUser = Db.findById(TableList.DEPT_USER, toUserId).getColumns();
                    String deviceToken = BaseUtil.objToStr(toUser.get("deviceToken"), "");
                    String msg_content = "【朋悦比邻】您好，您有一条邀约已回应，请登入app查收!";
                    boolean single = false;

//					String deviceType = BaseUtil.objToStr(toUser.get("deviceType"), "0");

                    String phone = BaseUtil.objToStr(toUser.get("phone"), "0");
                    //个推
                //    single = GTNotification.Single(deviceToken, phone, notification_title, msg_content, msg_content);
                    single = GTNotification.Single(toUser.getOrDefault("registration_id","").toString(),toUser.getOrDefault("app_type","").toString(), msg_content);
//						isYmSuc=shortMessageService.YMNotification(deviceToken,deviceType,notification_title,msg_content,isOnlineApp);
                    log.info("发送个推 推送成功? 设备号{}", single);
//                        if (!isYmSuc) {
//				            codeService.sendMsg(phone, 3, visitorResult, visitorBy, visitorDateTime, null);
//			                }

                }*/


                //todo 推送消息


                //websocket通知前端获取访客数量
                WebSocketMonitor.me.getVisitorData();
                WebSocketSyncData.me.sendVisitorData();
                return ResultData.dataResult("success", "审批成功",hasNext);
            } else {
                return Result.unDataResult("fail", apply + "邀约失败！");
            }
        } catch (Exception e) {
            log.error("邀请回应报错！", e);
            return Result.unDataResult("fail", "审批失败！系统错误，请联系客服！");
        }
    }

    /**
     * 非好友访问,具体流程：
     * 1内网存在用户 ->判断是否实名 ->return;
     * 2内网不存在用户 ->调用外网api接口查找用户 ->判断存在->判断是否实名->return
     */
    public Result visit(Long userId, String phone, String realName, String startDate, String endDate, String reason,String carNumber,String entourages,String visitDept) throws Exception {
        if (userId == null || phone == null || realName == null||startDate == null || endDate ==null) {
            return Result.unDataResult(ConsantCode.FAIL, "缺少用户参数!");
        }
        try{

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sdf.parse(startDate);
            sdf.parse(endDate);
        }
        catch (ParseException ex)
        {
            return Result.unDataResult(ConsantCode.FAIL, "日期格式不正确!");
        }


        //被访者
        VDeptUser visitorBy = VDeptUser.dao.findFirst("select * from " + TableList.DEPT_USER + " " +
                "where currentStatus ='normal' and userType !='visitor' and phone=?", phone);


        boolean hasAuth = UserPostService.me.checkPostAuth(visitorBy.getId(), UserPostConstant.APPROVE_VISITOR_POST);
        if(!hasAuth)
        {
            return ResultData.unDataResult("fail", "被访者没有审核访客权限");
        }


        //访者
        VDeptUser visitUser = VDeptUser.dao.findById(userId);
        if (visitUser == null) {
            return Result.unDataResult(ConsantCode.FAIL, "用户信息错误!");
        }

        if (visitorBy == null) {
            //return visitOutApi(visitUser, phone, realName, startDate, endDate);
            return Result.unDataResult(ConsantCode.FAIL, "受访者信息错误!");

        }

        if (!realName.equals(visitorBy.getRealName())) {
            return Result.unDataResult("fail", "用户姓名与手机不匹配!");
        }
        //查看访客是否实名
        //被访者id
        Long visitorId = visitorBy.getId();
        String isAuth = BaseUtil.objToStr(visitorBy.get("isAuth"), "");
        if (userId.equals(visitorId)) {
            return Result.unDataResult("fail", "请不要对自己发起访问！");
        }
        if (!"T".equals(isAuth)) {
            log.info("被访者未实名！:{}", visitorId);
            return Result.unDataResult("fail", "被访者未实名！");
        }
        if (visitorBy.getDeptId() == null) {
            Object existUser = Db.queryFirst("select 1 from " + TableList.DEPT_USER + " where id=" + visitorId + " and currentStatus" +
                    "='normal' and status='applySuc'");
            if (existUser == null) {
                return Result.unDataResult("fail", "被访者无归属部门！");
            }
        }
        String deviceToken = visitorBy.getDeviceToken();
        String isOnlineApp = visitorBy.getIsOnlineApp();
        //被访者姓名
        String visitorByName = BaseUtil.objToStr(visitorBy.get("realName"), null);
        //访问者姓名
        Record userInfo = Db.findFirst("select * from " + TableList.DEPT_USER + " where id =?", userId);
        String userName = userInfo.getStr("realName");
        String idNO = userInfo.getStr("idNO");
        //如果是访问recordType=1
        //查询内部是否有邀约信息
/*        Integer integer = Db.queryInt(Db.getSql("visitRecord.check"), userId, visitorId, Constant.VISITOR, endDate, startDate);
        //如果是邀约recordType=2 访客与被访者在数据库中位置调换
        if (integer != null) {
            //发送回消息
            log.info(startDate + "该时间段" + endDate + "内已经有邀约信息存在");
            return Result.unDataResult("fail", "在" + startDate + "——" + endDate + "内已经有邀约信息存在");
        }*/

        final List<VDeptUser> entourageList = new ArrayList<>();

        boolean result = Db.tx(()->{

            VVisitorRecord visitRecord = new VVisitorRecord();
            visitRecord.set("userId", userId);
            visitRecord.set("visitorId", visitorId);
            visitRecord.set("cstatus", "applyConfirm");
            visitRecord.set("visitDate", DateUtil.getCurDate());
            visitRecord.set("visitTime", DateUtil.getCurTime());
            visitRecord.set("reason", reason);
            visitRecord.set("startDate", startDate);
            visitRecord.set("endDate", endDate);
            visitRecord.set("vitype", "F");
            visitRecord.set("recordType", 1);
            visitRecord.setPlate(carNumber);
            visitRecord.setVisitDept(visitDept);

            //判断访问者是否是黑名单人员
            VBlackUser blackUser =  blackUserService.findBalckUser(userName,idNO);
            if(blackUser != null){
                visitRecord.setCstatus("applyFail");
                visitRecord.setReplyDate(DateUtil.getCurDate());
                visitRecord.setReplyTime(DateUtil.getCurTime());
                visitRecord.setReplyUserId(0L);
            }
            visitRecord.save();

            if(!"applyFail".equals(visitRecord.getCstatus())) {
                String processId = VisitorProcess.createNewProcess(String.valueOf(userId), deptUserService.getUserType(visitorBy,0), String.valueOf(visitorId));
                visitRecord.setProcessId(processId);
                visitRecord.update();
            }

            if(StringUtils.isNotBlank(entourages))
            {
                JSONArray jsonArray = JSONArray.parseArray(entourages);
                for(int i=0;i<jsonArray.size();i++)
                {
                    JSONObject jsonUser = jsonArray.getJSONObject(i);
                    String name = jsonUser.getString("name");
                    String userPhone = jsonUser.getString("phone");
                    //判断随行人员是否注册过
                    VDeptUser vDeptUser =null;
                    try {
                        vDeptUser = deptUserService.createVisitor(userPhone, name);
                    }
                    catch (Exception ex)
                    {
                        continue;
                    }
                    entourageList.add(vDeptUser);
                    VVisitorRecord record = new VVisitorRecord();
                    record.set("userId", vDeptUser.getId());
                    record.set("visitorId", visitorId);
                    record.set("cstatus", "applyConfirm");
                    record.set("visitDate", DateUtil.getCurDate());
                    record.set("visitTime", DateUtil.getCurTime());
                    record.set("reason", reason);
                    record.set("startDate", startDate);
                    record.set("endDate", endDate);
                    record.set("vitype", "F");
                    record.set("recordType", 1);
                    record.setPlate(carNumber);
                    record.setVisitDept(visitDept);
                    record.setPid(visitRecord.getId()!=null?visitRecord.getId().longValue():null);
                    if(blackUser != null){
                        record.setCstatus("applyFail");
                        record.setReplyDate(DateUtil.getCurDate());
                        record.setReplyTime(DateUtil.getCurTime());
                        record.setReplyUserId(0L);
                    }
                    record.save();
                    if(!"applyFail".equals(record.getCstatus())) {
                        String processId = VisitorProcess.createNewProcess(String.valueOf(vDeptUser.getId()), deptUserService.getUserType(visitorBy,0), String.valueOf(visitorId));
                        record.setProcessId(processId);
                        record.update();
                    }

                }
            }
            return true;
        });


        //记录访问记录
        if (result) {
            String notification_title = "访客-审核通知";
            String msg_content = "【朋悦比邻】您好，您有一条预约访客需审核，访问者:" + userName + "，被访者:" + visitorByName + ",访问时间:"
                    + startDate;
            //发送到 websocket
            WebSocketVisitor.me.sendReceiveVisitMsg(visitUser.getIdNO(),visitUser.getRealName(),startDate,endDate,"applyConfirm");

            if ("F".equals(isOnlineApp)) {
                CodeService.me.pushMsg(visitorBy, CodeMsg.MSG_STAFF_APPROVE);
                //CodeService.me.pushMsg(visitorBy, 5, null, null, startDate, userName);
                log.info(visitorByName + "：发送短信推送成功");
            } else {
               // boolean single = GTNotification.Single(deviceToken, phone, notification_title, msg_content, msg_content);
//				isYmSuc = shortMessageService.YMNotification(deviceToken, deviceType, notification_title, msg_content, isOnlineApp);
                log.info("发送个推成功{}", visitorByName);
                //if (!single) {
                    CodeService.me.pushMsg(visitorBy, CodeMsg.MSG_STAFF_APPROVE);
                    /*CodeService.me.pushMsg(visitorBy, 5, null, null, startDate, userName);
                    //发送随行人员短信
                    for(VDeptUser vDeptUser : entourageList)
                    {
                        if("T".equals(vDeptUser.getIsAuth()))
                        {
                            CodeService.me.pushMsg(vDeptUser, YunPainSmsUtil.MSG_TYPE_ENTOURAGE_AUTH, null, null, startDate, userName);
                        }else {
                            CodeService.me.pushMsg(vDeptUser, YunPainSmsUtil.MSG_TYPE_ENTOURAGE_NOAUTH, null, null, startDate, userName);
                        }
                    }*/
                    log.info(visitorByName + "：发送短信推送成功");
               // }
            }

            //websocket通知前端获取访客数量
            WebSocketMonitor.me.getVisitorData();
            WebSocketSyncData.me.sendVisitorData();

            return Result.unDataResult("success", "申请成功");
        } else {
            return Result.unDataResult("fail", "申请失败");
        }
    }




    /**
     * 车辆访问
     */
    public Result carVisit(Long userId, String name,String idcard,String phone, String realName, String startDate, String endDate, String reason,String carNumber,Integer num,String visitDept,String gate,Integer inOutType,String entourages) throws Exception {

        if ( userId == null || phone == null || realName == null||startDate == null || endDate ==null||visitDept==null) {
            return Result.unDataResult(ConsantCode.FAIL, "缺少用户参数!");
        }
        try{

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sdf.parse(startDate);
            sdf.parse(endDate);
        }
        catch (ParseException ex)
        {
            return Result.unDataResult(ConsantCode.FAIL, "日期格式不正确!");
        }

        //被访者
        VDeptUser visitorBy = VDeptUser.dao.findFirst("select * from " + TableList.DEPT_USER + " " +
                "where currentStatus ='normal' and userType !='visitor' and phone=?", phone);


        boolean hasAuth = UserPostService.me.checkPostAuth(visitorBy.getId(), UserPostConstant.APPROVE_CAR_POST);
        if(!hasAuth)
        {
            return ResultData.unDataResult("fail", "被访者没有审核车辆权限");
        }

        //访者
        VDeptUser visitUser = VDeptUser.dao.findById(userId);
        if (visitUser == null) {
            return Result.unDataResult(ConsantCode.FAIL, "用户信息错误!");
        }

        if (visitorBy == null) {
            return Result.unDataResult(ConsantCode.FAIL, "受访者信息错误!");

        }

        if (!realName.equals(visitorBy.getRealName())) {
            return Result.unDataResult("fail", "用户姓名与手机不匹配!");
        }
        //查看访客是否实名
        //被访者id
        Long visitorId = visitorBy.getId();
        String isAuth = BaseUtil.objToStr(visitorBy.get("isAuth"), "");
        if (userId.equals(visitorId)) {
            return Result.unDataResult("fail", "请不要对自己发起访问！");
        }
        if (!"T".equals(isAuth)) {
            log.info("被访者未实名！:{}", visitorId);
            return Result.unDataResult("fail", "被访者未实名！");
        }
        if (visitorBy.getDeptId() == null) {
            Object existUser = Db.queryFirst("select 1 from " + TableList.DEPT_USER + " where id=" + visitorId + " and currentStatus" +
                    "='normal' and status='applySuc'");
            if (existUser == null) {
                return Result.unDataResult("fail", "被访者无归属部门！");
            }
        }
        String deviceToken = visitorBy.getDeviceToken();
        String isOnlineApp = visitorBy.getIsOnlineApp();
        //被访者姓名
        String visitorByName = BaseUtil.objToStr(visitorBy.get("realName"), null);
        //访问者姓名
       // Record userInfo = Db.findFirst("select * from " + TableList.DEPT_USER + " where id =?", userId);
       // String userName = userInfo.getStr("realName");
        //String idNO = userInfo.getStr("idNO");



        boolean result = Db.tx(()->{

            VCar vCar = new VCar();
            vCar.setVisitId( userId);
            vCar.setIntervieweeId(visitorId);
            vCar.setCStatus( "applyConfirm");
            vCar.setVisitDate(DateUtil.getCurDate());
            vCar.setVisitTime( DateUtil.getCurTime());
            vCar.setReason(reason);
            vCar.setStartDate( startDate);
            vCar.setEndDate(endDate);
            vCar.setRecordType(1);
            vCar.setPlate(carNumber);
            vCar.setVisitDept(visitDept);
            vCar.setVisitPhone(phone);
            vCar.setVisitName(visitorByName);
            vCar.setInOutType(inOutType);
            vCar.setNum(num);
            vCar.setGate(gate);
            vCar.setUserName(visitUser.getRealName());
            vCar.setIdNO(visitUser.getIdNO());
            vCar.setEntourages(entourages);
            //判断访问者是否是黑名单人员
            VBlackUser blackUser =  blackUserService.findBalckUser(visitUser.getRealName(),visitUser.getIdNO());
            if(blackUser != null){
                vCar.setCStatus("applyFail");
                vCar.setReplyDate(DateUtil.getCurDate());
                vCar.setReplyTime(DateUtil.getCurTime());
                vCar.setReplyUserId(0L);
                return vCar.save();
            }
            String processId = VisitorProcess.createNewCarProcess(String.valueOf(userId),deptUserService.getUserType(visitorBy,1),String.valueOf(visitorId));
            vCar.setProcessId(processId);
            return vCar.save();
        });

        //记录访问记录
        if (result) {
            //todo userName
            CodeService.me.pushMsg(visitorBy, CodeMsg.MSG_CAR_APPROVE);
            //CodeService.me.pushMsg(visitorBy, 5, null, null, startDate, "");
            log.info(visitorByName + "：发送短信推送成功");
            return Result.unDataResult("success", "申请成功");
        } else {
            return Result.unDataResult("fail", "申请失败");
        }
    }




    /**
     * 访问外部Api
     *
     * @param phone     被访者手机
     * @param realName  被访者真实姓名
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @param visitUser 访客信息
     * @return
     */
    private Result visitOutApi(VDeptUser visitUser, String phone, String realName, String startDate, String endDate) {
        VVisitorRecord visitorRecord = new VVisitorRecord();
        //先保存信息，再回滚
        visitorRecord.setUserId(visitUser.getId())
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setRecordType(1)
                .setVisitDate(DateUtil.getCurDate())
                .setVisitTime(DateUtil.getCurTime())
                .setCstatus("applyConfirm")
                .setVitype("F")
                .save();
        //地址换为生产接口，如果返回有值则用新值
        String url = p.get("apiUrl") + "/visitor/visitorRecord/innerVisitRequest/";
        //jfinal中的hashMap封装类 暂时用手机号代替userCode
        Kv data = Kv.by("realName", realName).set("phone", phone).set("userCode", phone)
                .set("userRealName", visitUser.getRealName()).set("userPhone", visitUser.getPhone())
                .set("startDate", startDate).set("endDate", endDate).set("routerId", p.get("routerId"))
                .set("originId", visitorRecord.getId().toString());
        String ret = HttpKit.post(url, data, null);//转化为map对象或JsonObject
        JSONObject jsonObject = JSON.parseObject(ret);
        Map<String, Object> verify = (Map<String, Object>) jsonObject.get("verify");
        //如果返回值为false
        if (!"success".equals(verify.get("sign"))) {
            String desc = String.valueOf(verify.get("desc"));
            throw new RuntimeException(desc);
            //找不到用户
        } else {
            //将用户插入out_user数据库，并生成record表
            VOutVisitor vOutVisitor = new VOutVisitor();
            vOutVisitor.setIsAuth("T").setRealName(realName).setPhone(phone).setUserCode(phone);
            //外部用户是否存在
            AtomicReference<Long> id = new AtomicReference<>(Db.queryLong("select id from " + TableList.OUT_VISIT + " where realName=? and phone=?", realName, phone));
            //事务
            boolean tx = Db.tx(() -> {
                if (id.get() == null) {
                    vOutVisitor.save();
                    id.set(vOutVisitor.getId());
                }
                long o = BaseUtil.objToLong(jsonObject.get("data"), 0L);
                visitorRecord.setUserType("in").setVisitorType("out").setUserId(visitUser.getId())
                        .setVisitorId(id.get()).setOutRecordId(o)
                        .update();
                return true;
            });
            if (tx) {
                //websocket通知前端获取访客数量
                WebSocketMonitor.me.getVisitorData();
                WebSocketSyncData.me.sendVisitorData();
                return Result.unDataResult("success", "访问外部用户成功");
            } else {
                return Result.unDataResult("fail", "访问外部用户失败");
            }
        }
    }

    /**
     * 接收外网访问接口
     *
     * @param visitorRecord 访客记录
     * @param vOutVisitor   外部用户
     *                      接收外部访问信息，生成record表数据以及out_visitor数据
     * @return
     */
    public Result receiveOutVisit(VVisitorRecord visitorRecord, VOutVisitor vOutVisitor) {
        AtomicReference<Long> id = new AtomicReference<>(Db.queryLong("select id from " + TableList.OUT_VISIT + " where phone=? and realName=?", vOutVisitor.getPhone(), vOutVisitor.getRealName()));
        int check = check(visitorRecord.getUserId(), visitorRecord.getVisitorId(), visitorRecord.getRecordType(), visitorRecord.getStartDate()
                , visitorRecord.getEndDate(), null, "out");
        if (check > 0) {
            log.info(visitorRecord.getStartDate() + "该时间段" + visitorRecord.getEndDate() + "内已经有邀约信息存在");
            return Result.unDataResult("fail", "在" + visitorRecord.getStartDate() + "——" + visitorRecord.getEndDate() + "内已经有访问信息存在");
        }
        boolean tx = Db.tx(() -> {
            if (id.get() == null) {
                vOutVisitor.setIsAuth("T");
                vOutVisitor.save();
            }
            visitorRecord.remove("id");
            visitorRecord.save();
            return true;
        });
        //websocket通知前端获取访客数量
        WebSocketMonitor.me.getVisitorData();
        WebSocketSyncData.me.sendVisitorData();
        return tx ? Result.unDataResult("success", "操作成功") :
                Result.unDataResult("fail", "操作失败");
    }

    /**
     * 非好友邀约 暂时没有思路 如何邀约非好友，不同数据库如何交互？，非好友数据储存在生产库还是本地库？如何统一不同数据库？
     */
    public Result inviteStranger(Integer visitorId, String phone, String realName, String startDate, String endDate, String reason, Integer companyId,String carNumber,String entourages,String visitDept) throws Exception {

        if (visitorId == null || "".equals(phone) || "".equals(realName)) {
            return Result.unDataResult(ConsantCode.FAIL, "缺少用户参数!");
        }

        try{

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sdf.parse(startDate);
            sdf.parse(endDate);
        }
        catch (ParseException ex)
        {
            return Result.unDataResult(ConsantCode.FAIL, "日期格式不正确!");
        }

        boolean hasCarAuth = UserPostService.me.checkPostAuth(visitorId.longValue(), UserPostConstant.INVITE_VISITOR_POST);
        if(!hasCarAuth)
        {
            return ResultData.unDataResult("fail", "没有邀约权限");
        }
            VDeptUser invitor =null;
            try {
               invitor = deptUserService.createVisitor(phone, realName);
            }catch (Exception ex)
            {
                return Result.unDataResult("fail", ex.getMessage());
            }

            String invitorName = BaseUtil.objToStr(invitor.getRealName(), "");
            String invitorIdNO = BaseUtil.objToStr(invitor.getIdNO(), "");
//        //被邀者==访问者id
            Integer userId = BaseUtil.objToInteger(invitor.getId(), 0);
            if (userId.equals(visitorId)) {
                return Result.unDataResult("fail", "请不要对自己发起邀约！");
            }
//        // 查询登入者/邀约者姓名
            String sql = "select * from " + TableList.DEPT_USER + " where id=" + visitorId;
            Record visitor = Db.findFirst(sql);

            final List<VDeptUser> entourageList = new ArrayList<>();
            boolean result = Db.tx(()->{
                VVisitorRecord visitRecord = new VVisitorRecord();
                visitRecord.set("userId", userId);
                visitRecord.set("visitorId", visitorId);
                visitRecord.set("cstatus", "applySuccess");
                visitRecord.set("visitDate", DateUtil.getCurDate());
                visitRecord.set("visitTime", DateUtil.getCurTime());
                visitRecord.set("reason", reason);
                visitRecord.set("startDate", startDate);
                visitRecord.set("endDate", endDate);
                visitRecord.set("vitype", "A");
                //visitRecord.set("orgCode", orgCode);
                visitRecord.set("companyId", companyId);
                visitRecord.set("recordType", 2);
                visitRecord.set("remarkName", realName);
                //提示为非好友邀约
                visitRecord.set("answerContent", "非好友邀约");
                visitRecord.setPlate(carNumber);
                visitRecord.setVisitDept(visitDept);
                visitRecord.save();

                if(StringUtils.isNotBlank(entourages))
                {
                    JSONArray jsonArray = JSONArray.parseArray(entourages);
                    for(int i=0;i<jsonArray.size();i++)
                    {
                        JSONObject jsonUser = jsonArray.getJSONObject(i);
                        String name = jsonUser.getString("name");
                        String userPhone = jsonUser.getString("phone");
                        //判断随行人员是否注册过
                        VDeptUser vDeptUser =null;
                        try {
                            vDeptUser = deptUserService.createVisitor(userPhone, name);
                        }
                        catch (Exception ex)
                        {
                           continue;
                        }
                        entourageList.add(vDeptUser);
                        VVisitorRecord record = new VVisitorRecord();
                        record.set("userId", vDeptUser.getId());
                        record.set("visitorId", visitorId);
                        record.set("cstatus", "applySuccess");
                        record.set("visitDate", DateUtil.getCurDate());
                        record.set("visitTime", DateUtil.getCurTime());
                        record.set("reason", reason);
                        record.set("startDate", startDate);
                        record.set("endDate", endDate);
                        record.set("vitype", "F");
                        record.set("recordType", 2);
                        record.setPlate(carNumber);
                        record.setVisitDept(visitDept);
                        record.setPid(visitRecord.getId()!=null?visitRecord.getId().longValue():null);
                        record.save();
                    }
                }
                //CarService.me.addCarInfo(visitRecord);
                return true;
            });

            if (result) {
                //发送到 websocket
                WebSocketVisitor.me.sendReceiveVisitMsg(invitorIdNO,invitorName,startDate,endDate,"applyConfirm");
                //CodeService.me.pushMsg(invitor.getRegistrationId(),invitor.getAppType(),phone, YunPainSmsUtil.MSG_TYPE_INVITE, "", "", endDate, realName, startDate, visitorName);
                CodeService.me.pushMsg(invitor, CodeMsg.MSG_VISITOR_PASS);
                //发送随行人员短信
                for(VDeptUser vDeptUser : entourageList)
                {
                    CodeService.me.pushMsg(vDeptUser, CodeMsg.MSG_VISITOR_PASS);
             /*       if("T".equals(vDeptUser.getIsAuth()))
                    {
                        CodeService.me.pushMsg(vDeptUser.getRegistrationId(),vDeptUser.getAppType(),vDeptUser.getPhone(), YunPainSmsUtil.MSG_TYPE_ENTOURAGE_AUTH,null,null, null, null, startDate, visitorName);
                    }else {
                        CodeService.me.pushMsg(vDeptUser.getRegistrationId(),vDeptUser.getAppType(),vDeptUser.getPhone(), YunPainSmsUtil.MSG_TYPE_ENTOURAGE_NOAUTH,null,null,  null, null, startDate, visitorName);
                    }*/
                }
                //websocket通知前端获取访客数量
                WebSocketMonitor.me.getVisitorData();
                WebSocketSyncData.me.sendVisitorData();
                return Result.unDataResult("success", "邀约成功");
            } else {
                return Result.unDataResult("fail", "邀约失败");
            }
    }


    /**
     * 邀约车辆
     * @param visitorId
     * @param startDate
     * @param endDate
     * @param reason
     * @return
     * @throws Exception
     */
    public Result inviteCar(Long visitorId, String startDate, String endDate, String gate,Integer inOutType, String reason,String cars) throws Exception {

        if (visitorId == null || StringUtils.isEmpty(cars)) {
            return Result.unDataResult(ConsantCode.FAIL, "缺少用户参数!");
        }

        VDeptUser visitorBy = VDeptUser.dao.findById(visitorId);
        if (visitorBy == null) {
            return Result.unDataResult(ConsantCode.FAIL, "未找到用户信息!");
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sdf.parse(startDate);
            sdf.parse(endDate);
        } catch (ParseException ex) {
            return Result.unDataResult(ConsantCode.FAIL, "日期格式不正确!");
        }

        boolean hasCarAuth = UserPostService.me.checkPostAuth(visitorId.longValue(), UserPostConstant.INVITE_CAR_POST);
        if (!hasCarAuth) {
            return ResultData.unDataResult(ConsantCode.FAIL, "没有邀约权限");
        }

        VDept vDept = VDept.dao.findById(visitorBy.getDeptId());

        boolean hasNext = false;
        boolean result = false;
        int userType = deptUserService.getUserType(visitorBy, 1);
        JSONArray jsonArray = JSONArray.parseArray(cars);
        Record user_key = Db.findFirst("select * from v_user_key");

        List<Long> carIds = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonUser = jsonArray.getJSONObject(i);
            String name = jsonUser.getString("name");
            String userPhone = jsonUser.getString("phone");
            String idNo = jsonUser.getString("idNo");
            String carNumber = jsonUser.getString("carNumber");
            idNo = DESUtil.encode(user_key.getStr("workKey"), idNo);

            VDeptUser vDeptUser = null;
            try {
                vDeptUser = deptUserService.createVisitor(userPhone, name);
            } catch (Exception ex) {
                return Result.unDataResult("fail", ex.getMessage());
            }
            VCar vCar = new VCar();
            vCar.setIntervieweeId(visitorId);
            vCar.setCStatus("applyConfirm");
            vCar.setVisitDate(DateUtil.getCurDate());
            vCar.setVisitTime(DateUtil.getCurTime());
            vCar.setReason(reason);
            vCar.setStartDate(startDate);
            vCar.setEndDate(endDate);
            vCar.setRecordType(2);
            vCar.setPlate(carNumber);
            vCar.setVisitPhone(userPhone);
            vCar.setInOutType(inOutType);
            vCar.setNum(1);
            vCar.setGate(gate);
            vCar.setUserName(name);
            vCar.setVisitName(visitorBy.getRealName());
            vCar.setIdNO(idNo);
            vCar.setVisitId(vDeptUser.getId());
            String processId = VisitorProcess.createNewCarProcess(String.valueOf(vDeptUser.getId()), deptUserService.getUserType(visitorBy, 1), String.valueOf(visitorId));
            vCar.setProcessId(processId);
            if(vDept!=null)
            {
                vCar.setVisitDept(vDept.getDeptName());
            }

            boolean flag = true;
            hasNext = VisitorProcess.approveCar(processId, flag, null, userType);
            vCar.setCStatus(hasNext ? "applyConfirm" : "applySuccess");
            result = vCar.save();
            carIds.add(vCar.getId());
        }


        if (result) {
//            //发送随行人员短信
//            for(VDeptUser vDeptUser : entourageList)
//            {
//
//                if("T".equals(vDeptUser.getIsAuth()))
//                {
//                    CodeService.me.pushMsg(vDeptUser, CodeMsg.MSG_VISITOR_PASS);
//                    //CodeService.me.pushMsg(vDeptUser.getRegistrationId(),vDeptUser.getAppType(),vDeptUser.getPhone(), YunPainSmsUtil.MSG_TYPE_ENTOURAGE_AUTH,null,null, null, null, startDate, visitorName);
//                }else {
//                    CodeService.me.pushMsg(vDeptUser.getRegistrationId(),vDeptUser.getAppType(),vDeptUser.getPhone(), YunPainSmsUtil.MSG_TYPE_ENTOURAGE_NOAUTH,null,null,  null, null, startDate, visitorBy.getRealName());
//                }
//            }


            HashMap<String, Object> resultMap = new HashMap<>();
            boolean isManage = false;
            //还需要审核的时候，获取到当前人员类型，如果是领导，那么返回type 给前端，让前端跳转到经办岗页面
            resultMap.put("hasNext", hasNext);
            if (hasNext && userType == 1) {
                isManage = true;
            }
            resultMap.put("isManage", isManage);
            resultMap.put("carIds",carIds);

            //websocket通知前端获取访客数量
            WebSocketMonitor.me.getVisitorData();
            WebSocketSyncData.me.sendVisitorData();
            return ResultData.dataResult("success", "邀约成功",resultMap);
        } else {
            return Result.unDataResult("fail", "邀约失败");
        }
    }





    /**
     * 回应访问接口整合,改为先回应，最后再给管理员审核
     */
    public Result modifyCompanyFromId(String id, Long userId, Long companyId, String cstatus, String answerContent) throws Exception {
        if (id == null) {
            return Result.unDataResult("fail", "缺少参数!");
        }
        VVisitorRecord visitorRecord = VVisitorRecord.dao.findById(id);
        if (visitorRecord == null) {
            return Result.unDataResult("fail", "无效记录!");
        }
        Long visitorId = visitorRecord.getVisitorId();
        String userType = visitorRecord.getUserType();//外网访问
        //处理外网访客
        if ("out".equals(userType)) {
            //todo 修改内网record表 ，传到云record表。
            visitorRecord.setCstatus(cstatus).setReplyUserId(userId)
                    .setAnswerContent(answerContent)
                    .setCompanyId(companyId);
            //innerVisitResponse 企业用户回应
            String url = p.get("apiUrl") + "/visitor/visitorRecord/innerVisitResponse/";//云端url
            //jfinal中的hashMap封装类 暂时用手机号代替userCode
            //jfinal中的hashMap封装类 暂时用手机号代替userCode
            Record record = visitorRecord.toRecord();
            Map<String, String> newRecord = new HashMap<>();
            record.getColumns().forEach((k, v) -> {
                if (v != null) {
                    newRecord.put(k, String.valueOf(v));
                }
            });
            //转为String类型
            String ret = HttpKit.post(url, newRecord, null);//传出到api云端，获取返回值
            JSONObject jsonObject = JSON.parseObject(ret);
            Result result = new Result();
            Map<String, Object> verify = (Map<String, Object>) jsonObject.get("verify");

            result.setVerify(verify);
            if ("fail".equals(verify.get("sign"))) {
                return result;
            }
            //拒绝访问 如果失败如何回滚云端数据呢？
            if (visitorRecord.update()) {
                if ("applySuccess".equals(cstatus)) {
                    //todo 获取访客api图片 调用api接口
                    String imageServerUrl = ParamService.me.findValueByName("imageServerUrl");
                    String path = "innerUser" + File.separator + visitorRecord.getUserId() + File.separator + System.currentTimeMillis() + ".jpg";
                    String baseImage = jsonObject.getString("data");
                    byte[] bytes = Base64.decode(baseImage);//byte图片
                    File fileFromBytes = FilesUtils.getFileFromBytes(bytes, p.get("imageSaveDir"), path);
                }
                //只保存了
                return result;
            } else {
                return Result.unDataResult("success", "操作失败！");
            }
            //todo 调用外部接口 传到外网
        }
        if (!visitorId.equals(userId)) {
            return Result.unDataResult("fail", "被访者错误!");
        }
        Record visitorUser = Db.findById(TableList.DEPT_USER, userId);
        String visitorBy = visitorUser.get("realName").toString();
        //访客记录
        if (!"applyConfirm".equals(visitorRecord.get("cstatus"))) {
            return Result.unDataResult("fail", "非申请中状态!");
        }
        int update = 0;
        //访客Id 访问者
        Integer recordUserId = BaseUtil.objToInteger(visitorRecord.get("userId"), 0);
        //访客信息
        Record userUser = Db.findById(TableList.DEPT_USER, recordUserId);
//        String visitor = userUser.get("realName").toString();
        String wxOpenId = BaseUtil.objToStr(userUser.get("wx_open_id"), "");
        //websocket聊天信息
        JSONObject msg = new JSONObject();
        Map<String, String> wxMap = new HashMap<>();
        String visitorResult = "拒绝访问";
        visitorRecord.setAnswerContent(answerContent).setCstatus(cstatus)
                .setReplyDate(DateUtil.getCurDate()).setReplyTime(DateUtil.getCurTime())
                .setReplyUserId(userId);
        //websocket消息
        msg.put("toUserId", recordUserId);
        msg.put("type", Constant.MASSEGETYPE_REPLY);
        msg.put("fromUserId", visitorId);
        //微信消息
        wxMap.put("wxOpenId", wxOpenId);
        wxMap.put("visitorBy", visitorBy);
        Record orgComMap = new Record();
        String orgCode = "无";
        String companyName = "无";
        String addr = "无";
        String orgName = "无";
        //保存公司，并审核，审核失败则推送
        if ("applySuccess".equals(cstatus)) {
            if (companyId != null) {
                orgComMap = Db.findFirst("SELECT\n" +
                        "\torg_code,\n" +
                        "\torg_name,\n" +
                        "\tvisitor_access_type,\n" +
                        "\tdept_name companyName,\n" +
                        "\troleType \n" +
                        "FROM\n" +
                        "\tv_org o\n" +
                        "\tLEFT JOIN v_dept d ON d.org_id = o.id\n" +
                        "\tLEFT JOIN v_dept_user du ON du.deptId = d.id \n" +
                        "WHERE\n" +
                        "\td.id = ? \n" +
                        "\tAND du.id = ? \n" +
                        "\tAND du.currentStatus = 'normal' \n" +
                        "\tAND du.STATUS = 'applySuc' ", companyId, userId);
            }
            if (orgComMap == null) {
                return Result.unDataResult("fail", "请选择您自己所在的部门地址！");
            }
            orgCode = BaseUtil.objToStr(orgComMap.get("org_code"), "无");
            orgName = BaseUtil.objToStr(orgComMap.get("org_name"), "无");
            addr = BaseUtil.objToStr(orgComMap.get("addr"), "无");
            companyName = BaseUtil.objToStr(orgComMap.get("companyName"), "无");
//            String companyFloor = BaseUtil.objToStr(orgComMap.get("companyFloor"), "无");
//            String accessType = BaseUtil.objToStr(orgComMap.get("accessType"), "0");
            visitorRecord.setCompanyId(companyId).setOrgCode(orgCode);
            //有管理权限 直接审核成功
            visitorResult = "接受访问";
            //微信信息
            String sid = Base64.encode((id).getBytes("UTF-8"));
            //推送消息

        }
        msg.put("companyName", companyName);
        msg.put("orgName", orgName);
        msg.put("addr", addr);
        if (visitorRecord.update()) {
            WebSocketVisitor.me.visitReply(id,cstatus);
            //websocket通知前端获取访客数量
            WebSocketMonitor.me.getVisitorData();
            WebSocketSyncData.me.sendVisitorData();
            return Result.unDataResult("success", visitorResult + "成功");
        } else {
            return Result.unDataResult("fail", visitorResult + "失败");
        }
    }
    //推送




    public Result visitMyCompany(String userId, Integer pageNum, Integer pageSize) throws Exception {
        VDeptUser user = VDeptUser.dao.findById(userId);
        if (user.getDeptId() == null) {
            return Result.unDataResult("fail", "暂无部门数据!");
        }
        if (!"manage".equals(user.get("role"))) {
            return Result.unDataResult("fail", "非管理者无权查看!");
        }
        String columnSqlCompany = " select u.* ";
        String fromSqlCompany = "  from " + TableList.DEPT_USER + " u " + " left join " + TableList.DEPT_USER
                + " cu on u.companyId=cu.companyId" + " where u.companyId = '" + user.get("companyId") + "' and u.id!="
                + userId;
        List<Record> records = Db.find(columnSqlCompany + fromSqlCompany);
        String userUrl = "";
        if (records.size() < 1) {
            return Result.unDataResult("fail", "暂无同事数据!");
        }
        for (int i = 0; i < records.size(); i++) {
            userUrl = userUrl + records.get(i).get("id") + ",";
        }
        userUrl = userUrl.substring(0, userUrl.length() - 1);
        String columnSql = "select vr.*,u.realName userRealName,v.realName vistorRealName,o.province province,o.city city,o.org_name org_name,c.companyName companyName";
        String from = " from " + TableList.VISITOR_RECORD + " vr " + " left join " + TableList.DEPT_USER
                + " u on vr.userId=u.id" + " left join " + TableList.DEPT_USER + " v on vr.visitorId=v.id" + " left join "
                + TableList.DEPT + " c on vr.companyId=c.id" + " left join " + TableList.ORG + " o on c.orgId=o.id"
                + " where vr.visitorId in (" + userUrl
                + ") and vr.cstatus='applyConfirm' and vr.orgCode is not null and vr.companyId  = '" + user.get("companyId") + "' ";
        String oderBy = "order by cstatus,visitDate desc,visitTime desc";
        String totalRowSql = "select count(*) " + from;
        Page<Record> paginate = Db.paginateByFullSql(pageNum, pageSize, totalRowSql, columnSql + from + oderBy);
        //查看未过期记录的条数
        String count = Db.queryStr("select count(*) num from " + TableList.VISITOR_RECORD + "  where visitorId in(" + userUrl + ") and cstatus='applyConfirm' and endDate>SYSDATE() and orgCode is not null and companyId  = '" + user.get("companyId") + "'");
        MyPage<VVisitorRecord> myPage = new MyPage(paginate.getList(), pageNum, pageSize, paginate.getTotalPage(), paginate.getTotalRow());

        return paginate.getList().size() > 0 ? ResultData.dataResultCount("success", "获取成功", myPage, count)
                : ResultData.dataResult("success", "暂无同事数据", myPage);
    }

    /**
     * 通过记录id查找被访问者的公司信息以及姓名头像
     *
     * @param recordId 记录id
     * @return result
     */
    public Result findRecordFromId(Object recordId) {
        List<Record> list = Db.find(Db.getSql("visitRecord.findRecordFromId"), recordId,recordId);
        Record first = null;
        //随行人员
        String entourage ="";
        for(Record record : list)
        {
            //pid 为空，为主访问记录,否则是随行人员将人员姓名添加到对应的字段
            if(record.get("pid")==null || "".equals(record.get("pid").toString())) {
                first = record;
            }else{
                entourage+=record.get("userName")+",";
            }
        }
        if(entourage.length()>1)
        {
            entourage = entourage.substring(0,entourage.length()-1);
        }
        first.set("entourage",entourage);
        return first != null ? ResultData.dataResult("success", "获取成功", first.getColumns()) :
                Result.unDataResult("fail", "获取失败");
    }

    private int check(Object userId, Object toUserId,
                      Object recordType, Object startDate,
                      Object endDate, Object userType, Object visitorType) {
        userType = userType == null ? "in" : userType;
        visitorType = visitorType == null ? "in" : visitorType;

        String sql = " select id from " + TableList.VISITOR_RECORD + " where userId = '" + userId + "' and visitorId ='"
                + toUserId + "' and recordType = " + recordType + " and cstatus<>'applyFail' and userType='" + userType + "'" +
                " and STR_TO_DATE(startDate,'%Y-%m-%d %H:%i')<STR_TO_DATE('" + endDate + "','%Y-%m-%d %H:%i')" +
                " and visitorType='" + visitorType + "' and   STR_TO_DATE(endDate,'%Y-%m-%d %H:%i')>STR_TO_DATE('" + startDate + "','%Y-%m-%d %H:%i') ";
        log.info("检查是否时间段有邀约信息：{}", sql);

        Integer integer = Db.queryInt(sql);
        integer = integer == null ? 0 : integer;
        return integer;
    }

    /**
     * 接收云端发送来的回应，如果是接收访问(applySuccess)，则下发图片。
     *
     * @return
     */
    public Result innerVisitReceive(VVisitorRecord visitorRecord) {
        VVisitorRecord record = visitorRecord.findById(visitorRecord.getId());
        if (visitorRecord.update()) {
            if ("applyFail".equals(visitorRecord.getCstatus())) {
                return Result.unDataResult("success", "操作成功");
            }
            //todo 获取图片
            String idHandleImgUrl = Db.queryStr("select idHandleImgUrl from " + TableList.DEPT_USER + " where id =" + record.get("userId"));
            String imageServerUrl = ParamService.me.findValueByName("imageServerUrl");
            String photo = Base64.encode(FilesUtils.getImageFromNetByUrl(imageServerUrl + idHandleImgUrl));
            return ResultData.dataResult("success", "操作成功", photo);
            //下发图片
        }
        return Result.unDataResult("fail", "操作失败");

    }

    //新版访问记录
    public Result findRecordUser(Long userId, Integer pageNum, Integer pageSize) {
        if (userId == null) {
            return ResultData.unDataResult("fail", "缺少参数");
        }
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        String coloumSql = "select *";
        String fromSql = " from (SELECT vr.id,vr.visitorId, Max( startDate ) startDate,endDate,u.realName,idHandleImgUrl,headImgUrl,\n" +
                "IF( dept_name IS NULL, '', dept_name ) companyName,case when IFNULL(u.addr,'')='' then '省行政服务中心' else u.addr end as addr\n" +
                " FROM (\n" +
                "select id,IF(userId=" + userId + ",visitorId,userId) visitorId,startDate,endDate,companyId,orgCode\n" +
                "from " + TableList.VISITOR_RECORD + "\n" +
                "where  pid is null and (userId=" + userId + " or visitorId=" + userId + "))vr\n" +
                "LEFT JOIN " + TableList.DEPT_USER + " u ON vr.visitorId = u.id\n" +
                "LEFT JOIN " + TableList.DEPT + " d on d.id=u.deptId " +
                " where u.realName is not null\n" +
                "GROUP BY visitorId order by startDate desc)x";
        String totalRowSql = "select count(*) " + fromSql;
        log.info(coloumSql + fromSql);
        Page<Record> paginate = Db.paginateByFullSql(pageNum, pageSize, totalRowSql, coloumSql + fromSql);

        MyPage<VVisitorRecord> myPage = new MyPage(apiList(paginate.getList()), pageNum, pageSize, paginate.getTotalPage(), paginate.getTotalRow());

        return ResultData.dataResult("success", "查看成功", myPage);
    }

    //查询记录详情
    public Result findRecordUserDetail(Long userId, Long visitorId, Integer pageNum, Integer pageSize, Integer recordType) {
        if (userId == null || visitorId == null) {
            return ResultData.unDataResult("fail", "缺少参数");
        }
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;

        //查询全部
        String and = " recordType =" + recordType + " and";
        if (recordType == null) {
            and = "";
        }
        String coloumSql = "select *";
        String fromSql = "from (select vr.id,vr.userId,vr.visitorId,vr.visitDate,vr.visitTime,vr.recordType,vr.startDate,vr.endDate,vr.cstatus,\n" +
                "IF(vr.replyDate IS NULL,'',vr.replyDate) replyDate,IF(vr.replyTime IS NULL,'',vr.replyTime) replyTime," +
                "IF( dept_name IS NULL, '', dept_name ) companyName,case when IFNULL(vu.addr,'')='' then '省行政服务中心' else vu.addr end as addr,\n" +
                "(CASE WHEN vr.userId=" + userId + " AND vr.recordType=1 THEN u.realName \n" +
                "WHEN vr.userId=" + userId + " AND vr.recordType=2 THEN vu.realName \n" +
                "WHEN  vr.visitorId=" + userId + " AND vr.recordType=1 THEN u.realName  \n" +
                "WHEN  vr.visitorId=" + userId + " AND vr.recordType=2 THEN vu.realName \n" +
                "else '无' end) originator,\n" +
                "(CASE WHEN vr.userId=" + userId + " AND vr.recordType=1 THEN vu.realName \n" +
                "WHEN vr.userId=" + userId + " AND vr.recordType=2 THEN u.realName \n" +
                "WHEN  vr.visitorId=" + userId + " AND vr.recordType=1 THEN vu.realName  \n" +
                "WHEN  vr.visitorId=" + userId + " AND vr.recordType=2 THEN u.realName \n" +
                "else '无' end) receiver,\n" +
                "u.realName visitor,\n" +
                "vu.realName visited from " + TableList.VISITOR_RECORD + " vr\n" +
                "        left join " + TableList.DEPT + " c on c.id=vr.companyId\n" +
                "        left join " + TableList.DEPT_USER + " u on u.id=vr.userId" +
                "        left join " + TableList.DEPT_USER + " vu on vu.id = vr.visitorId" +
                "        where vr.pid is null and vu.realName is not null and u.realName is not null and " + and + "((userId=" + userId + " and visitorId=" + visitorId + ") or(userId=" + visitorId + " and visitorId=" + userId + "))\n" +
                "        ORDER BY startDate>NOW() desc, IF(startDate > NOW(), FIELD(cstatus,'Cancle','applyFail',\n" +
                "        'applySuccess','applyConfirm'), startDate ) desc,startDate desc,endDate asc)x";
        Record visitor = Db.findFirst("select realName from " + TableList.DEPT_USER + " where id=" + visitorId);
        String visitorName = "无";
        if (visitor != null) {
            visitorName = BaseUtil.objToStr(visitor.get("realName"), "无");
        }
        String totalRowSql = "select count(*) " + fromSql;
        Page<Record> paginate = Db.paginateByFullSql(pageNum, pageSize, totalRowSql, coloumSql + fromSql);
        log.info(coloumSql + fromSql);
        MyPage<VVisitorRecord> myPage = new MyPage(apiList(paginate.getList()), pageNum, pageSize, paginate.getTotalPage(), paginate.getTotalRow());

        return ResultData.dataResult("success", visitorName, myPage);
    }

    public Result visitorSucList(Integer pageNum, Integer pageSize, String userId) {
        if ("".equals(userId)) {
            return Result.unDataResult("fail", "查询失败，参数缺失");
        }
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        String coloumSql = " SELECT vr.id,IF(u.realName IS NULL,IF(remarkName is null,'',remarkName),u.realName) realName,u.phone,u.headImgUrl,\n" +
                "\tvr.visitDate,vr.visitTime,vr.userId,vr.visitorId,vr.reason,vr.cstatus,vr.dateType\n" +
                ",vr.startDate,vr.endDate,vr.answerContent,vr.orgCode,vr.companyId,vr.recordType,\n" +
                "vr.replyDate,vr.replyTime,vr.vitype,vr.replyUserId,vr.isReceive,c.dept_name companyName";
        String fromSql = " from " + TableList.VISITOR_RECORD + " vr\n" +
                "left join " + TableList.DEPT_USER + " u on u.id=vr.visitorId" +
                "\n" +
                "left join " + TableList.DEPT + " c on vr.companyId=c.id\n" +
                "where vr.userId=" + userId + " and cstatus='applySuccess' and u.id is not null  ";
        String oderBy = " ORDER BY startDate>NOW() desc,  IF(startDate > NOW(), FIELD(cstatus,'Cancle','applyFail',  'applySuccess','applyConfirm'), startDate ) desc,startDate desc,endDate";
        String totalRowSql = "select count(*) " + fromSql;
        Page<Record> paginate = Db.paginateByFullSql(pageNum, pageSize, totalRowSql, coloumSql + fromSql + oderBy);
        log.info(coloumSql + fromSql);
        MyPage<VVisitorRecord> myPage = new MyPage(apiList(paginate.getList()), pageNum, pageSize, paginate.getTotalPage(), paginate.getTotalRow());

        return ResultData.dataResult("success", "获取成功", myPage);
    }



    public   List<Record>  findTopRecordByNum(Integer pageSize) {

        String coloumSql = "select a.id,idNO,realName,startDate,endDate,cStatus from( select * from v_visitor_record where visitDate = DATE_SUB(curdate(),INTERVAL 0 DAY) order by replyDate desc, replyTime desc, visitDate desc,visitTime desc   limit ?) a\n" +
                "left join v_dept_user b on a.userId = b.id ";
        List<Record> list = Db.find(coloumSql,pageSize);
        return list;
    }



    public Record findRecordById(Object id) {
        String coloumSql = "select a.id,idNO,realName,startDate,endDate,cStatus from( select * from v_visitor_record where id = ? ) a\n" +
                "left join v_dept_user b on a.userId = b.id ";
        Record record = Db.findFirst(coloumSql,id);
        return record;
    }


    public List<Record> findValidList(Long userId , String time,Long createUserId,String machineCode,String orderBy){
        StringBuilder sql = new StringBuilder();
        List<Object> objects = new LinkedList<>();
        getFindValidList(sql,objects,userId,time,null,null,createUserId,machineCode,null,orderBy);
        return Db.find(sql.toString(),objects.toArray());
    }


    public Page<Record> findValidListPage(int pageNumber,int pageSize,Long userId ,String phone,Long createUserId,String machineCode,Integer recordType,String orderBy){
        StringBuilder sql = new StringBuilder();
        List<Object> objects = new LinkedList<>();
        String visitDate = DateUtil.getCurDate();
        getFindValidList(sql,objects,userId,null,visitDate,phone,createUserId,machineCode,recordType,orderBy);
        SqlPara sqlPara = new SqlPara();
        sqlPara.setSql(sql.toString());
        for(Object object : objects) {
            sqlPara.addPara(object);
        }
        return Db.paginate(pageNumber,pageSize,sqlPara);
    }



    private void getFindValidList(StringBuilder sql, List<Object> objects,Long userId , String time,String visitDate,String phone,Long createUserId,String machineCode,Integer recordType,String orderBy){
        sql.append("select v.userId visitor_id,v.id record_id, vu.isAuth is_auth,v.recordType record_type,u.addr address,d.dept_name,visitorid as staff_id,u.realName as real_name,u.phone,u.sex,v.startDate as start_date,v.endDate as end_date,v.reason,v.cstatus,v.plate visitor_plate,vu.addr visitor_cmp,vu.phone visitor_phone,vu.realName visitor_name,vu.sex visitor_sex ");
        sql.append(" from v_visitor_record v LEFT JOIN v_dept_user u on  u.id = v.visitorId LEFT JOIN v_dept_user vu on v.userId = vu.id left join v_dept d on u.deptId  = d.id  where 1=1 ");

        if(StringUtils.isNotBlank(time))
        {
            objects.add(time);
            sql.append("and ? <= v.endDate ");
        }
        if(userId!= null)
        {
            objects.add(userId);
            sql.append(" and v.userId = ? ");
        }
        if(StringUtils.isNotBlank(visitDate))
        {
            objects.add(visitDate);
            sql.append("and v.visitDate = ? ");
        }

        if(StringUtils.isNotBlank(phone))
        {
            objects.add(phone);
            sql.append("and v.vu.phone = ? ");
        }
        if(createUserId != null)
        {
            objects.add(createUserId);
            sql.append("and v.createUser = ? ");
        }

        if(StringUtils.isNotBlank(machineCode))
        {
            objects.add(machineCode);
            sql.append("and v.machineCode = ? ");
        }

        if(recordType!=null && recordType!=0)
        {
            objects.add(recordType);
            sql.append("and v.recordType = ? ");
        }

        sql.append(" order by visitDate " + orderBy);
        sql.append(" , visitTime " + orderBy+" ");
    }





    //查询所有审批的车辆
    public Result findCarList(Long userId, Integer pageNum, Integer pageSize) {
        if (userId == null) {
            return ResultData.unDataResult("fail", "缺少参数");
        }

        boolean hasCarAuth = UserPostService.me.checkPostAuth(userId, UserPostConstant.APPROVE_CAR_POST);
        if(!hasCarAuth)
        {
            return ResultData.unDataResult("fail", "没有车辆审批权限");
        }

        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;

        String sql = "select a.*,du1.realName,du2.realName visitName,r.endDate from v_car a left join v_visitor_record r on a.visitId = r.id left join v_dept_user du1 on du1.id = r.userId left join v_dept_user du2 on r.visitorId = du2.id  where r.cstatus = 'applySuccess' order by visitDate desc ,visitTime desc";
        SqlPara sqlPara = new SqlPara();
        sqlPara.setSql(sql);

        Page<Record> paginate = Db.paginate(pageNum, pageSize, sqlPara);

        MyPage<VVisitorRecord> myPage = new MyPage(apiList(paginate.getList()), pageNum, pageSize, paginate.getTotalPage(), paginate.getTotalRow());

        return ResultData.dataResult("success", "查看成功", myPage);
    }





    //查询我的审批的车辆
    public Result findMyCarList(Long userId,Integer pageNum, Integer pageSize) {
        if (userId == null) {
            return ResultData.unDataResult("fail", "缺少参数");
        }
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;

        String sql = "select su.addr,au.realName approveName ,vu.idHandleImgUrl,vu.idNo,vu.realName visitorName,vu.addr visitorCmp,vu.phone visitorPhone,c.* from v_car c LEFT JOIN v_dept_user vu on visitId = vu.id\n" +
                " LEFT JOIN v_dept_user au on approvalUserId = au.id  LEFT JOIN v_dept_user su on intervieweeId = su.id   where visitId = ? or intervieweeId=? order by visitDate desc ,visitTime desc";
        SqlPara sqlPara = new SqlPara();
        sqlPara.setSql(sql);
        sqlPara.addPara(userId);
        sqlPara.addPara(userId);
        Page<Record> paginate = Db.paginate(pageNum, pageSize, sqlPara);
        Record user_key = Db.findFirst("select * from v_user_key");
        for(Record record : paginate.getList())
        {
            String entourages =record.get("entourages");
            record.set("entourages",JSONArray.parse(entourages));
            String idNo =record.get("idNo");
            idNo = DESUtil.decode(user_key.getStr("workKey"), idNo);
            record.set("idNo",idNo);
        }
        MyPage<VVisitorRecord> myPage = new MyPage(apiList(paginate.getList()), pageNum, pageSize, paginate.getTotalPage(), paginate.getTotalRow());
        return ResultData.dataResult("success", "查看成功", myPage);
    }



    //审批车辆
    public Result approvalCar(Long userId,String status,String reason,String assignee,String carIds) {
        if (userId == null || carIds ==null || status ==null) {
            return ResultData.unDataResult("fail", "缺少参数");
        }

        VDeptUser vDeptUser =  VDeptUser.dao.findById(userId);
        if (vDeptUser == null ) {
            return ResultData.unDataResult("fail", "用户不存在");
        }

        boolean hasCarAuth = UserPostService.me.checkPostAuth(userId, UserPostConstant.APPROVE_CAR_POST);
        if(!hasCarAuth)
        {
            return ResultData.unDataResult("fail", "没有车辆审批权限");
        }
        boolean result = false;
        boolean hasNext = false;
        int userType = deptUserService.getUserType(vDeptUser, 1);
        JSONArray carList = JSONArray.parseArray(carIds);
        for(Object carId : carList) {

            VCar car = VCar.dao.findById(carId);
            if (car == null) {
                return ResultData.unDataResult("fail", "没有找到该记录");
            }

            boolean flag = "applySuccess".equals(status);
            hasNext = VisitorProcess.approveCar(car.getProcessId(), flag, assignee, userType);
            car.setCStatus(hasNext ? "applyConfirm" : status);

            car.setReplyDate(DateUtil.getCurDate());
            car.setReplyTime(DateUtil.getCurTime());
            car.setReplyUserId(userId);
            car.setApprovalDateTime(DateUtil.getSystemTime());
            car.setApprovalUserId(userId);
            car.setReason(reason);
            result = car.update();
            if("applyFail".equals(status))
            {
                VDeptUser visitor = VDeptUser.dao.findById(car.getVisitId());
                CodeService.me.pushMsg(visitor, CodeMsg.MSG_CAR_APPROVE_NOPASS);
            }
        }

        if (result) {
            HashMap<String, Object> resultMap = new HashMap<>();
            boolean isManage = false;
            //还需要审核的时候，获取到当前人员类型，如果是领导，那么返回type 给前端，让前端跳转到经办岗页面
            resultMap.put("hasNext", hasNext);
            if (hasNext && userType == 1) {
                isManage = true;
            }
            resultMap.put("isManage", isManage);

            if(hasNext && StringUtils.isNotEmpty(assignee))
            {
                VDeptUser assigneeUser = VDeptUser.dao.findById(assignee);
                CodeService.me.pushMsg(assigneeUser, CodeMsg.MSG_CAR_APPROVE);
            }
            return ResultData.dataResult("success", "审批成功", resultMap);
        } else {
            return ResultData.unDataResult("fail", "审批失败");
        }

    }




    /**
     * 查找车辆明细
     *id
     * @return result
     */
    public Result findCarFromId(Object id) {

        String sql = "select a.*,du1.realName,du2.realName visitName,r.endDate from v_car a left join v_visitor_record r on a.visitId = r.id left join v_dept_user du1 on du1.id = r.userId left join v_dept_user du2 on r.visitorId = du2.id  where r.cstatus = 'applySuccess' and a.id =?";
        SqlPara sqlPara = new SqlPara();
        sqlPara.setSql(sql);
        sqlPara.addPara(id);
        Record first = Db.findFirst(sqlPara);
        if(first!=null) {
            Long recordId = first.getLong("visitId");
            List<Record> list = Db.find(Db.getSql("visitRecord.findRecordFromId"), recordId, recordId);
            String entourage = ""; //随行人员
            for (Record record : list) {
                //pid 为空，为主访问记录,否则是随行人员将人员姓名添加到对应的字段
                if (record.get("pid") != null && !"".equals(record.get("pid").toString())) {
                    entourage += record.get("userName") + ",";
                }
            }
            if (entourage.length() > 1) {
                entourage = entourage.substring(0, entourage.length() - 1);
            }
            first.set("entourage", entourage);
        }
        return first != null ? ResultData.dataResult("success", "获取成功", first.getColumns()) :
                Result.unDataResult("fail", "获取失败");
    }



    /**
     *统计
     */
    public List<Record> statVisitorRecordToday() {
        List<Record> list = Db.find("SELECT " +
                "sum(num) num,org_name,recordType " +
                "FROM " +
                "( " +
                "SELECT " +
                "a.*, c.org_name " +
                "FROM " +
                "( " +
                "SELECT " +
                "count(*) num, " +
                "machineCode,recordType " +
                "FROM " +
                "v_visitor_record  where visitDate=?"+
                "GROUP BY " +
                "machineCode,recordType " +
                ") a " +
                "LEFT JOIN v_machine b ON a.machineCode = b.machine_code " +
                "LEFT JOIN v_org c ON b.org_code = c.org_code " +
                ") T " +
                "GROUP BY " +
                "org_name,recordType",DateUtil.getCurDate());

        return list;

    }



    //查询我的访问申请列表
    public Result findMyVisitList(Long userId,Integer pageNum, Integer pageSize) {
        if (userId == null) {
            return ResultData.unDataResult("fail", "缺少参数");
        }
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;

        StringBuilder sql = new StringBuilder();
        sql.append("select  vu.idNo,REPLACE(vu.idHandleImgUrl,'\\\\','/') idHandleImgUrl,REPLACE(u.idHandleImgUrl,'\\\\','/') staffIdHandleImgUrl, v.userId visitorId,v.id recordId, vu.isAuth isAuth,v.recordType recordType,u.addr address,visitorid as staffId,u.realName as realName,u.phone,u.sex,v.startDate,CONCAT(d.dept_name,'   ',u.addr) deptName,v.endDate ,v.reason,v.cstatus,v.plate visitorPlate,vu.addr visitorCmp,vu.phone visitorPhone,vu.realName visitorName,vu.sex visitorSex ");
        sql.append(" from v_visitor_record v LEFT JOIN v_dept_user u on  u.id = v.visitorId LEFT JOIN v_dept_user vu on v.userId = vu.id left join v_dept d on u.deptId  = d.id  where v.userId = ?  or v.visitorId = ? order by visitDate desc, visitTime desc");

        SqlPara sqlPara = new SqlPara();
        sqlPara.setSql(sql.toString());
        sqlPara.addPara(userId);
        sqlPara.addPara(userId);
        Page<Record> paginate = Db.paginate(pageNum, pageSize, sqlPara);

        Record user_key = Db.findFirst("select * from v_user_key");
        for(Record record : paginate.getList())
        {
            String idNo =record.get("idNo");
            idNo = DESUtil.decode(user_key.getStr("workKey"), idNo);
            record.set("idNo",idNo);
        }

        MyPage<VVisitorRecord> myPage = new MyPage(apiList(paginate.getList()), pageNum, pageSize, paginate.getTotalPage(), paginate.getTotalRow());
        return ResultData.dataResult("success", "查看成功", myPage);
    }



    /**
     * 获取二维码信息
     */
    public String getQRCode(Long recordId)
    {
        StringBuilder strQRCodeCon = new StringBuilder("abc&2&1&1&");
        VVisitorRecord vVisitorRecord = VVisitorRecord.dao.findById(recordId);
        VDeptUser vDeptUser = VDeptUser.dao.findById(vVisitorRecord.getUserId());
        if(vDeptUser!=null) {
            String visitor_name = vDeptUser.getRealName();

            String visitorCont = "[" + visitor_name + "]"
                    + "[" + vVisitorRecord.getId() + "]"
                    + "[" + vVisitorRecord.getStartDate() + "]"
                    + "[" + vVisitorRecord.getEndDate() + "]";
            strQRCodeCon.append(System.currentTimeMillis());
            strQRCodeCon.append("|");
            strQRCodeCon.append(Base64.encode(visitorCont.getBytes()));
        }
        return strQRCodeCon.toString();
    }




    //查询我的访问审批列表

    /**
     *
     * @param userId
     * @param pageNum
     * @param pageSize
     * @param type 0 未办 1 已办
     * @return
     */
    public Result findMyApproveVisitList(Long userId,Integer pageNum,Integer pageSize,Integer type) {
        if (userId == null) {
            return ResultData.unDataResult("fail", "缺少参数");
        }
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        List<String> list = new ArrayList<>();
        if(type ==0) {
            list = VisitorProcess.getTaskList(String.valueOf(userId));
        }else if (type ==1)
        {
            list = VisitorProcess.getDoneTasks(String.valueOf(userId));
        }
        StringBuilder ret = new StringBuilder();
        joinIds(list,ret);
        StringBuilder sql = new StringBuilder();
        sql.append("select vu.idNo, REPLACE(vu.idHandleImgUrl,'\\\\','/') idHandleImgUrl,REPLACE(u.idHandleImgUrl,'\\\\','/') staffIdHandleImgUrl,v.userId visitorId,v.id recordId, vu.isAuth isAuth,v.recordType recordType,u.addr address,visitorid as staffId,u.realName as realName,u.phone,u.sex,v.startDate,CONCAT(d.dept_name,'   ',u.addr) deptName,v.endDate ,v.reason,v.cstatus,v.plate visitorPlate,  vu.addr visitorCmp,vu.phone visitorPhone,vu.realName visitorName,vu.sex visitorSex ");
        sql.append(" from v_visitor_record v LEFT JOIN v_dept_user u on  u.id = v.visitorId LEFT JOIN v_dept_user vu on v.userId = vu.id left join v_dept d on u.deptId  = d.id  where v.processId in  "+ret.toString() +" order by visitDate desc, visitTime desc");

        SqlPara sqlPara = new SqlPara();
        sqlPara.setSql(sql.toString());


        Page<Record> paginate = Db.paginate(pageNum, pageSize, sqlPara);

        Record user_key = Db.findFirst("select * from v_user_key");
        for(Record record : paginate.getList())
        {
            String idNo =record.get("idNo");
            idNo = DESUtil.decode(user_key.getStr("workKey"), idNo);
            record.set("idNo",idNo);
        }


        MyPage<VVisitorRecord> myPage = new MyPage(apiList(paginate.getList()), pageNum, pageSize, paginate.getTotalPage(), paginate.getTotalRow());
        return ResultData.dataResult("success", "查看成功", myPage);
    }



    //查询我的车辆审批列表
    public Result findMyApproveCarList(Long userId,Integer pageNum, Integer pageSize,Integer type) {
        if (userId == null) {
            return ResultData.unDataResult("fail", "缺少参数");
        }
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        List<String> list = new ArrayList<>();

        if(type ==0) {
            list = VisitorProcess.getTaskList(String.valueOf(userId));
        }else if (type ==1)
        {
            list = VisitorProcess.getDoneTasks(String.valueOf(userId));
        }

        StringBuilder ret = new StringBuilder();
        joinIds(list,ret);

        String sql = "select au.realName approveName ,REPLACE(vu.idHandleImgUrl,'\\\\','/')  idHandleImgUrl,vu.idNo,vu.realName visitorName,vu.addr visitorCmp,vu.phone visitorPhone,c.* from v_car c LEFT JOIN v_dept_user vu on visitId = vu.id\n" +
                "LEFT JOIN v_dept_user au on approvalUserId = au.id where processId in  "+ret.toString()+" order by visitDate desc ,visitTime desc";
        SqlPara sqlPara = new SqlPara();
        sqlPara.setSql(sql.toString());
        Page<Record> paginate = Db.paginate(pageNum, pageSize, sqlPara);

        Record user_key = Db.findFirst("select * from v_user_key");

        for(Record record : paginate.getList())
        {
            String entourages =record.get("entourages");
            record.set("entourages",JSONArray.parse(entourages));
            String idNo =record.get("idNo");
            idNo = DESUtil.decode(user_key.getStr("workKey"), idNo);
            record.set("idNo",idNo);
        }

        MyPage<VVisitorRecord> myPage = new MyPage(apiList(paginate.getList()), pageNum, pageSize, paginate.getTotalPage(), paginate.getTotalRow());
        return ResultData.dataResult("success", "查看成功", myPage);
    }




    private void joinIds(List<String> idList, StringBuilder ret) {
        ret.append("('");
        boolean isFirst = true;
        for (String processInstanceId : idList) {
            if (isFirst) {
                isFirst = false;
            } else {
                ret.append("','");
            }
            ret.append(processInstanceId);
        }
        ret.append("')");
    }




    public Result getNOApproveNum(Long userId) {
        if (userId == null) {
            return ResultData.unDataResult("fail", "缺少参数");
        }

        List<String> list =  VisitorProcess.getTaskList(String.valueOf(userId));

        StringBuilder ret = new StringBuilder();
        joinIds(list,ret);
        String sql ="select count(*) num from v_visitor_record where processId in  "+ret.toString();
        Record record = Db.findFirst(sql);
        int visitorNum =record.getInt("num");
        sql ="select count(*) num from v_car where processId in  "+ret.toString();
        record = Db.findFirst(sql);
        int carNum =record.getInt("num");

        Map result = new HashedMap();
        result.put("carNum",carNum);
        result.put("visitorNum",visitorNum);

        return ResultData.dataResult("success", "查看成功", result);
    }




}



