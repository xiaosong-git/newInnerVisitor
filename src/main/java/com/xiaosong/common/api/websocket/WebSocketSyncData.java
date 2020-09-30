package com.xiaosong.common.api.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.xiaosong.MainConfig;
import com.xiaosong.common.api.utils.ApiDataUtils;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VKey;
import com.xiaosong.util.*;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ServerEndpoint(value = "/visitor/syncData", configurator = GetHttpSessionConfigurator.class)
public class WebSocketSyncData {

    public static final WebSocketSyncData me = new WebSocketSyncData();

    private Session session;
    private String key;
    //rsa加密私钥
    private String rsaPublicKey;

    // 若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static HashMap<String,WebSocketSyncData> webSocketSet = new HashMap<>();

    private String imgServerUrl = MainConfig.p.get("imgServerUrl");//图片服务地址

    @OnOpen
    public void open(Session session) {
        Object clienetIp = session.getUserProperties().get("client-ip").toString();
        System.out.println("客户端请求IP："+clienetIp);
        this.session = session;

    }

    @OnMessage
    public void receiveMessage(String message, Session session) {
        System.out.println(" send Message .. " + message);
        //上位机第一次连接注册上位机编号
        if(message.startsWith("pospCode:"))
        {
             key = message.replaceFirst("pospCode:","");
             VKey vKey = VKey.dao.findFirst("select * from v_key where  swi_code=?",key);
             if(vKey!=null) {
                 rsaPublicKey = vKey.getPublicKey();
                 webSocketSet.put(key, this);
             }
        }else {
            try {
                JSONObject jsonData = JSONObject.parseObject(message);
                String sign = jsonData.getString("sign");
                long timestamp = jsonData.getLong("timestamp");
                String nonce = jsonData.getString("nonce");
                String data = jsonData.getString("data");
                String checkSign =  SignUtils.getSign(timestamp,rsaPublicKey,nonce,data);
                //验证签名，如果签名验证不通过，那么断开websocket连接
                if(sign.equals(checkSign)) {
                    JSONArray jsonArray = jsonData.getJSONArray("data");
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject json = jsonArray.getJSONObject(i);
                        String userType = json.getString("userType");
                        int id = json.getInteger("id");
                        if ("staff".equals(userType)) {
                            Db.update("update " + TableList.DEPT_USER + " set isReceive='T' where id =" + id);
                        } else if ("visitor".equals(userType)) {
                            Db.update("update " + TableList.VISITOR_RECORD + " set isReceive='T' where id =" + id);
                        }
                    }
                }else{
                    throw new Exception("签名验证失败");
                }
            } catch (Exception ex) {
                ex.fillInStackTrace();
                this.close();
                return;
            }
        }
        sendStaffData();
        sendVisitorData();

    }

    @OnClose
    public void close() {
        webSocketSet.remove(this.key);
        System.out.println("Close is running ..."+key);
    }


    public void sendMessageToAll(String message)
    {
        for(String k : webSocketSet.keySet()){
            WebSocketSyncData item = webSocketSet.get(k);
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

    /**
     * 发送员工记录
     */
    public void sendStaffData()
    {
        String sql ="select a.id,a.realName,a.idHandleImgUrl,a.idNO,floor,org_code,org_name,currentStatus,cardNO,userType from v_dept_user a left join v_dept  b on a.deptId = b.id LEFT join v_org c on b.org_id = c.id where isAuth ='T' and IFNULL(isReceive,'')!= 'T' and userType ='staff'";
        SqlPara sqlPara = new SqlPara();
        sqlPara.setSql(sql);
        Page<Record> pageList =   Db.paginate(1,5,sqlPara);
        List list = pageList.getList();
        sendList(list);
    }

    /**
     *  发送批量导入的员工数据
     *
     */
    public void sendStaffList(List ids)
    {
        List<Record> records = new ArrayList<>();
        if(ids != null){
            for(int i =0;i<ids.size();i++){
                Record record = Db.findFirst("select * from v_dept_user where id = ?",ids.get(i));
                if(record != null){
                    records.add(record);
                }
            }
        }
        sendList(records);
    }
    private void  sendList(List list){
        List<Map<String,Object>> result = ApiDataUtils.apiList(list);
        for(Map<String,Object> map : result)
        {
            String idHandleImgUrl = map.get("idHandleImgUrl").toString();
            byte[] imgBytes= FilesUtils.getImageFromNetByUrl(imgServerUrl+ File.separator+idHandleImgUrl);
            if(imgBytes!=null) {
                String imgBase64 = Base64.encode(imgBytes);
                map.put("photo", imgBase64);
            }
        }
        if(result.size()>0) {
            String msg = JSON.toJSONString(result, SerializerFeature.WriteMapNullValue);
            sendMessageToAll(msg);
        }
    }

    /**
     * 发送访客记录
     */
    public void sendVisitorData()
    {
        String sql ="SELECT\n" +
                "\tt.id,\n" +
                "\ta.realName,\n" +
                "\ta.idHandleImgUrl,\n" +
                "\ta.idNO,\n" +
                "\tfloor,\n" +
                "\torg_code,\n" +
                "\tvisitorId companyUserId,\n" +
                "\torg_name,\n" +
                "\tcurrentStatus,\n" +
                "\tt.isReceive,\n" +
                "\tt.visitDate,\n" +
                "\tt.startDate,\n" +
                "\tt.endDate,\n" +
                "  a.id userId\n" +
                "FROM\n" +
                "\tv_visitor_record t\n" +
                "LEFT JOIN v_dept_user a ON t.userId = a.id\n" +
                "LEFT JOIN v_dept b ON a.deptId = b.id\n" +
                "LEFT JOIN v_org c ON b.org_id = c.id\n" +
                "WHERE\n" +
                "\tisAuth = 'T'\n" +
                "AND IFNULL(t.isReceive, '') != 'T'\n" +
                "AND t.cstatus = 'applySuccess' AND to_days(startDate) = to_days(now())";

        SqlPara sqlPara = new SqlPara();
        sqlPara.setSql(sql);
        Page<Record> pageList =   Db.paginate(1,5,sqlPara);
        List list = pageList.getList();
        List<Map<String,Object>> result = ApiDataUtils.apiList(list);
        for(Map<String,Object> map : result)
        {
            String idHandleImgUrl = map.get("idHandleImgUrl").toString();
            byte[] imgBytes= FilesUtils.getImageFromNetByUrl(imgServerUrl+ File.separator+idHandleImgUrl);
            if(imgBytes!=null) {
                String imgBase64 = Base64.encode(imgBytes);
                map.put("photo", imgBase64);
            }
            map.put("userType", "visitor");
        }
        if(result.size()>0) {
            String msg = JSON.toJSONString(result, SerializerFeature.WriteMapNullValue);
            sendMessageToAll(msg);
        }
    }




}