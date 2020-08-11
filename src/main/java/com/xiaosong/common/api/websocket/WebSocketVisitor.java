package com.xiaosong.common.api.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.api.visitorRecord.VisitorRecordService;
import com.xiaosong.util.BaseUtil;
import com.xiaosong.util.IdCardUtil;
import org.apache.commons.lang3.StringUtils;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/visitor/bigScreen", configurator = GetHttpSessionConfigurator.class)
public class WebSocketVisitor {

    public static final WebSocketVisitor me = new WebSocketVisitor();

    private Session session;
    // 若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static CopyOnWriteArraySet<WebSocketVisitor> webSocketSet = new CopyOnWriteArraySet<WebSocketVisitor>();

    @OnOpen
    public void open(Session session) {
        System.out.println("OPEN is running");
        webSocketSet.add(this);
        this.session = session;


    }

    @OnMessage
    public void receiveMessage(String message, Session session) {
        System.out.println(" send Message .. " + message);

        List<Record> list = VisitorRecordService.me.findTopRecordByNum(10);
        List< HashMap<String,Object>> result = new ArrayList<>();
        if(list!=null && list.size()>0)
        {
            for(Record record : list)
            {
                HashMap<String,Object> map = new HashMap<>();
                map.put("id",record.get("id"));
                map.put("idNO",desensitizedIdNumber(record.get("idNO")));
                map.put("realName",record.get("realName"));
                map.put("startDate",record.get("startDate"));
                map.put("endDate",record.get("endDate"));
                map.put("cStatus",record.get("cStatus"));
                result.add(map);
            }
        }
        String msg =  JSON.toJSONString(result, SerializerFeature.WriteMapNullValue);
        sendMessageToAll(msg);

    }

    @OnClose
    public void close() {
        webSocketSet.remove(this);
        System.out.println("Close is running ...");
    }


    public void sendReceiveVisitMsg(String idNo,String realName,String startDate,String endDate,String cStatus)
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("idNO",desensitizedIdNumber(idNo));
        jsonObject.put("realName",realName);
        jsonObject.put("startDate",startDate);
        jsonObject.put("endDate",endDate);
        jsonObject.put("cStatus",cStatus);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(jsonObject);
        sendMessageToAll(jsonArray.toJSONString());
    }



    public void visitReply(String id,String cStatus)
    {
        Record record = VisitorRecordService.me.findRecordById(id);
        List< HashMap<String,Object>> result = new ArrayList<>();
        if(record!=null)
        {
            HashMap<String,Object> map = new HashMap<>();
            map.put("id",record.get("id"));
            map.put("idNO",desensitizedIdNumber(record.get("idNO")));
            map.put("realName",record.get("realName"));
            map.put("startDate",record.get("startDate"));
            map.put("endDate",record.get("endDate"));
            map.put("cStatus",cStatus);
            result.add(map);
        }
        sendMessageToAll(JSON.toJSONString(result, SerializerFeature.WriteMapNullValue));
    }


    public void visitReply(JSONObject msg)
    {

        long id = msg.getLong("id");
        String cStatus = BaseUtil.objToStr(msg.get("cstatus"), null);
        Record record = VisitorRecordService.me.findRecordById(id);
        List< HashMap<String,Object>> result = new ArrayList<>();
        if(record!=null)
        {
            HashMap<String,Object> map = new HashMap<>();
            map.put("id",record.get("id"));
            map.put("idNO",desensitizedIdNumber(record.get("idNO")));
            map.put("realName",record.get("realName"));
            map.put("startDate",record.get("startDate"));
            map.put("endDate",record.get("endDate"));
            map.put("cStatus",cStatus);
            result.add(map);
        }
        sendMessageToAll(JSON.toJSONString(result, SerializerFeature.WriteMapNullValue));
    }


    public void sendMessageToAll(String message)
    {
        for(WebSocketVisitor item: webSocketSet){
            try {
               item.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    private void sendMessage(String message)
    {
        session.getAsyncRemote().sendText(message);
    }



    private String desensitizedIdNumber(String idNO)
    {
        //身份证号解密
        //DESUtil.decode()
        if(StringUtils.isBlank(idNO))
        {
            return "";
        }
        return IdCardUtil.desensitizedIdNumber(idNO);

    }

}