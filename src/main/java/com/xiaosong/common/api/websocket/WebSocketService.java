package com.xiaosong.common.api.websocket;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.api.base.MyBaseService;
import com.xiaosong.common.api.user.UserService;
import com.xiaosong.compose.Result;
import com.xiaosong.constant.Constant;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VAppUserMessage;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.model.VVisitorRecord;
import com.xiaosong.util.BaseUtil;
import com.xiaosong.util.DateUtil;
import com.xiaosong.util.GTNotification;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @program: innervisitor
 * @description:
 * @author: cwf
 * @create: 2020-01-02 16:27
 **/
public class WebSocketService extends MyBaseService {
    public static final WebSocketService me = new WebSocketService();
    Log log = Log.getLog(WebSocketService.class);

    /**
     * 判断用户是否为好友，非好友则返回信息
     *
     * @return 是否为好友
     * @throws IOException
     * @author cwf
     * @date 2020/1/2 14:45
     */
    public boolean isFriend(Session session, JSONObject msg) throws IOException {

        int type = msg.getInteger("type");
        String userId = WebSocketEndPoint.me.getUserId(session.getQueryString());
        Long friendId = msg.getLong("toUserId");
        Record friend = Db.findFirst(Db.getSql("deptUser.findFriend"), friendId, userId);
        JSONObject obj = new JSONObject();
        obj.put("fromUserId", friendId);
        obj.put("toUserId", userId);
        obj.put("message", "对方开启了好友验证，你还不是他好友，请先发送好友验证请求！");
        obj.put("type", type);
        saveJson(friendId, obj);
        if (friend == null || friend.get("applyType") != (Object) 1) {
            session.getAsyncRemote().sendText(obj.toString());
            //非好友
            return false;
        }
        Record user = Db.findFirst(Db.getSql("deptUser.findFriend"), userId, friendId);
        if (user == null) {
            obj.put("message", "您还不是对方好友，请添加好友！");
            session.getAsyncRemote().sendText(obj.toString());
            //非好友
            return false;
        } else {
            int applyType = BaseUtil.objToInteger(user.get("applyType"), 2);
            if (applyType != 1) {
                obj.put("message", "您还不是对方好友，请添加好友！");
                if (applyType == 2) {
                    obj.put("message", "您已删除对方，请重新添加好友！");
                }
                session.getAsyncRemote().sendText(obj.toString());
                //非好友
                return false;
            }
        }
        //是好友
        return true;
    }

    /**
     * 获取用户信息并存入jsonObj
     */
    public void saveJson(Long fromUserId, JSONObject obj) {
        VDeptUser deptUser = VDeptUser.dao.findById(fromUserId);
        if (deptUser != null) {
            obj.put("realName", deptUser.getRealName());
//        obj.put("nickName",deptUser.getNiceName());
            obj.put("headImgUrl", deptUser.getHeadImgUrl());
            obj.put("idHandleImgUrl", deptUser.getIdHandleImgUrl());
//        obj.put("orgId",appUser.getOrgId());
        }
    }

    /**
     * 好友聊天 好友申请
     *
     * @param session
     * @param msg
     */
    public void dealChat(Session session, JSONObject msg, String fromUserId, String toUserId, int type) { //获取主要消息
        String content = msg.getString("message");
        if (content == null) {
            return;
        }
        //传输对象
        //判断
        JSONObject obj = new JSONObject();
        VDeptUser fromUser = VDeptUser.dao.findById(fromUserId);
        if (fromUser != null) {
            obj.put("realName", fromUser.getRealName());
            obj.put("headImgUrl", fromUser.getHeadImgUrl());
            obj.put("idHandleImgUrl", fromUser.getIdHandleImgUrl());
        }
        try {//发送给toUserId
            WebSocketEndPoint webSocketEndPoint = WebSocketMapUtil.get(toUserId);
            //用户在线，调用发送接口
            RemoteEndpoint.Async fromUserRemote = session.getAsyncRemote();
            if (webSocketEndPoint != null) {
                obj.put("message", msg.getString("message"));
                obj.put("toUserId", toUserId);
                obj.put("fromUserId", fromUserId);
                obj.put("type", type);
                obj.put("updateTime", DateUtil.getSystemTime());
                //查看用户信息
                //查看好友申请数量
                if (type == 4) {
                    Integer count = Db.queryInt("select count(*) c from " + TableList.USER_FRIEND + " where friendId=" + toUserId + " and applyType=0");
                    if (count != 0) {
                        obj.put("count", count);
                    }
                }
                webSocketEndPoint.getSession().getAsyncRemote().sendText(obj.toString());
                fromUserRemote.sendText(Result.ResultCodeType("success", "发送成功", "200", type));
                //用户不在线，插入数据库
            } else {
                VAppUserMessage VDeptUserMessage = new VAppUserMessage();
                boolean save = VDeptUserMessage.setFromUserId(Long.valueOf(fromUserId)).setToUserId(Long.valueOf(toUserId))
                        .setMessage(content).setUpdateTime(DateUtil.getSystemTime()).setType(type).save();
                if (save) {
                    fromUserRemote.sendText(Result.ResultCodeType("success", "发送成功", "200", type));
                    //发送推送
                    VDeptUser deptUser = VDeptUser.dao.findById(toUserId);
                    String notification_title = type == 4 ? "您有一条好友申请需处理！" : obj.get("realName") + "给您发来消息！";
                    //个推
                    GTNotification.Single(deptUser.getDeviceToken(), deptUser.getPhone(), notification_title, content, content);
                } else {
                    fromUserRemote.sendText(Result.ResultCodeType("fail", "发送失败", "-1", type));
                }
            }
        } catch (Exception e) {
            log.error("发送聊天错误", e);
        }
    }

    /**
     * @param session    websocket session信息
     * @param msg        websocket内容
     * @param fromUserId 来源
     * @param toUserId   去向
     * @param recordType 访问类型 1--访问 2 邀约
     * @throws Exception
     */
    public void receiveVisit(Session session, JSONObject msg, String fromUserId, String toUserId, Integer recordType) throws Exception {
        //查询登入人信息
        VDeptUser fromUser = VDeptUser.dao.findById(fromUserId);
        //被访人或被约人
        VDeptUser toUser = VDeptUser.dao.findById(toUserId);
        //部门
        RemoteEndpoint.Async toUserRemote = session.getAsyncRemote();
        boolean verify = UserService.me.isVerify(toUserId);
        if (!verify) {
            log.info("对方未实名,无法进行访问邀约");
            toUserRemote.sendText(Result.ResultCodeType("fail", "对方未实名,无法进行访问邀约", "-1", BaseUtil.objToInteger(msg.get("type"), 2)));
            return;
        }
        Integer companyId = BaseUtil.objToInteger(msg.get("companyId"), 0);
        String cstatus = BaseUtil.objToStr(msg.get("cstatus"), "applyConfirm");
        String startDate = BaseUtil.objToStr(msg.get("startDate"), DateUtil.getCurDate());
        String endDate = BaseUtil.objToStr(msg.get("endDate"), DateUtil.addMinute(DateUtil.getSystemTime(), 720));
        String reason = BaseUtil.objToStr(msg.get("reason"), "无");
        String notification_title = "访问信息提醒";
        //查询被访人或被约人是否实名
        log.info("被访者或被邀约者{}实名:{}", toUserId, verify);

        VVisitorRecord VisitorRecord = new VVisitorRecord();
        String msg_content = "";
        //访问逻辑
        if (recordType == 1) {
            Integer id = Db.queryInt("select id from " + TableList.DEPT_USER + " where id=? and currentStatus='normal' and status='applySuc'", toUserId);
            if (id == null) {
                toUserRemote.sendText(Result.ResultCodeType("fail", "对方无部门归属,无法进行访问", "-1", BaseUtil.objToInteger(msg.get("type"), 2)));
                return;
            }
            //查询内部是否有邀约信息
            boolean check = this.check(fromUserId, toUserId, recordType, endDate, startDate, toUserRemote);
            if (check) {
                return;
            }
            VisitorRecord.set("userId", fromUserId).set("visitorId", toUserId);
            msg_content=fromUser.getRealName()+"向您发起访问，请登入app查收";
            //邀约逻辑
        } else if (recordType == 2) {
            boolean check = this.check(toUserId, fromUserId, recordType, endDate, startDate, toUserRemote);
            if (check) {
                return;
            }
            VisitorRecord.set("userId", toUserId).set("visitorId", fromUserId);
            //邀约地址
            Record records = Db.findFirst("select o.org_name,o.org_code,d.dept_name companyName from " + TableList.ORG + " o left join " + TableList.DEPT + " d" +
                    " on o.id=d.org_id where d.id=?", companyId);
            msg.put("orgName", BaseUtil.objToStr(records.get("org_name"), "无"));
            msg.put("companyName", BaseUtil.objToStr(records.get("companyName"), "无"));
            msg_content=fromUser.getRealName()+"向您发起邀约，请登入app查收";
            notification_title = "邀约信息提醒";
        }
        //数据实体化
        VisitorRecord.set("companyId", companyId)
                .set("cstatus", cstatus).set("reason", reason).set("startDate", startDate).set("endDate", endDate)
                .set("visitDate", DateUtil.getCurDate()).set("visitTime", DateUtil.getCurTime()).set("recordType", recordType)
                .set("dateType", "limitPeriod").set("vitype", "A").set("isReceive", "F");
        //存入数据库
        boolean save = VisitorRecord.save();
        JSONObject obj = new JSONObject();//返回给websocket
        obj.put("sign", "success");
        obj.put("desc", "操作成功");
        obj.put("code", "200");
        obj.put("type", BaseUtil.objToInteger(msg.get("type"), 2));
        obj.put("id", VisitorRecord.getId());
        obj.put("userId", fromUserId);
        obj.put("visitorId", toUserId);
        obj.put("startDate", startDate);
        obj.put("endDate", endDate);
        if (save) {
            System.out.println("储存数据成功");
            //送还登入者 type=2
            toUserRemote.sendText(obj.toJSONString());
        }
        //用户在线，调用发送接口
        WebSocketEndPoint webSocketEndPoint = WebSocketMapUtil.get(toUserId);
        if (webSocketEndPoint != null) {
            msg.put("fromUserId", fromUserId);
            msg.put("id", VisitorRecord.getId());
            msg.put("visitDate", DateUtil.getCurDate());
            msg.put("visitTime", DateUtil.getCurTime());
            msg.put("dateType", "limitPeriod");
            msg.put("answerContent", "null");
            msg.put("replyDate", "null");
            msg.put("replyTime", "null");
            msg.put("vitype", "A");
            msg.put("replyUserId", "null");
            msg.put("realName", fromUser.getRealName());
            msg.put("idHandleImgUrl", BaseUtil.objToStr(fromUser.get("idHandleImgUrl"), ""));
            msg.put("niceName", BaseUtil.objToStr(fromUser.get("niceName"), ""));
            log.info(toUserId + "发送访问请求" + msg);
            webSocketEndPoint.getSession().getAsyncRemote().sendText(msg.toJSONString());
            //websocket发送完成后，改变下发状态
            VisitorRecord.setIsReceive("T").update();
        } else {
            //发送推送给用户

            String deviceToken = BaseUtil.objToStr(toUser.get("deviceToken"), "");
//				String deviceType = BaseUtil.objToStr(userMap.get("deviceType"), "0");
//            String isOnlineApp = BaseUtil.objToStr(toUser.get("isOnlineApp"), "F");
//            if ("F".equals(isOnlineApp)) {
//                //发送短信提醒
//
//            }
            String phone = BaseUtil.objToStr(toUser.get("phone"), "0");
            boolean single = GTNotification.Single(deviceToken, phone, notification_title, msg_content, msg_content);
            log.info("发送个推 推送成功? {}", single);
        }

    }

    private boolean check(Object userId, Object visitorId, Object recordType, Object endDate, Object startDate, RemoteEndpoint.Async remote) {

        Integer integer = Db.queryInt(Db.getSql("visitRecord.check"), userId, visitorId, recordType, endDate, startDate);
        if (integer != null) {
            remote.sendText(Result.ResultCodeType("fail", "在" + startDate + "——" + endDate + "内已经有邀约信息存在", "-1", 2));
            return true;
        }
        return false;
    }

    public Result gainMessagefromDb(Session session, String userId) throws Exception {
        //从数据库获取离线消息
        log.info("进入获取离线聊天消息");
        String sql = "select um.*,realName,headImgUrl from " + TableList.USER_MESSAGE + " um " +
                "left join " + TableList.DEPT_USER + " u on fromUserId=u.id" +
                " where toUserId = ?";
        List<Record> records = Db.find(sql, userId);
        if (records == null) {
            return Result.unDataResult("success", "无聊天记录需要获取");
        }
        Iterator<Map.Entry<String, Object>> iterator;
        Map.Entry<String, Object> entry;
        JSONObject obj = new JSONObject();
        for (Record record : records) {
            iterator = record.getColumns().entrySet().iterator();

            while (iterator.hasNext()) {
                entry = iterator.next();
                obj.put(entry.getKey(), entry.getValue() == null ? "null" : entry.getValue());
            }
            Integer type = BaseUtil.objToInteger(record.get("type"), 0);
            if (type == 4) {
                int count = Db.queryInt("select count(*) c from " + TableList.USER_FRIEND + " where friendId=? and applyType=0", userId);
                if (count != 0) {
                    obj.put("count", count);
                }
            }
            obj.remove("id");
            System.out.println(obj.toJSONString());
            //发送给session连接者
            session.getAsyncRemote().sendText(obj.toString());
        }
        int delete = Db.delete("delete from " + TableList.USER_MESSAGE +
                " where  toUserId=?", userId);
        if (delete > 0) {
            log.debug("{}:删除聊天记录成功", userId);
        }
        return Result.success();
    }

    public Result gainVisitRcordfromDb(Session session, String userId) {
        log.info("进入获取离线邀约");
        //1从数据库获取离线邀约消息
        List<Record> msgList = getVisitRecordByVisitorId(userId);
        if (msgList == null || msgList.isEmpty()) {
            log.info("无访问记录需要获取");
            return Result.unDataResult("success", "无访问记录需要获取");
        }
        JSONObject obj = new JSONObject();
        Iterator<Map.Entry<String, Object>> iterator;
        Map.Entry<String, Object> entry;
        String key;
        Object value;
        //2.根据list循环发送每一条邀约信息
        for (Record record : msgList) {
            iterator = record.getColumns().entrySet().iterator();
            while (iterator.hasNext()) {
                entry = iterator.next();
                obj.put(entry.getKey(), entry.getValue());
            }

            //判断消息来源 1访问 2 邀约
            obj.remove("userId");
            //fromUserId=谁发送的 当recordType=1时 发送人为userId
            if (record.getInt("recordType") == Constant.RECORDTYPE_VISITOR) {
                obj.put("fromUserId", record.get("userId"));
                //获取用户信息
                saveJson(record.getLong("userId"), obj);
                // 当recordType=1时 发送人为userId
            } else if (record.getInt("recordType") == Constant.RECORDTYPE_INVITE) {
                obj.put("fromUserId", record.get("visitorId"));
                //获取用户信息
                saveJson(BaseUtil.objToLong(record.get("visitorId"), 0L), obj);
            }
            obj.put("toUserId", userId);
            //2.1 发送信息时判断是(邀约/访问)还是应答（邀约/访问）
            if ("applyConfirm".equals(record.get("cstatus"))) {
                obj.put("type", Constant.MASSEGETYPE_VISITOR);
            } else {
                //如果状态不为applyConfirm 那么返回tpye=3作为应答
                obj.put("type", Constant.MASSEGETYPE_REPLY);
            }
            //发送给session连接者
            System.out.println("发送websocket消息:" + obj.toJSONString());
            session.getAsyncRemote().sendText(obj.toJSONString());
            //3.判断是否已经发送过消息给访客，是为T 否为F
            if (!("T".equals(record.get("isReceive")))) {

                Db.update(" update  " + TableList.VISITOR_RECORD +
                        " set isReceive='T'" +
                        " where  id = ? and SYSDATE()<endDate", record.getLong("id"));
            }
        }
        return Result.unDataResult("success", "访问记录获取成功");
    }

    public List<Record> getVisitRecordByVisitorId(String userId) {

        String coloumSql = " select * ";

        /* 查看谁访问我 我=被访者=visitorId=20 记录状态=recordType=1 阅读状态=未阅读='F' replyDate is null */
        String fromSql = " from(\n" +
                "select *  from " + TableList.VISITOR_RECORD + " where endDate>SYSDATE() and  isReceive ='F' and cstatus='applyConfirm' and" +
                " visitorId = " + userId + " and recordType=1 and vitype='A' and replyDate is null\n";
        /* 查看谁邀请我去访问 我=访客=userId=20 记录状态=recordType=2 阅读状态=未阅读='F' replyDate is null */
        String union1 = "union all\n" +
                "select *  from " + TableList.VISITOR_RECORD + " where endDate>SYSDATE() and  isReceive ='F' and cstatus='applyConfirm' and " +
                "userId =  " + userId + " and recordType=2 and vitype='A' and replyDate is  null\n";
        /* 查看谁回应了我的访问申请 我=访客=userId=20 replyDate is not null回应日期不为空，状态不是申请中 cstatus<>'applyConfirm' 则说明回应 记录状态=recordType=1 */
        String union2 = "union all\n" +
                "select *  from " + TableList.VISITOR_RECORD + " where endDate>SYSDATE() and  isReceive ='F' and cstatus<>'applyConfirm' " +
                "and userId =  " + userId + " and recordType=1 and vitype='A' and replyDate is not null\n";
        /* 查看谁回应了我的邀约申请 我=被访者=visitorId=20 replyDate is not null回应日期不为空，状态不是申请中 cstatus<>'applyConfirm' 则说明回应 记录状态=recordType=1  */
        String union3 = "union all \n" +
                "select *  from " + TableList.VISITOR_RECORD + " where endDate>SYSDATE() and  isReceive ='F' and cstatus<>'applyConfirm' " +
                "and visitorId =  " + userId + " and vitype='A' and recordType=2\n" +
                "and replyDate is not null";
        String suffix = ")x";
        System.out.println(coloumSql + fromSql + union1 + union2 + union3 + suffix);
        return Db.find(coloumSql + fromSql + union1 + union2 + union3 + suffix);
    }
}
