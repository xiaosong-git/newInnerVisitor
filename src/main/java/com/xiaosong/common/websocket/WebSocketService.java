package com.xiaosong.common.websocket;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.compose.Result;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VAppUser;
import com.xiaosong.model.VAppUserMessage;
import com.xiaosong.util.BaseUtil;
import com.xiaosong.util.DateUtil;
import com.xiaosong.util.GTNotification;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

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
        Record friend = Db.findFirst(Db.getSql("vAppUser.findFriend"), friendId, userId);
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
        Record user = Db.findFirst(Db.getSql("vAppUser.findFriend"),userId,friendId);
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
    public void dealChat(Session session, JSONObject msg) { //获取主要消息
        String content=msg.getString("message");
        if (content==null){
            return ;
        }
        //获取fromUserId
         String fromUserId = WebSocketEndPoint.me.getUserId(session.getQueryString());
        //传输对象
         String toUserId= msg.getString("toUserId");
         Integer type= msg.getInteger("type");
        //判断
         JSONObject obj = new JSONObject();
        try {
            WebSocketEndPoint webSocketEndPoint = WebSocketMapUtil.get(toUserId);
            //用户在线，调用发送接口
            RemoteEndpoint.Async asyncRemote = session.getAsyncRemote();
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
                asyncRemote.sendText(Result.ResultCodeType("success","发送成功","200",type));
                //用户不在线，插入数据库
            } else {
                VAppUserMessage vAppUserMessage=new VAppUserMessage();
                boolean save = vAppUserMessage.setFromUserId(Long.valueOf(fromUserId)).setToUserId(Long.valueOf(toUserId))
                        .setMessage(content).setUpdateTime(DateUtil.getSystemTime()).setType(type).save();
                if (save){
                    asyncRemote.sendText(Result.ResultCodeType("success","发送成功","200",type));
                    //发送推送
                    VAppUser vAppUser=VAppUser.dao.findById(toUserId);
                    String notification_title=type==4?"您有一条好友申请需处理！":"您有一条聊天消息需处理！";
                    //个推
                    GTNotification.Single(vAppUser.getDeviceToken(), vAppUser.getPhone(), notification_title, content, content);
                }else {
                    asyncRemote.sendText(Result.ResultCodeType("fail","发送失败","-1",type));
                }
            }
        }catch (Exception e){
            log.error("发送聊天错误",e);
        }
    }
}
