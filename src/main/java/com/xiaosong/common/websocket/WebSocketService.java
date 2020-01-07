package com.xiaosong.common.websocket;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.compose.Result;
import com.xiaosong.common.user.UserService;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VAppUser;
import com.xiaosong.model.VAppUserMessage;
import com.xiaosong.model.VVisitorRecord;
import com.xiaosong.util.BaseUtil;
import com.xiaosong.util.DateUtil;
import com.xiaosong.util.GTNotification;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @program:  innervisitor
 * @description:
 * @author: cwf
 * @create: 2020-01-02 16:27
 **/
public class WebSocketService {
    public static final WebSocketService me = new WebSocketService();
    Log log=Log.getLog(WebSocketService.class);

    /**
     *  判断用户是否为好友，非好友则返回信息
     * @return  是否为好友
     * @throws IOException
     * @author cwf
     * @date 2020/1/2 14:45
     */
    public boolean isFriend(Session session , JSONObject msg) throws IOException {

        int type= msg.getInteger("type");
        String userId = WebSocketEndPoint.me.getUserId(session.getQueryString());
        Long friendId= msg.getLong("toUserId");
        Record friend = Db.findFirst(Db.getSql("appUser.findFriend"), friendId, userId);
        JSONObject obj = new JSONObject();
        obj.put("fromUserId", friendId);
        obj.put("toUserId",userId);
        obj.put("message", "对方开启了好友验证，你还不是他好友，请先发送好友验证请求！");
        obj.put("type", type);
        saveJson(friendId,obj);
        if (friend==null||friend.get("applyType")!=(Object) 1){
            session.getAsyncRemote().sendText(obj.toString());
            //非好友
            return false;
        }
        Record user = Db.findFirst(Db.getSql("appUser.findFriend"),userId,friendId);
        if (user==null){
            obj.put("message", "您还不是对方好友，请添加好友！");
            session.getAsyncRemote().sendText(obj.toString());
            //非好友
            return false;
        }else  {
            int applyType = BaseUtil.objToInteger(user.get("applyType"), 2);
            if (applyType!=1) {
                obj.put("message", "您还不是对方好友，请添加好友！");
                if(applyType==2){
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
     *
     */
    public void saveJson(Long fromUserId, JSONObject obj){
        VAppUser appUser = VAppUser.dao.findById(fromUserId);
        if (appUser!=null){
        obj.put("realName",appUser.getRealName());
        obj.put("nickName",appUser.getNiceName());
        obj.put("headImgUrl",appUser.getHeadImgUrl());
        obj.put("idHandleImgUrl",appUser.getIdHandleImgUrl());
        obj.put("orgId",appUser.getOrgId());
        }
    }

    /**
     * 好友聊天 好友申请
     * @param session
     * @param msg
     */
    public void dealChat(Session session, JSONObject msg,String fromUserId,String toUserId,int type) { //获取主要消息
        String content=msg.getString("message");
        if (content==null){
            return ;
        }
        //传输对象
        //判断
         JSONObject obj = new JSONObject();
        try {//发送给toUserId
            WebSocketEndPoint webSocketEndPoint = WebSocketMapUtil.get(toUserId);
            //用户在线，调用发送接口
            RemoteEndpoint.Async fromUserRemote = session.getAsyncRemote();
            if (webSocketEndPoint!=null) {
                obj.put("message", msg.getString("message"));
                obj.put("toUserId", toUserId);
                obj.put("fromUserId", fromUserId);
                obj.put("type",type);
                obj.put("updateTime", DateUtil.getSystemTime());
                //查看用户信息
                saveJson(Long.valueOf(fromUserId),obj);
                //查看好友申请数量
                if (type==4){
                    Integer count = Db.queryInt("select count(*) c from " + TableList.USER_FRIEND + " where friendId=" + toUserId + " and applyType=0");
                    if (count!=0){
                        obj.put("count",count);
                    }
                }
                webSocketEndPoint.getSession().getAsyncRemote().sendText(obj.toString());
                fromUserRemote.sendText(Result.ResultCodeType("success","发送成功","200",type));
                //用户不在线，插入数据库
            } else {
                VAppUserMessage vAppUserMessage=new VAppUserMessage();
                boolean save = vAppUserMessage.setFromUserId(Long.valueOf(fromUserId)).setToUserId(Long.valueOf(toUserId))
                        .setMessage(content).setUpdateTime(DateUtil.getSystemTime()).setType(type).save();
                if (save){
                    fromUserRemote.sendText(Result.ResultCodeType("success","发送成功","200",type));
                    //发送推送
                    VAppUser vAppUser=VAppUser.dao.findById(toUserId);
                    String notification_title=type==4?"您有一条好友申请需处理！":"您有一条聊天消息需处理！";
                    //个推
                    GTNotification.Single(vAppUser.getDeviceToken(), vAppUser.getPhone(), notification_title, content, content);
                }else {
                    fromUserRemote.sendText(Result.ResultCodeType("fail","发送失败","-1",type));
                }
            }
        }catch (Exception e){
            log.error("发送聊天错误",e);
        }
    }
    public void  receiveVisit(Session session, JSONObject msg, String fromUserId, String toUserId, int type, Integer recordType) throws Exception {
            //被访人或被约人
            String cstatus=BaseUtil.objToStr(msg.get("cstatus"),"applyConfirm");
            String startDate=BaseUtil.objToStr(msg.get("startDate"),DateUtil.getCurDate());
            String endDate=BaseUtil.objToStr(msg.get("endDate"),DateUtil.addMinute(DateUtil.getSystemTime(),720));
            String reason=BaseUtil.objToStr(msg.get("reason"),"无");
            /** update by cwf  2019/9/24 10:33 Reason: 由被访人选择公司，*/
            //公司名称改为部门
            Integer companyId = BaseUtil.objToInteger(msg.get("companyId"),0);
            //查询登入人信息
            VAppUser fromUser = VAppUser.dao.findById(fromUserId);
            VAppUser toUser = VAppUser.dao.findById(toUserId);
            //登入人公司ID与大楼ID
            Integer check =null;
            String notification_title = "访问信息提醒";
            //查询被访人或被约人是否实名
            String recordStr=recordType==1?"您访问的用户":"您邀请的用户";
            String Str=recordType==1?"访问":"邀约";
            RemoteEndpoint.Async toUserRemote = session.getAsyncRemote();
        try {
            boolean verify = UserService.me.isVerify(toUserId);
            log.info("被访者或被邀约者{}实名:{}",toUserId,verify);
            if (!verify){
                log.info(recordStr+"未实名,无法进行"+Str);
                toUserRemote.sendText(Result.ResultCodeType("fail",recordStr+"未实名,无法进行"+Str,"-1",BaseUtil.objToInteger(msg.get("type"),2)));
                return;
            }
            if (companyId==0){
                Integer id = Db.queryInt("select id from " + TableList.DEPT_USER + " where userId=? and currentStatus='normal' and status='applySuc'", toUserId);
                if (id==null){
                    toUserRemote.sendText(Result.ResultCodeType("fail",recordStr+"无公司归属,无法进行"+Str,"-1",BaseUtil.objToInteger(msg.get("type"),2)));
                    return;
                }
            }
            //如果是访问recordType=1
            if (recordType==1){
                //查询内部是否有邀约信息
                check = Db.queryInt(Db.getSql("visitRecord.check"), fromUserId,toUserId, recordType, endDate, startDate);
                //如果是邀约recordType=2 访客与被访者在数据库中位置调换
            } else if(recordType==2){
                check = Db.queryInt(Db.getSql("visitRecord.check"), toUserId, fromUserId, recordType, endDate, startDate);
                notification_title="邀约信息提醒";
            }
            if (check != null) {
                //发送回消息
                toUserRemote.sendText(Result.ResultCodeType("fail","在"+startDate+"——"+endDate+"内已经有邀约信息存在","-1",BaseUtil.objToInteger(msg.get("type"),2)));
                log.info(startDate+"该时间段"+endDate+"内已经有邀约信息存在");
                return ;
            }
            Map<String,Object> paramMap=new HashMap<>();
            //查询大楼编码
            String orgName=null;
            String companyName=null;
            if (recordType==2) {
                Record records = Db.findFirst("select o.org_name,o.org_code,c.companyName from " + TableList.ORG + " o left join " + TableList.COMPANY + " c" +
                        " on o.id=c.orgId where c.id=?", companyId);
                String orgCode=BaseUtil.objToStr(records.get("org_code"),"无");
                orgName=BaseUtil.objToStr(records.get("org_name"),"无");
                companyName=BaseUtil.objToStr(records.get("companyName"),"无");
                System.out.println(records);
                paramMap.put("orgCode",orgCode);
            }
            VVisitorRecord VisitorRecord=new VVisitorRecord();
            VisitorRecord.set("userId",fromUserId).set("visitorId",toUserId).set("companyId",companyId)
                    .set("cstatus",cstatus).set("reason",reason).set("startDate",startDate).set("endDate",endDate)
                    .set("visitDate",DateUtil.getCurDate()).set("visitTime",DateUtil.getCurTime()).set("recordType",recordType)
                    .set("dateType","limitPeriod").set("vitype","A").set("isReceive","F");
            //存入数据库
            boolean save = VisitorRecord.save();
            JSONObject obj = new JSONObject();
            obj.put("sign","success");
            obj.put("desc","操作成功");
            obj.put("code","200");
            obj.put("type",BaseUtil.objToInteger(msg.get("type"),2));
            obj.put("id",VisitorRecord.getId());
            obj.put("userId",fromUserId);
            obj.put("visitorId",toUserId);
            obj.put("startDate",startDate);
            obj.put("endDate",endDate);
            if (save){
                System.out.println("储存数据成功");
                //送还登入者 type=2
                toUserRemote.sendText(obj.toJSONString());
            }
            //用户在线，调用发送接口
            WebSocketEndPoint webSocketEndPoint = WebSocketMapUtil.get(toUserId);
            if (webSocketEndPoint!=null) {
                msg.put("orgName",orgName);
                msg.put("companyName",companyName);
                msg.put("fromUserId",fromUserId);
                msg.put("id",VisitorRecord.getId());
                msg.put("visitDate",DateUtil.getCurDate());
                msg.put("visitTime",DateUtil.getCurTime());
                msg.put("dateType","limitPeriod");
                msg.put("answerContent","null");
                msg.put("replyDate","null");
                msg.put("replyTime","null");
                msg.put("vitype","A");
                msg.put("replyUserId","null");
                msg.put("realName",fromUser.getRealName());
                msg.put("idHandleImgUrl",BaseUtil.objToStr(fromUser.get("idHandleImgUrl"),""));
                msg.put("niceName",BaseUtil.objToStr(fromUser.get("niceName"),""));
                System.out.println(toUserId+"发送访问请求"+msg);
                webSocketEndPoint.getSession().getAsyncRemote().sendText(msg.toJSONString());
                Map<String,Object> updateMap=new HashMap<>();
                //websocket发送完成后，改变下发状态
                VisitorRecord.setIsReceive("T").update();
            }else{
                //发送推送给用户
                String msg_content = "【朋悦比邻】您好，您有一条预约访客申请，请登入app查收!";
                String deviceToken = BaseUtil.objToStr(toUser.get("deviceToken"), "");
//				String deviceType = BaseUtil.objToStr(userMap.get("deviceType"), "0");
				String isOnlineApp = BaseUtil.objToStr(toUser.get("isOnlineApp"),"F");
				if ("F".equals(isOnlineApp)){
				    //发送短信提醒

                }
                String phone = BaseUtil.objToStr(toUser.get("phone"), "0");
                boolean	 single = GTNotification.Single(deviceToken, phone, notification_title, msg_content, msg_content);
                log.info("发送个推 推送成功? {}",single);
            }
        }catch (Exception e){
            log.error("错误",e);
            toUserRemote.sendText(Result.ResultCodeType("fail","系统异常","500",BaseUtil.objToInteger(msg.get("type"),2)));
            return ;
        }
    }
}
