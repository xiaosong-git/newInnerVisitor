package com.xiaosong.common.api.visitorRecord;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Prop;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.xiaosong.MainConfig;
import com.xiaosong.common.api.base.MyBaseService;
import com.xiaosong.common.api.code.CodeService;
import com.xiaosong.common.api.websocket.*;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.constant.Constant;
import com.xiaosong.constant.MyRecordPage;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.model.VOutVisitor;
import com.xiaosong.model.VVisitorRecord;
import com.xiaosong.constant.MyPage;
import com.xiaosong.param.ParamService;
import com.xiaosong.util.*;
import com.xiaosong.util.Base64;
import okhttp3.WebSocket;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.File;
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
        long id = msg.getLong("id");
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
                WebSocketEndPoint webSocketEndPoint = WebSocketMapUtil.get(toUserId);

                if (webSocketEndPoint != null) {
                    RemoteEndpoint.Async toUserRemote = webSocketEndPoint.getSession().getAsyncRemote();
                    JSONObject obj = new JSONObject();
                    obj.put("orgName", "无");
                    obj.put("companyId", "无");
                    obj.put("fromUserId", fromUserId);
                    Integer companyId = BaseUtil.objToInteger(visitorRecord.get("companyId"), 0);
                    String orgCode = BaseUtil.objToStr(visitorRecord.get("orgCode"), null);
                    visitorRecord.remove("companyId");
                    visitorRecord.remove("orgCode");
                    JSONObject recordObject = JSONObject.parseObject(visitorRecord.toJson());
                    obj.putAll(recordObject);
                    for (String str : obj.keySet()) {
                        if (!obj.containsKey(str))
                            obj.put(str, "无");
                        System.out.println(str + ":" + obj.get(str));
                    }
                    if (companyId != 0) {
                        String companyName = Db.queryStr("select dept_name companyName from " + TableList.DEPT + " where id=?", companyId);
                        obj.put("companyName", companyName);
                    }
                    if (orgCode != null) {
                        String orgName = Db.queryStr("select org_name from " + TableList.ORG + " where org_code=?", orgCode);
                        obj.put("orgName", orgName);
                    }
                    obj.put("type", Constant.MASSEGETYPE_REPLY);
                    obj.put("toUserId", msg.get("toUserId"));
                    System.out.println("发送给toUser的消息为+" + obj);
                    toUserRemote.sendText(obj.toJSONString());
                    //不在线推送
                } else {
                    Record toUser = Db.findById(TableList.DEPT_USER, toUserId);
                    String notification_title = "预约访问信息提醒";
                    String deviceToken = BaseUtil.objToStr(toUser.get("deviceToken"), null);
                    String msg_content = "【朋悦比邻】您好，您有一条预约访问已审核，请登入app查收!";
                    String realName = BaseUtil.objToStr(toUser.get("realName"), "");
                    String startDate = BaseUtil.objToStr(visitorRecord.get("startDate"), "");
//						String deviceType = BaseUtil.objToStr(toUser.get("deviceType"), "0");
                    String isOnlineApp = BaseUtil.objToStr(toUser.get("isOnlineApp"), "F");
                    String phone = BaseUtil.objToStr(toUser.get("phone"), "0");
                    String visitorResult = "审核不成功";
                    if ("applySuccess".equals(cstatus)) {
                        visitorResult = "审核成功";
                    }
                    if ("F".equals(isOnlineApp)) {
                        CodeService.me.sendMsg(phone, 3, visitorResult, realName, startDate, null);
                        log.info(realName + "：发送短信推送成功");
                    } else {
                        boolean single = GTNotification.Single(deviceToken, phone, notification_title, msg_content, msg_content);
                        if (!single) {
                            CodeService.me.sendMsg(phone, 3, visitorResult, realName, startDate, null);
                        }
                    }
                    log.info("发送个推推送成功设备号{}", deviceToken);
                }
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
    public Result visitReply(VVisitorRecord visitorRecord) {
        try {
            String replyDate = DateUtil.getCurDate();
            String replyTime = DateUtil.getCurTime();
            Integer id = BaseUtil.objToInteger(visitorRecord.get("id"), null);
            //登入人
            Long userId = BaseUtil.objToLong(visitorRecord.get("userId"), null);
            String cstatus = BaseUtil.objToStr(visitorRecord.get("cstatus"), null);
            String answerContent = BaseUtil.objToStr(visitorRecord.get("answerContent"), "");
            //更新邀约信息
            if (id == null || userId == null || cstatus == null) {
                return Result.unDataResult("fail", "缺少参数");
            }
            visitorRecord.remove("userId").setReplyDate(replyDate).setReplyTime(replyTime).setIsReceive("F");
            boolean update = visitorRecord.update();
            String apply = "同意";
            if ("applyFail".equals(cstatus)) {
                apply = "拒绝";
            }
            if (update) {
                //返回回消息
                Map<String, Object> visitorMap = Db.findById(TableList.VISITOR_RECORD, id).getColumns();
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
                    String notification_title = "邀约回应信息提醒";
                    String deviceToken = BaseUtil.objToStr(toUser.get("deviceToken"), "");
                    String msg_content = "【朋悦比邻】您好，您有一条邀约已回应，请登入app查收!";
                    boolean single = false;

//						String deviceType = BaseUtil.objToStr(toUser.get("deviceType"), "0");

                    String phone = BaseUtil.objToStr(toUser.get("phone"), "0");
                    //个推
                    single = GTNotification.Single(deviceToken, phone, notification_title, msg_content, msg_content);
//						isYmSuc=shortMessageService.YMNotification(deviceToken,deviceType,notification_title,msg_content,isOnlineApp);
                    log.info("发送个推 推送成功? 设备号{}", single);
//                        if (!isYmSuc) {
//				            codeService.sendMsg(phone, 3, visitorResult, visitorBy, visitorDateTime, null);
//			                }

                }
                return Result.unDataResult("success", apply + "邀约成功！");
            } else {
                return Result.unDataResult("fail", apply + "邀约失败！");
            }
        } catch (Exception e) {
            log.error("邀请回应报错！", e);
            return Result.unDataResult("fail", "同意邀约失败！系统错误，请联系客服！");
        }
    }

    /**
     * 非好友访问,具体流程：
     * 1内网存在用户 ->判断是否实名 ->return;
     * 2内网不存在用户 ->调用外网api接口查找用户 ->判断存在->判断是否实名->return
     */
    public Result visit(Long userId, String phone, String realName, String startDate, String endDate, String reason) throws Exception {
        if (userId == null || phone == null || realName == null) {
            return Result.unDataResult(ConsantCode.FAIL, "缺少用户参数!");
        }
        //被访者
        VDeptUser visitorBy = VDeptUser.dao.findFirst("select id,deptId,realName,isAuth,deviceToken,deviceType,isOnlineApp from " + TableList.DEPT_USER + " " +
                "where phone=?", phone);
        //访者
        VDeptUser visitUser = VDeptUser.dao.findById(userId);
        if (visitUser == null) {
            return Result.unDataResult(ConsantCode.FAIL, "用户信息错误!");
        }
        /**
         * 调用外部接口查看被访者
         */
        if (visitorBy == null) {
            return visitOutApi(visitUser, phone, realName, startDate, endDate);
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
            Object existUser = Db.queryFirst("select 1 from " + TableList.DEPT_USER + " where userId=" + visitorId + " and currentStatus" +
                    "='normal' and status='applySuc'", visitorId);
            if (existUser == null) {
                return Result.unDataResult("fail", "被访者无归属部门！");
            }
        }
        String deviceToken = visitorBy.getDeviceToken();
        String isOnlineApp = visitorBy.getIsOnlineApp();
        //被访者姓名
        String visitorByName = BaseUtil.objToStr(visitorBy.get("realName"), null);
        //访问者姓名
        String userName = Db.queryStr("select realName from " + TableList.DEPT_USER + " where id =?", userId);
        //如果是访问recordType=1
        //查询内部是否有邀约信息
        Integer integer = Db.queryInt(Db.getSql("visitRecord.check"), userId, visitorId, Constant.VISITOR, endDate, startDate);
        //如果是邀约recordType=2 访客与被访者在数据库中位置调换
        if (integer != null) {
            //发送回消息
            log.info(startDate + "该时间段" + endDate + "内已经有邀约信息存在");
            return Result.unDataResult("fail", "在" + startDate + "——" + endDate + "内已经有邀约信息存在");
        }
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
        //记录访问记录
        if (visitRecord.save()) {
            String notification_title = "访客-审核通知";
            String msg_content = "【朋悦比邻】您好，您有一条预约访客需审核，访问者:" + userName + "，被访者:" + visitorByName + ",访问时间:"
                    + startDate;

            //发送到 websocket
            WebSocketVisitor.me.sendReceiveVisitMsg(visitUser.getIdNO(),visitUser.getRealName(),startDate,endDate,"applyConfirm");

            if ("F".equals(isOnlineApp)) {
                CodeService.me.sendMsg(phone, 5, null, null, startDate, userName);
                log.info(visitorByName + "：发送短信推送成功");
            } else {
                boolean single = GTNotification.Single(deviceToken, phone, notification_title, msg_content, msg_content);
//				isYmSuc = shortMessageService.YMNotification(deviceToken, deviceType, notification_title, msg_content, isOnlineApp);
                log.info("发送个推成功{}", visitorByName);
                if (!single) {
                    CodeService.me.sendMsg(phone, 5, null, null, startDate, userName);
                    log.info(visitorByName + "：发送短信推送成功");
                }
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
                long o = BaseUtil.objToLong(jsonObject.get("data"), 0l);
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
    public Result inviteStranger(Integer visitorId, String phone, String realName, String startDate, String endDate, String reason, Integer companyId) throws Exception {

        if (visitorId == null || "".equals(phone) || "".equals(realName)) {
            return Result.unDataResult(ConsantCode.FAIL, "缺少用户参数!");
        }
//        SqlPara para = Db.getSqlPara("deptUser.findByPhone", phone);//根据手机查找用户
////        //如果用户不存在
//        VDeptUser user = VDeptUser.dao.findFirst(para);
        String sql = "select id,deptId companyId,realName,idNO,isAuth,deviceToken,deviceType,isOnlineApp from " + TableList.DEPT_USER + " " +
                "where phone='" + phone + "'";
//        //被邀者==访问者
        Record invitor = Db.findFirst(sql);
        if (invitor == null) {
            // todo 如果用户不存在 去云端进行查询用户并邀约
            return Result.unDataResult("fail", "用户不存在");
        } else {
            String invitorName = BaseUtil.objToStr(invitor.get("realName"), "");
            String invitorIdNO = BaseUtil.objToStr(invitor.get("idNO"), "");
//        //被邀者==访问者id
            Integer userId = BaseUtil.objToInteger(invitor.get("id"), 0);
            if (userId.equals(visitorId)) {
                return Result.unDataResult("fail", "请不要对自己发起邀约！");
            }
//        // 查询登入者/邀约者姓名
            sql = "select * from " + TableList.DEPT_USER + " where id=" + visitorId;
            Record visitor = Db.findFirst(sql);
            String visitorName = BaseUtil.objToStr(visitor.get("realName"), "");
//
//        //登入者公司信息 地址等等
            String companySql = "select c.addr,c.dept_name companyName, o.org_code,o.org_name from " + TableList.DEPT + " c left join " + TableList.ORG + " o on " +
                    "o.id=c.org_id " +
                    "where c.id=" + companyId;
            System.out.println(companySql);
            Record company = Db.findFirst(companySql);
            String addr = BaseUtil.objToStr(company.get("addr"), "");
            String orgCode = BaseUtil.objToStr(company.get("org_code"), "");
            String orgName = BaseUtil.objToStr(company.get("org_name"), "");
//        //查看是否重复邀约
            int check = check(userId, visitorId, 2, startDate, endDate, "in", "in");
//        //如果是邀约recordType=2 访客与被访者在数据库中位置调换
            if (check > 0) {
                //发送回消息
                log.info(startDate + "该时间段" + endDate + "内已经有邀约信息存在");
                return Result.unDataResult("fail", "在" + startDate + "——" + endDate + "内已经有邀约信息存在");
            }
//        //被邀约者/访客Id
            Record visitRecord = new Record();
            visitRecord.set("userId", userId);
            visitRecord.set("visitorId", visitorId);
            //直接传入已审核同意进出
            visitRecord.set("cstatus", "applySuccess");
            visitRecord.set("visitDate", DateUtil.getCurDate());
            visitRecord.set("visitTime", DateUtil.getCurTime());
            visitRecord.set("reason", reason);
            visitRecord.set("startDate", startDate);
            visitRecord.set("endDate", endDate);
            visitRecord.set("vitype", "A");
            visitRecord.set("orgCode", orgCode);
            visitRecord.set("companyId", companyId);
            visitRecord.set("recordType", 2);
            visitRecord.set("remarkName", realName);
            //提示为非好友邀约
            visitRecord.set("answerContent", "非好友邀约");

            //记录访问记录
            boolean save = Db.save(TableList.VISITOR_RECORD, visitRecord);
            if (save) {
                //发送到 websocket
                WebSocketVisitor.me.sendReceiveVisitMsg(invitorIdNO,invitorName,startDate,endDate,"applySuccess");

                String encode = Base64.encode(BaseUtil.objToStr(visitRecord.get("id"),"").getBytes("UTF-8"));
                String url = p.get("URL") + encode;
                YunPainSmsUtil.sendSmsCode(url, phone, 6, addr, orgName, endDate, realName, startDate, visitorName);
                //websocket通知前端获取访客数量
                WebSocketMonitor.me.getVisitorData();
                WebSocketSyncData.me.sendVisitorData();
                return Result.unDataResult("success", "邀约成功");
            } else {
                return Result.unDataResult("fail", "邀约失败");
            }
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
    public void visitPush(Map<String, Object> visitorRecord, Map<String, Object> userUser, Map<String, Object> visitorUser, Map<String, Object> saveMap, JSONObject msg, String visitorResult, Map<String, String> wxMap) throws Exception {
        log.info("visitPush");
        String toUserId = BaseUtil.objToStr(visitorRecord.get("userId"), "0");
        String viType = BaseUtil.objToStr(visitorRecord.get("vitype"), "");
        String startDate = BaseUtil.objToStr(visitorRecord.get("startDate"), "");
        String deviceToken = BaseUtil.objToStr(userUser.get("deviceToken"), "");
//		String isOnlineApp = BaseUtil.objToStr(userUser.get("isOnlineApp"), "");
        String phone = BaseUtil.objToStr(userUser.get("phone"), "");
        String visitorBy = BaseUtil.objToStr(visitorUser.get("realName"), "");
        //发送访问者websocket聊天框
        WebSocketEndPoint webSocketEndPoint = WebSocketMapUtil.get(toUserId);
        if ("A".equals(viType)) {
            //用户在线，调用发送接口
            if (webSocketEndPoint != null) {
                for (Map.Entry<String, Object> entry : visitorRecord.entrySet()) {
                    if (entry.getValue() == null) {
                        visitorRecord.put(entry.getKey(), "无");
                    }
                    msg.put(entry.getKey(), entry.getValue());
                }
                webSocketEndPoint.getSession().getAsyncRemote().sendText(msg.toJSONString());
                saveMap.put("isReceive", "T");
                Db.update(TableList.VISITOR_RECORD, saveMap);
            } else {
                boolean single = false;
                //不在线发送推送给用户
                String notification_title = "访客-访问提醒";
                String msg_content = "【朋悦比邻】您好，您有一条预约访客申请已回复，请进入app查看!";
                String isOnlineApp = BaseUtil.objToStr(userUser.get("isOnlineApp"), "F");

//个推
                single = GTNotification.Single(deviceToken, phone, notification_title, msg_content, msg_content);
//						shortMessageService.YMNotification(deviceToken,deviceType,notification_title,msg_content,isOnlineApp);
                //个推不在线，短信推送
                if (!single || "F".equals(isOnlineApp)) {
                    CodeService.me.sendMsg(phone, 3, visitorResult, visitorBy, startDate, null);
                }
            }
            //推送微信
        } else if ("C".equals(viType)) {

            if (wxMap.get("wxOpenId") == null || "".equals(wxMap.get("wxOpenId"))) {
                //短信推送
                CodeService.me.sendMsg(phone, 3, visitorResult, visitorBy, startDate, null);
            } else {
                //审核结果发送给访问者微信公众号
                log.info("wxUrl=" + MainConfig.p.get("wxUrl"));
                String s = HttpClientUtil.sendPost(MainConfig.p.get("wxUrl"), wxMap, "application/x-www-form-urlencoded");
                //如果微信推送失败，则切换短信推送
            }
            //其他情况，短信推送
        } else {
            CodeService.me.sendMsg(phone, 3, visitorResult, visitorBy, startDate, null);
        }
    }

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
        Record first = Db.findFirst(Db.getSql("visitRecord.findRecordFromId"), recordId);

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
                "IF( dept_name IS NULL, '', dept_name ) companyName\n" +
                " FROM (\n" +
                "select id,IF(userId=" + userId + ",visitorId,userId) visitorId,startDate,endDate,companyId,orgCode\n" +
                "from " + TableList.VISITOR_RECORD + "\n" +
                "where userId=" + userId + " or visitorId=" + userId + ")vr\n" +
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
                "IF( dept_name IS NULL, '', dept_name ) companyName,\n" +
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
                "        where vu.realName is not null and u.realName is not null and " + and + "((userId=" + userId + " and visitorId=" + visitorId + ") or(userId=" + visitorId + " and visitorId=" + userId + "))\n" +
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


    public List<Record> findValidList(Long userId , String time){
        StringBuilder sql = new StringBuilder();
        List<Object> objects = new LinkedList<>();
        sql.append("select u.realName as real_name,u.phone,u.sex,v.startDate as start_date,v.endDate as end_date,v.reason,v.cstatus");

        sql.append(" from v_visitor_record v LEFT JOIN v_dept_user u on  u.id = v.visitorId where ? <= v.endDate and v.userId = ?");

        objects.add(time);
        objects.add(userId);
        return Db.find(sql.toString(),objects.toArray());
    }

}
