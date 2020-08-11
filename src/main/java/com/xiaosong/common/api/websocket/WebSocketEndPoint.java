package com.xiaosong.common.api.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.log.Log;
import com.xiaosong.common.api.visitorRecord.VisitorRecordService;
import com.xiaosong.constant.Constant;
import com.xiaosong.util.BaseUtil;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Stuart Douglas
 */
@ServerEndpoint(value = "/visitor/chat", configurator = GetHttpSessionConfigurator.class)
public class WebSocketEndPoint {
    private Session session;
    //获取url后面参数
    private String queryString;
    private Log log = Log.getLog(WebSocketEndPoint.class);
    public static final WebSocketEndPoint me = new WebSocketEndPoint();

    //接收消息
    @OnMessage
    public void message(String message, Session session) {
        int type = 0;
        //解析消息
        try {
            //增加心跳检测
            if ("ping".equals(message)) {
                session.getAsyncRemote().sendText("pong");
                return;
            }
            log.info("处理要发送的消息：{}", message);
            JSONObject msg = JSON.parseObject(message);
            type = msg.getInteger("type");//类型 1--普通聊天 2--访问邀约 3--回应访问邀约
            String fromUserId = getUserId(queryString);//来源
            String toUserId = msg.getString("toUserId");//去向
            Integer recordType = BaseUtil.objToInteger(msg.get("recordType"), 0); // 1--访问 2--邀约
            switch (type) {
                case Constant.MSG_VISITOR:
                    WebSocketService.me.receiveVisit(session, msg, fromUserId, toUserId, recordType);
                   // WebSocketVisitor.me.sendMessageToAll(message);
                    break;
                case Constant.MSG_REPLY:
                    VisitorRecordService.me.visitReply(session, msg);
                    WebSocketVisitor.me.visitReply(msg);
                    log.info("回应");
                    break;
                default://1为聊天 4为好友申请
                    //            //判断是否为好友，非好友则返回信息
                    if (WebSocketService.me.isFriend(session, msg)) {
                        WebSocketService.me.dealChat(session, msg, fromUserId, toUserId, type);
                    }
            }
        } catch (Exception e) {
            log.error("发送数据报错:{}", e);
            session.getAsyncRemote().sendText("发送失败");
            return;
        }
    }

    /**
     * 连接建立后触发的方法
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) throws Exception {
        //url之后的参数
        this.queryString = session.getQueryString();
        //查询登入人数
//        System.out.println( WebSocketMapUtil.getValues());
        //退出之前的登入
        quit();
        //保存用户
        saveUser(session);
        WebSocketService.me.gainMessagefromDb(session, getUserId(queryString));
        //获取当前登入人：userId的邀约消息
        WebSocketService.me.gainVisitRcordfromDb(session, getUserId(queryString));
    }

    /**
     * 连接关闭后触发的方法
     */
    @OnClose
    public void onClose() {
        String userId = getUserId(queryString);
        WebSocketMapUtil.remove(userId);
        log.info("====== onClose:" + userId + " ======");
    }

    /**
     * 发生错误时触发的方法
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error(error.getMessage());
    }

    private void saveUser(Session session) {
        //切分为userId，token
        String userId = getUserId(queryString);
//        String token=getToken(queryString);
//        log.info("userId="+userId+",token="+token);
        this.session = session;
        WebSocketMapUtil.put(userId, this);
    }

    //切分为userId
    public String getUserId(String str) {
        return str.substring(str.indexOf("=") + 1, str.indexOf("&"));
    }

    //token
    private String getToken(String str) {
        return str.substring(str.indexOf("=", str.indexOf("=") + 1) + 1);
    }

    //退出之前未关闭的异常登入状态
    private void quit() {
        ConcurrentMap<String, WebSocketEndPoint> webSocketMap = WebSocketMapUtil.webSocketMap;

        String userId = getUserId(queryString);
//        log.info(userId+"：用户在线"+webSocketMap.containsKey(userId));
        if (webSocketMap.containsKey(getUserId(queryString))) {
            try {
                webSocketMap.get(getUserId(queryString)).session.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            } finally {
                log.info("重新登入成功" + userId);
            }
        }
    }

    public Session getSession() {
        return this.session;
    }
}