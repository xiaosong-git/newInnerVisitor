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
import com.xiaosong.model.VSync;
import com.xiaosong.util.*;
import com.xiaosong.util.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.File;
import java.util.*;

@ServerEndpoint(value = "/visitor/syncData", configurator = GetHttpSessionConfigurator.class)
public class WebSocketSyncData {

    public static final WebSocketSyncData me = new WebSocketSyncData();

    private Session session;
    private String key;
    //rsa加密私钥
    private String rsaPublicKey;

    // 若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static HashMap<String,WebSocketSyncData> webSocketSet = new HashMap<>();

    private static Hashtable<String,Long> lastSendTime = new Hashtable<>();


    private String imgServerUrl = MainConfig.p.get("imgServerUrl")+"imgserver/";//图片服务地址


    public WebSocketSyncData()
    {
        //checkThread.start();
    }


    @OnOpen
    public void open(Session session) {
        Object clienetIp = session.getUserProperties().get("client-ip").toString();
        System.out.println("客户端请求IP："+clienetIp);
        this.session = session;
    }

    @OnMessage
    public void receiveMessage(String message, Session session) {
        System.out.println(key+" receive Message .. " + message);
        //上位机第一次连接注册上位机编号
        if(message.startsWith("pospCode:"))
        {
             key = message.replaceFirst("pospCode:","");
             VKey vKey = VKey.dao.findFirst("select * from v_key where  swi_code=?",key);
             if(vKey!=null) {
                 rsaPublicKey = vKey.getPublicKey();
                 webSocketSet.put(key, this);
                 System.out.println(" 连接上位机设备 :" + key);
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

                    System.out.println("保存同步记录-------------------------");
                    JSONArray jsonArray = jsonData.getJSONArray("data");
                    for (int i = 0; i < jsonArray.size(); i++) {
                        System.out.println("保存同步记录-------------------------" + jsonArray.size());
                        JSONObject json = jsonArray.getJSONObject(i);
                        String userType = json.getString("userType");
                        Long id = json.getLong("id");
                        VSync vSync = VSync.dao.findFirst("select * from v_sync where pospCode=? and relationId=? and type=?", key, id, userType);
                        System.out.println("保存同步记录-------------------------" + userType + "————————" + id + "-----" + key);
                        if (vSync == null) {
                            try {
                                vSync = new VSync();
                                vSync.setIsReceive("T");
                                vSync.setPospCode(key);
                                vSync.setRelationId(id);
                                vSync.setType(userType);
                                System.out.println("开始保存同步记录");
                                boolean result = vSync.save();
                                System.out.println("保存同步记录" + result);
                            } catch (Exception ex) {
                                ex.fillInStackTrace();
                            }
                        } else {
                            System.out.println("已经存在该同步记录");
                        }
                        //有返回移除最后发送时间
                        String lastSendTimeKey = key + "_" + userType;
                        if (lastSendTime.containsKey(lastSendTimeKey)) {
                            lastSendTime.remove(lastSendTimeKey);
                        }

                    }
                }else{
                    sendMessage(message);
                    throw new Exception("签名验证失败");
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                ex.fillInStackTrace();
                //this.close();
                return;
            }
        }
        sendStaffData(key);
        sendVisitorData(key);

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
    public void sendStaffData(String... pospCode)
    {

        for(String k : webSocketSet.keySet()){
            if(pospCode!=null && pospCode.length>0 && !k.equals(pospCode[0]))
            {
                continue;
            }
            WebSocketSyncData item = webSocketSet.get(k);
            try {
                String sql ="select  b.dept_name,b.addr,a.isAuth,a.activeDate,a.expiryDate,a.id,a.realName,a.idHandleImgUrl,a.idNO,floor,org_code,org_name,currentStatus,cardNO,userType from v_dept_user a left join v_dept  b on a.deptId = b.id LEFT join v_org c on b.org_id = c.id left join v_sync d on a.id = d.relationId and d.type='staff' and d.pospCode ='"+k+"'  where IFNULL(userType,'') ='staff'  and IFNULL(d.isReceive,'')!= 'T'";
                SqlPara sqlPara = new SqlPara();
                sqlPara.setSql(sql);
                Page<Record> pageList =   Db.paginate(1,5,sqlPara);
                List list = pageList.getList();
                sendList(list,item);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }


    }

    /**
     *  发送批量导入的员工数据
     *
     */
    public void sendStaffList(List ids)
    {
        for(String k : webSocketSet.keySet()){
            WebSocketSyncData item = webSocketSet.get(k);
            try {
                List<Record> records = new ArrayList<>();
                if(ids != null){
                    for(int i =0;i<ids.size();i++){
                        Record record = Db.findFirst("select b.dept_name,b.addr,a.isAuth,a.activeDate,a.expiryDate,a.id,a.realName,a.idHandleImgUrl,a.idNO,floor,org_code,org_name,currentStatus,cardNO,userType from v_dept_user a left join v_dept  b on a.deptId = b.id LEFT join v_org c on b.org_id = c.id left join v_sync d on a.id = d.relationId and d.type='staff' and d.pospCode =?   where  IFNULL(userType,'') ='staff'  and IFNULL(d.isReceive,'')!= 'T'  and a.id = ?",k,ids.get(i));
                        if(record != null){
                            records.add(record);
                        }
                    }
                }
                sendList(records,item);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }


    }
    private void  sendList(List list,WebSocketSyncData webSocketSyncData){
        List<Map<String,Object>> result = ApiDataUtils.apiList(list);
        for(Map<String,Object> map : result)
        {

            String idHandleImgUrl = map.get("idHandleImgUrl").toString();
            String isAuth = map.get("isAuth").toString();
            if(!StringUtils.isBlank(idHandleImgUrl) && "T".equals(isAuth)) {
                byte[] imgBytes = FilesUtils.getImageFromNetByUrl(imgServerUrl + File.separator + idHandleImgUrl);
                if (imgBytes != null && imgBytes.length > 2 * 1024) {
                    String imgBase64 = Base64.encode(imgBytes);
                    map.put("photo", imgBase64);
                }
            }
            map.put("userType", "staff");
        }
        if(result.size()>0) {
            String msg = JSON.toJSONString(result, SerializerFeature.WriteMapNullValue);
           // sendMessageToAll(msg);
            webSocketSyncData.sendMessage(msg);
            lastSendTime.put(webSocketSyncData.key+"_staff",System.currentTimeMillis());
        }
    }

    /**
     * 发送访客记录
     */
    public void sendVisitorData(String... pospCode)
    {

        for(String k : webSocketSet.keySet()){

            if(pospCode!=null && pospCode.length>0 && !k.equals(pospCode[0]))
            {
                continue;
            }
            WebSocketSyncData item = webSocketSet.get(k);
            try {
                String sql ="SELECT\n" +
                        "t.id,\n" +
                        "a.realName,\n" +
                        "a.idHandleImgUrl,\n" +
                        "a.idNO,\n" +
                        "floor,\n" +
                        "org_code,\n" +
                        "visitorId companyUserId,\n" +
                        "org_name,\n" +
                        "a.currentStatus,\n" +
                        "t.isReceive,\n" +
                        "t.visitDate,\n" +
                        "t.startDate,\n" +
                        "t.endDate,\n" +
                        "  a.id userId\n" +
                        "FROM\n" +
                        "v_visitor_record t\n" +
                        "LEFT JOIN v_dept_user a ON t.userId = a.id\n" +
                        "LEFT JOIN v_dept_user v ON t.visitorId = v.id\n" +
                        "LEFT JOIN v_dept b ON v.deptId = b.id\n" +
                        "LEFT JOIN v_org c ON b.org_id = c.id\n" +
                        "left join v_sync d on t.id = d.relationId and d.type='visitor' and d.pospCode = '" + k +"'\n"+
                "WHERE\n" +
                        "\ta.isAuth = 'T'\n" +
                        "AND IFNULL(d.isReceive, '') != 'T'\n" +
                        "AND t.cstatus = 'applySuccess' AND endDate >'"+DateUtil.getSystemTime()+"' and startDate <='"+DateUtil.getSystemTomorrowTime()+"'";
                SqlPara sqlPara = new SqlPara();
                sqlPara.setSql(sql);
                Page<Record> pageList =   Db.paginate(1,5,sqlPara);
                List list = pageList.getList();
                List<Map<String,Object>> result = ApiDataUtils.apiList(list);
                for(Map<String,Object> map : result)
                {
                    String idHandleImgUrl = map.get("idHandleImgUrl").toString();
                    if(!StringUtils.isBlank(idHandleImgUrl)) {
                        byte[] imgBytes = FilesUtils.getImageFromNetByUrl(imgServerUrl + File.separator + idHandleImgUrl);
                        if (imgBytes != null && imgBytes.length > 2 * 1024) {
                            String imgBase64 = Base64.encode(imgBytes);
                            map.put("photo", imgBase64);
                        }
                    }
                    map.put("userType", "visitor");
                }
                if(result.size()>0) {
                    String msg = JSON.toJSONString(result, SerializerFeature.WriteMapNullValue);
                    //sendMessageToAll(msg);
                    item.sendMessage(msg);
                    lastSendTime.put(item.key+"_visitor",System.currentTimeMillis());
                }

            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }



    Thread checkThread = new Thread(new Runnable() {
        @Override
        public void run() {

            while(1==1)
            {
                for(String key : lastSendTime.keySet())
                {
                       Long lastTime = lastSendTime.get(key);
                       //有十秒没返回的数据，那么重新发送
                       if(System.currentTimeMillis() - lastTime >10000){
                           if(key!=null){
                               System.out.println("搜索到十秒没有返回值的记录，开始重新发送");
                               String [] keySplit = key.split("_");
                               if(keySplit.length==2)
                               {
                                   String webSocketKey = keySplit[0];
                                   String type =keySplit[1];
                                   if(webSocketSet.containsKey(webSocketKey))
                                   {
                                        lastSendTime.put(key,System.currentTimeMillis());
                                        if("staff".equals(type))
                                        {
                                            sendStaffData(webSocketKey);
                                        }else if("visitor".equals(type))
                                        {
                                            sendVisitorData(type);
                                        }
                                   }
                               }
                           }
                       }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });





}