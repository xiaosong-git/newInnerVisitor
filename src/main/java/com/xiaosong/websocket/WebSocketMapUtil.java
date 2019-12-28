package com.xiaosong.websocket;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WebSocketMapUtil {
    public static ConcurrentMap<String, WebSocketEndPoint> webSocketMap = new ConcurrentHashMap<>();
    public static void put(String key, WebSocketEndPoint myWebSocket){
        webSocketMap.put(key, myWebSocket);
    }
    public static WebSocketEndPoint get(String key){
        return webSocketMap.get(key);
    }
    public static void remove(String key){
        webSocketMap.remove(key);
    }
    public static Collection<WebSocketEndPoint> getValues(){
        return webSocketMap.values();
    }

}
