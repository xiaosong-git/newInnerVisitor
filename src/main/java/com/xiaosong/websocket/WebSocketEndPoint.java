package com.xiaosong.websocket;

import com.jfinal.kit.LogKit;
import org.apache.log4j.Logger;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Stuart Douglas
 */
@ServerEndpoint(value="/myapp.ws",configurator=GetHttpSessionConfigurator.class)
public class WebSocketEndPoint {
    public Session session;
    //获取url后面参数
    public String queryString ;
    Logger logger = Logger.getLogger(WebSocketEndPoint.class);
    //群发
    @OnMessage
    public void message(String message, Session session) {
        for (Session s : session.getOpenSessions()) {
            s.getAsyncRemote().sendText(message);
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
        logger.info("====== onClose:"+session.getId()+" ======");
    }
    /**
     * 发生错误时触发的方法
     */
    @OnError
    public void onError(Session session,Throwable error){
        LogKit.info(session.getId()+"连接发生错误"+error.getMessage());
        logger.error(error);
    }

    /**
     * 给指定的人发送消息
     * @param message
     */
    public void sendToUser(String message) {
        String sendUserno = message.split("[|]")[1];
        String sendMessage = message.split("[|]")[0];
        try {
            if (WebSocketMapUtil.get(sendUserno) != null) {
                WebSocketMapUtil.get(sendUserno).sendMessage("用户" + sendUserno + "发来消息：" + " <br/> " + sendMessage);
            } else {
                System.out.println("当前用户不在线");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }
    private void saveUser(Session session) {

        //切分为userId，token
        String userId = getUserId(queryString);
        String token=getToken(queryString);
        logger.info("userId="+userId+",token="+token);
        this.session = session;
        WebSocketMapUtil.put(userId,this);
    }
    //切分为userId
    public String getUserId(String str){
        return str.substring(str.indexOf("=") +1,str.indexOf("&"));
    }
    //token
    public String getToken(String str){
       return str.substring(str.indexOf("=",str.indexOf("=")+1)+1);
    }
    //退出之前未关闭的异常登入状态
    public void quit(){
        ConcurrentMap<String, WebSocketEndPoint> webSocketMap = WebSocketMapUtil.webSocketMap;
        String userId = getUserId(queryString);
        logger.info(userId+"：用户在线"+webSocketMap.containsKey(userId));
        if (webSocketMap.containsKey(getUserId(queryString))) {
            try {
                webSocketMap.get(getUserId(queryString)).session.close();
            } catch (IOException e) {
               logger.error(e);
            }finally {
                logger.info("重新登入成功"+userId);
            }
        }
    }
}