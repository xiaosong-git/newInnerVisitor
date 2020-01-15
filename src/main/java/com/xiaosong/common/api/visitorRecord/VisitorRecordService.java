package com.xiaosong.common.api.visitorRecord;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Prop;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.xiaosong.MainConfig;
import com.xiaosong.common.api.code.CodeService;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.common.api.websocket.WebSocketEndPoint;
import com.xiaosong.common.api.websocket.WebSocketMapUtil;
import com.xiaosong.constant.Constant;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VAppUser;
import com.xiaosong.model.VOutVisitor;
import com.xiaosong.model.VVisitorRecord;
import com.xiaosong.constant.MyPage;
import com.xiaosong.util.*;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @program: innerVisitor
 * @description: 访客记录
 * @author: cwf
 * @create: 2020-01-03 14:45
 **/
public class VisitorRecordService {
    Log log = Log.getLog(VisitorRecordService.class);
    public static final VisitorRecordService me = new VisitorRecordService();
    Prop p = MainConfig.p;

    /**
     * 根据 where 条件进行查询我的邀约，邀约我的，我的访问，访问我的判断
     */
    //id=visitorId 我的邀约 id=userId 邀约我的
    public Result invite(Long userId, Integer pageNum, Integer pageSize, Integer recordType, String condition) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        //查看的是对方的信息
        String otherMan = "userId".equals(condition) ? "visitorId" : "userId";
        String coloumSql = "SELECT vr.id,IF(u.realName IS NULL or u.realName=\"\",remarkName,u.realName) realName,u.phone,u.headImgUrl,\n" +
                "\tvr.visitDate,vr.visitTime,vr.userId,vr.visitorId,vr.reason,vr.cstatus,vr.dateType\n" +
                ",vr.startDate,vr.endDate,vr.answerContent,vr.orgCode,vr.companyId,vr.recordType,\n" +
                "vr.replyDate,vr.replyTime,vr.vitype,vr.replyUserId,vr.isReceive,o.org_name,c.companyName,o.accessType";
        String from = " from " + TableList.VISITOR_RECORD + " vr\n" +
                "left join " + TableList.APP_USER + " u on u.id=vr." + otherMan + "\n" +
                "left join " + TableList.COMPANY + " c on vr.companyId=c.id\n" +
                "left join  " + TableList.ORG + " o on vr.orgCode=o.org_code " +
                "where " + condition + "=" + userId + " and recordType=" + recordType;
        String oderBy = " ORDER BY startDate>NOW() desc,  IF(startDate > NOW(), FIELD(cstatus,'Cancle','applyFail',  'applySuccess','applyConfirm'), startDate ) desc,startDate desc,endDate";
        String totalRowSql = "select count(*) " + from;
//        log.info(coloumSql+fromSql+union );
        //jfinal中的分页对象
        Page<VVisitorRecord> records = VVisitorRecord.dao.paginateByFullSql(pageNum, pageSize, totalRowSql, coloumSql + from + oderBy);
        //查看未过期记录的条数
        String count = Db.queryStr("select count(*) num from " + TableList.VISITOR_RECORD + "  where visitorId = " + userId + " and cstatus='applyConfirm' and endDate>SYSDATE() and recordType=" + recordType + "  ");
        //转换为api接口对象
        MyPage<VVisitorRecord> myPage = new MyPage(records.getList(), pageNum, pageSize, records.getTotalPage(), records.getTotalRow());
        return ResultData.dataResultCount("success", "获取成功", myPage, count);
    }

    /**
     * 回应邀约/访问
     * 回应时将isReceive 字段改为'F' 表示对方未接收
     */
    public void visitReply(Session session, JSONObject msg) throws Exception {
        //根据Id获取需要更新的类容
        String replyDate = DateUtil.getCurDate();
        String replyTime = DateUtil.getCurTime();
        Integer id = msg.getInteger("id");
        //登入人
        String fromUserId = WebSocketEndPoint.me.getUserId(session.getQueryString());
        String cstatus = BaseUtil.objToStr(msg.get("cstatus"), null);
        String answerContent = BaseUtil.objToStr(msg.get("answerContent"), null);
        String sql = "update " + TableList.VISITOR_RECORD +
                " set cstatus='" + cstatus + "', replyDate='" + replyDate + "', replyTime='" + replyTime + "' " +
                ",answerContent='" + answerContent + "',replyUserId=" + fromUserId + ",isReceive='F' " +
                " where id = " + id;
        RemoteEndpoint.Async userRemote = session.getAsyncRemote();
        try {
            int update = Db.update(sql);
            if (update > 0) {
                userRemote.sendText(Result.ResultCodeType("success", "发送成功", "200", 3));
                //返回回消息
                String toUserId = msg.getString("toUserId");
                WebSocketEndPoint webSocketEndPoint = WebSocketMapUtil.get(toUserId);
                Record visitRecord = Db.findById(TableList.VISITOR_RECORD, id);

                if (webSocketEndPoint != null) {
                    RemoteEndpoint.Async toUserRemote = webSocketEndPoint.getSession().getAsyncRemote();
                    JSONObject obj = new JSONObject();
                    obj.put("orgName", "无");
                    obj.put("companyId", "无");
                    obj.put("fromUserId", fromUserId);
                    Integer companyId = BaseUtil.objToInteger(visitRecord.get("companyId"), 0);
                    String orgCode = BaseUtil.objToStr(visitRecord.get("orgCode"), null);
                    visitRecord.remove("companyId");
                    visitRecord.remove("orgCode");
                    JSONObject recordObject = JSONObject.parseObject(visitRecord.toJson());
                    obj.putAll(recordObject);
                    for (String str : obj.keySet()) {
                        if (!obj.containsKey(str))
                            obj.put(str, "无");
                        System.out.println(str + ":" + obj.get(str));

                    }
                    if (companyId != 0) {
                        String companyName = Db.queryStr("select companyName from " + TableList.COMPANY + " where id=?", companyId);
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
                    Record toUser = Db.findById(TableList.APP_USER, toUserId);
                    String notification_title = "预约访问信息提醒";
                    String deviceToken = BaseUtil.objToStr(toUser.get("deviceToken"), null);
                    String msg_content = "【朋悦比邻】您好，您有一条预约访问已审核，请登入app查收!";
                    String realName = BaseUtil.objToStr(toUser.get("realName"), "");
                    String startDate = BaseUtil.objToStr(visitRecord.get("startDate"), "");
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
     * 非好友访问,具体流程：
     * 1内网存在用户 ->判断是否实名 ->return;
     * 2内网不存在用户 ->调用外网api接口查找用户 ->判断存在->判断是否实名->return
     */
    public Result visit(Long userId, String phone, String realName, String startDate, String endDate, String reason) throws Exception {
        if (userId == null || phone == null || realName == null) {
            return Result.unDataResult(ConsantCode.FAIL, "缺少用户参数!");
        }
        //被访者
        VAppUser visitorBy = VAppUser.dao.findFirst("select id,companyId,realName,isAuth,deviceToken,deviceType,isOnlineApp from " + TableList.APP_USER + " " +
                "where phone=?", phone);
        //访者
        VAppUser visitUser = VAppUser.dao.findById(userId);
        if (visitUser == null) {
            return Result.unDataResult(ConsantCode.FAIL, "用户信息错误!");
        }
        if (visitorBy == null) {
            VVisitorRecord visitorRecord = new VVisitorRecord();
            visitorRecord.setUserId(userId).setStartDate(startDate)
                    .setEndDate(endDate).setRecordType(1).setVisitDate(DateUtil.getCurDate())
                    .setVisitTime(DateUtil.getCurTime()).setCstatus("applyConfirm")
                    .setVitype("F");
            boolean save = visitorRecord.save();
//            if (save) {
//                System.out.println(visitorRecord.getId());
//                throw new RuntimeException("1");
//            }
            //todo 增加调用公众api查看用户是否存在
            //地址换为生产接口，如果返回有值则用新值
            String url = p.get("apiUrl") + "/visitor/visitorRecord/innerVisitRequest/";
            //jfinal中的hashMap封装类 暂时用手机号代替userCode
            Kv data = Kv.by("realName", realName).set("phone", phone).set("userCode", phone)
                    .set("userRealName", visitUser.getRealName()).set("userPhone", visitUser.getPhone())
                    .set("startDate", startDate).set("endDate", endDate).set("routerId", p.get("routerId"))
                    .set("originId",visitorRecord.getId().toString());
            String ret = HttpKit.post(url, data, null);//转化为map对象或JsonObject
            JSONObject jsonObject = JSON.parseObject(ret);
            Map<String, Object> map = (Map<String, Object>) jsonObject.get("data");
            Result result = new Result();
            Map<String, Object> verify = (Map<String, Object>) jsonObject.get("verify");
            //如果返回值为false
            if (map == null) {
                result.setVerify(verify);
                //找不到用户
                return result;
            } else {
                //将用户插入out_user数据库，并生成record表
                VOutVisitor vOutVisitor = new VOutVisitor();
                vOutVisitor.setIsAuth("T").setRealName(realName).setPhone(phone);
                //外部用户是否存在
                AtomicReference<Long> id = new AtomicReference<>(Db.queryLong("select id from " + TableList.OUT_VISIT + " where realName=? and phone=?", realName, phone));
                //事务
                boolean tx = Db.tx(() -> {
                    if (id.get() == null) {
                        vOutVisitor.save();
                        id.set(vOutVisitor.getId());
                    }
                    visitorRecord.setOriginType("F").setUserId(userId)
                            .setVisitorId(id.get())
                            .update();
                    return true;
                });
                if (tx) {
                    return Result.unDataResult("success", "访问外部用户成功");
                } else {
                    return Result.unDataResult("fail", "访问外部用户失败");
                }
            }
        } else if (!realName.equals(visitorBy.getRealName())) {
            return Result.unDataResult("fail", "用户姓名与手机不匹配!");
        }
        //查看访客是否实名
        //被访者id
        Long visitorId = visitorBy.getId();
        String isAuth = BaseUtil.objToStr(visitorBy.get("isAuth"), "");
        if (userId.equals(visitorId)) {
            return Result.unDataResult("fail", "请不要对自己发起访问！");
        }
        Integer companyId = BaseUtil.objToInteger(visitorBy.getCompanyId(), null);
        if (!"T".equals(isAuth)) {
            log.info("被访者未实名！:{}", visitorId);
            return Result.unDataResult("fail", "被访者未实名！");
        }
        if (companyId == null) {
            Object existUser = Db.queryFirst("select 1 from " + TableList.DEPT_USER + " where userId=" + visitorId + " and currentStatus" +
                    "='normal' and status='applySuc'", visitorId);
            if (existUser == null) {
                return Result.unDataResult("fail", "被访者无归属公司！");
            }
        }
        String deviceToken = visitorBy.getDeviceToken();
        String isOnlineApp = visitorBy.getIsOnlineApp();
        //被访者姓名
        String visitorByName = BaseUtil.objToStr(visitorBy.get("realName"), null);
        //访问者姓名
        String userName = Db.queryStr("select realName from " + TableList.APP_USER + " where id =?", userId);
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
            return Result.unDataResult("success", "申请成功");
        } else {
            return Result.unDataResult("fail", "申请失败");
        }
    }

    /**
     * @param visitorRecord 访客记录
     * @param vOutVisitor   外部用户
     *                      接收外部访问信息，生成record表数据以及out_visitor数据
     * @return
     */
    public Result receiveOutVisit(VVisitorRecord visitorRecord, VOutVisitor vOutVisitor) {
        AtomicReference<Long> id = new AtomicReference<>(Db.queryLong("select id from " + TableList.OUT_VISIT + " where phone=? and realName=?", vOutVisitor.getPhone(), vOutVisitor.getRealName()));
        int check = check(visitorRecord.getUserId(), visitorRecord.getVisitorId(), visitorRecord.getRecordType(), visitorRecord.getStartDate()
                , visitorRecord.getEndDate(), "F");
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
        return tx ? Result.unDataResult("success", "操作成功") :
                Result.unDataResult("fail", "操作失败");
    }

    /**
     * 非好友邀约 暂时没有思路 如何邀约非好友，不同数据库如何交互？，非好友数据储存在生产库还是本地库？如何统一不同数据库？
     */
    public Result InviteStranger(Map<String, Object> paramMap) throws Exception {
        //邀请者id
//        Integer visitorId = BaseUtil.objToInteger(paramMap.get("userId"), null);
//        String phone = BaseUtil.objToStr(paramMap.get("phone"), "");
//        String realName = BaseUtil.objToStr(paramMap.get("realName"), "");
//        String startDate = BaseUtil.objToStr(paramMap.get("startDate"), "");
//        String endDate = BaseUtil.objToStr(paramMap.get("endDate"), "");
//        String reason = BaseUtil.objToStr(paramMap.get("reason"), "");
//        //我的公司id
//        Integer companyId = BaseUtil.objToInteger(paramMap.get("companyId"),0);
//        if (visitorId == null||"".equals(phone)||"".equals(realName)) {
//            return Result.unDataResult(ConsantCode.FAIL, "缺少用户参数!");
//        }
//        //如果用户不存在
//        Map<String, Object> userByPhone = userService.getUserByPhone(phone);
////        if (userByPhone ==null){
////            //生成新用户
////            userService.createUser(phone, realName);
////        }
//        String sql ="select id,companyId,realName,isAuth,deviceToken,deviceType,isOnlineApp from "+TableList.USER+" " +
//                "where phone='"+phone+"'";
//        //被邀者==访问者
//        Map<String, Object> invitor=findFirstBySql(sql);
////		String invitorName = BaseUtil.objToStr(invitor.get("realName"),"");
//        //被邀者==访问者id
//        Integer userId = BaseUtil.objToInteger(invitor.get("id"), 0);
//        if (userId.equals(visitorId)){
//            return Result.unDataResult("fail","请不要对自己发起邀约！");
//        }
//        // 查询登入者/邀约者姓名
//        sql="select * from "+ TableList.USER +" where id="+visitorId;
//        Map<String, Object> visitor=findFirstBySql(sql);
//        String visitorName = BaseUtil.objToStr(visitor.get("realName"), "");
//        //查询登入者org
//        sql = "select o.org_name,o.org_code,c.companyName from " + TableList.ORG + " o left join " + TableList.COMPANY + " c" +
//                " on o.id=c.orgId where c.id='" + companyId + "'";
//        Map<String, Object> orgMap = findFirstBySql(sql);
//        String orgCode=BaseUtil.objToStr(orgMap.get("org_code"),"无");
//        String	orgName=BaseUtil.objToStr(orgMap.get("org_name"),"无");
//        String	companyName=BaseUtil.objToStr(orgMap.get("companyName"),"无");
//        //登入者公司信息 地址等等
//        String companySql ="select c.addr,c.name,o.org_code from "+TableList.COMPANY+" c" +
//                " left join "+TableList.ORG+" o on c.orgId=o.id " +
//                "where c.id="+companyId;
//        Map<String, Object> company= findFirstBySql(companySql);
//        String addr = BaseUtil.objToStr(company.get("addr"),"");
//        //查看是否重复邀约
//        Map<String, Object> check=check(userId,visitorId,2,startDate,endDate);
//        //如果是邀约recordType=2 访客与被访者在数据库中位置调换
//        if (check != null) {
//            //发送回消息
//            logger.info(startDate+"该时间段"+endDate+"内已经有邀约信息存在");
//            return Result.unDataResult("fail","在"+startDate+"——"+endDate+"内已经有邀约信息存在");
//        }
//        //被邀约者/访客Id
//        Map<String, Object> visitRecord =new HashMap<>();
//        Date date = new Date();
//        visitRecord.put("userId",userId);
//        visitRecord.put("visitorId",visitorId);
//        //直接传入已审核同意进出
//        visitRecord.put("cstatus","applySuccess");
//        visitRecord.put("visitDate",new SimpleDateFormat("yyyy-MM-dd").format(date));
//        visitRecord.put("visitTime",new SimpleDateFormat("yyyy-MM-dd").format(date));
//        visitRecord.put("reason",reason);
//        visitRecord.put("startDate",startDate);
//        visitRecord.put("endDate",endDate);
//        visitRecord.put("vitype","A");
//        visitRecord.put("orgCode",orgCode);
//        visitRecord.put("companyId",companyId);
//        visitRecord.put("recordType",2);
//        visitRecord.put("remarkName",realName);
//        //提示为非好友邀约
//        visitRecord.put("answerContent","非好友邀约");
//
//        //记录访问记录
//        int saveVisitRecord =save(TableList.VISITOR_RECORD,visitRecord);
//        if (saveVisitRecord>0) {
//            String encode = Base64.encode(String.valueOf(saveVisitRecord).getBytes("UTF-8"));
//            String url= Constant.URL+encode;
//            YunPainSmsUtil.sendSmsCode(url, phone, 6, addr, companyName, endDate, realName, startDate,visitorName );
//            return Result.unDataResult("success","邀约成功");
//        }else {
//            return Result.unDataResult("fail","邀约失败");
//        }
        return null;
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
        String originType = visitorRecord.getOriginType();
        //处理外网访客
        if ("F".equals(originType)) {
            //todo 修改内网record表 ，传到云record表。
            if ("applyFail".equals(cstatus)) {

                visitorRecord.setCstatus(cstatus).setReplyUserId(userId)
                        .setAnswerContent(answerContent)
                        .setCompanyId(companyId);
                String url = p.get("apiUrl") + "/visitor/visitorRecord/innerVisitRequest/";
                //jfinal中的hashMap封装类 暂时用手机号代替userCode
                Record record = visitorRecord.toRecord();
                Map<String, Object> columns = record.getColumns();
                String ret = HttpKit.post(url, String.valueOf(columns), null);//转化为map对象或JsonObject
                JSONObject jsonObject = JSON.parseObject(ret);
                Map<String, Object> map = (Map<String, Object>) jsonObject.get("data");
                Result result = new Result();
                Map<String, Object> verify = (Map<String, Object>) jsonObject.get("verify");

            }
            //todo 获取云端图片

            //todo 保存图片到本地

            //todo 调用外部接口 传到外网
        }
        if (!visitorId.equals(userId)) {
            return Result.unDataResult("fail", "被访者错误!");
        }
        Record visitorUser = Db.findById(TableList.APP_USER, userId);
        String visitorBy = visitorUser.get("realName").toString();
        //访客记录
        if (!"applyConfirm".equals(visitorRecord.get("cstatus"))) {
            return Result.unDataResult("fail", "非申请中状态!");
        }
        int update = 0;
        //访客Id 访问者
        Integer recordUserId = BaseUtil.objToInteger(visitorRecord.get("userId"), 0);
        //访客信息
        Record userUser = Db.findById(TableList.APP_USER, recordUserId);
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
                orgComMap = Db.findFirst("select org_code,org_name,accessType,companyName,c.addr,roleType from  " + TableList.ORG + " o " +
                        "left join " + TableList.COMPANY + " c on c.orgId=o.id left join " + TableList.DEPT_USER + " cu on cu.companyId=c.id " +
                        " where c.id=? and userId=? and cu.currentStatus ='normal'  and cu.status ='applySuc' ", companyId, userId);
            }
            if (orgComMap.getColumns().isEmpty()) {
                return Result.unDataResult("fail", "用户不存在该公司");
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
//            wxMap.put("companyFloor", companyFloor);
//            wxMap.put("startDate", visitorRecord.getStartDate());
//            wxMap.put("endDate", visitorRecord.getEndDate());
//            wxMap.put("accessType", accessType);
            String sid = Base64.encode((id).getBytes("UTF-8"));
//            wxMap.put("qrcodeUrl", MainConfig.p.get("URL") + sid);
//            wxMap.put("visitResult", visitorResult);
            //推送消息

        }
        msg.put("companyName", companyName);
        msg.put("orgName", orgName);
        msg.put("addr", addr);
//        wxMap.put("orgName", orgName);
//        wxMap.put("visitResult", visitorResult);
//        wxMap.put("companyName", companyName);
        if (visitorRecord.update()) {
            return Result.unDataResult("success", visitorResult + "成功");
        } else {
            return Result.unDataResult("fail", visitorResult + "失败");
        }
    }

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
        VAppUser user = VAppUser.dao.findById(userId);
        if (user.getCompanyId() == null) {
            return Result.unDataResult("fail", "暂无公司数据!");
        }
        if (!"manage".equals(user.get("role"))) {
            return Result.unDataResult("fail", "非管理者无权查看!");
        }
        String columnSqlCompany = " select u.* ";
        String fromSqlCompany = "  from " + TableList.APP_USER + " u " + " left join " + TableList.DEPT_USER
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
        String from = " from " + TableList.VISITOR_RECORD + " vr " + " left join " + TableList.APP_USER
                + " u on vr.userId=u.id" + " left join " + TableList.APP_USER + " v on vr.visitorId=v.id" + " left join "
                + TableList.COMPANY + " c on vr.companyId=c.id" + " left join " + TableList.ORG + " o on c.orgId=o.id"
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
                      Object endDate, Object originType) {

        String sql = " select id from " + TableList.VISITOR_RECORD + " where userId = '" + userId + "' and visitorId ='"
                + toUserId + "' and recordType = " + recordType + " and cstatus<>'applyFail' and originType='" + originType + "'" +
                " and STR_TO_DATE(startDate,'%Y-%m-%d %H:%i')<STR_TO_DATE('" + endDate + "','%Y-%m-%d %H:%i')" +
                " and   STR_TO_DATE(endDate,'%Y-%m-%d %H:%i')>STR_TO_DATE('" + startDate + "','%Y-%m-%d %H:%i') ";
        log.info("检查是否时间段有邀约信息：{}", sql);
        return Db.queryInt(sql);
    }
}
