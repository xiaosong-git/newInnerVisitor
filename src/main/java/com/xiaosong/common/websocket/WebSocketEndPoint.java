package com.xiaosong.common.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.log.Log;
import com.xiaosong.constant.Constant;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Stuart Douglas
 */
@ServerEndpoint(value="/visitor/chat",configurator=GetHttpSessionConfigurator.class)
public class WebSocketEndPoint {
    private Session session;
    //获取url后面参数
    private String queryString ;
    private Log log = Log.getLog(WebSocketEndPoint.class);
    protected static final WebSocketEndPoint me = new WebSocketEndPoint();
    //接收消息
    @OnMessage
    public void message(String message, Session session) {
        int type=0;
        //解析消息
        try {
            //增加心跳检测
            if( "ping".equals(message)){
                session.getAsyncRemote().sendText("pong");
                return  ;
            }
            log.info("处理要发送的消息：{}",message);
            JSONObject msg = JSON.parseObject(message);
            type= msg.getInteger("type");
            getUserId(queryString);
//            //判断是否为好友，非好友则返回信息
//            //是好友
            if (WebSocketService.me.isFriend(session, msg)){
                switch (type){
                    case Constant.MSG_VISITOR:
//                        visitorRecordService.receiveVisit(session,msg);
                        log.info("访问");
                        break;
                    case Constant.MSG_REPLY:
//                        visitorRecordService.visitReply(session,msg);
                        log.info("回应");
                    default:
                        WebSocketService.me.dealChat(session,msg);
                }
            }
        }catch (Exception e){
            log.error("发送数据报错:{}",e);
            session.getAsyncRemote().sendText("发送失败");
            return;
        }
    }
    /**
     * 连接建立后触发的方法
     */
    @OnOpen
    public void onOpen(Session session,EndpointConfig config) throws IOException {
        //url之后的参数
        this.queryString=session.getQueryString();
        //查询登入人数
        System.out.println( WebSocketMapUtil.getValues());
        //退出之前的登入
        quit();
        //保存用户
        saveUser(session);
    }
    /**
     * 连接关闭后触发的方法
     */
    @OnClose
    public void onClose() {
        WebSocketMapUtil.remove(getUserId(queryString));
        log.info("====== onClose:"+session.getId()+" ======");
    }
    /**
     * 发生错误时触发的方法
     */
    @OnError
    public void onError(Session session,Throwable error){
        log.error(error.getMessage());
    }

    private void saveUser(Session session) {
        //切分为userId，token
        String userId = getUserId(queryString);
//        String token=getToken(queryString);
//        log.info("userId="+userId+",token="+token);
        this.session = session;
        WebSocketMapUtil.put(userId,this);
    }
    //切分为userId
    public String getUserId(String str){
        return str.substring(str.indexOf("=") +1,str.indexOf("&"));
    }
    //token
    private String getToken(String str){
       return str.substring(str.indexOf("=",str.indexOf("=")+1)+1);
    }
    //退出之前未关闭的异常登入状态
    private void quit(){
        ConcurrentMap<String, WebSocketEndPoint> webSocketMap = WebSocketMapUtil.webSocketMap;

        String userId = getUserId(queryString);
//        log.info(userId+"：用户在线"+webSocketMap.containsKey(userId));
        if (webSocketMap.containsKey(getUserId(queryString))) {
            try {
                webSocketMap.get(getUserId(queryString)).session.close();
            } catch (IOException e) {
               log.error(e.getMessage());
            }finally {
                log.info("重新登入成功"+userId);
            }
        }
    }
}