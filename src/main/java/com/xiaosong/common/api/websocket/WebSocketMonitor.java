package com.xiaosong.common.api.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.bean.MonitorResult;
import com.xiaosong.common.api.utils.ApiDataUtils;
import com.xiaosong.common.api.visitorRecord.VisitorRecordService;
import com.xiaosong.common.web.monitor.MonitorCenterController;
import com.xiaosong.common.web.monitor.MonitorCenterService;
import com.xiaosong.constant.MonitorType;
import com.xiaosong.constant.PersonType;
import com.xiaosong.util.BaseUtil;
import com.xiaosong.util.IdCardUtil;
import org.apache.commons.lang3.StringUtils;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.xiaosong.util.DateUtil.findDates;
import static com.xiaosong.util.DateUtil.getTimeInterval;

@ServerEndpoint(value = "/visitor/monitorCenter", configurator = GetHttpSessionConfigurator.class)
public class WebSocketMonitor {

    public static final WebSocketMonitor me = new WebSocketMonitor();
    private Log log = Log.getLog(MonitorCenterController.class);
    public MonitorCenterService srv = MonitorCenterService.me;
    private Session session;
    // 若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static CopyOnWriteArraySet<WebSocketMonitor> webSocketSet = new CopyOnWriteArraySet<WebSocketMonitor>();

    @OnOpen
    public void open(Session session) {
        System.out.println("OPEN is running");
        webSocketSet.add(this);
        this.session = session;
    }

    @OnMessage
    public void receiveMessage(String message, Session session) {
        System.out.println(" send Message .. " + message);
        getAllData();
    }

    @OnClose
    public void close() {
        webSocketSet.remove(this);
        System.out.println("Close is running ...");
    }


    public void sendMessageToAll(String message)
    {
        for(WebSocketMonitor item: webSocketSet){
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
     * 获取所有数据
     */
    public void getAllData()
    {
            getPassToday();
            getInStatTodayByHour();
            getPersonNum();
            getVisitorNumWeek();
            getVisitorStatToday();
            getDeviceStatus();
    }


    /**
     * 获取访客数据
     */
    public void getVisitorData()
    {
        getPersonNum();
        getVisitorNumWeek();
        getVisitorStatToday();
    }


    /**
     * 获取通行数据
     */
    public void getPassData()
    {
        getPassToday();
        getInStatTodayByHour();
    }


    /**
     * 今日通行情况
     */
    public void getPassToday()
    {
        MonitorResult monitorResult = new MonitorResult();
        HashMap<String,Object> resultMap = new HashMap<>();
        List<Record> list =  srv.getPassToday();
        List<Record> resultList = new ArrayList<>();
        int visitorTotal = 0;
        int staffTotal = 0;

        for(Record record : list)
        {
            Record map = null;
            //判断是否有相应的门号,如果有取相应记录
            for( Record item : resultList)
            {
                if(item.get("gate").equals(record.get("gate")))
                {
                    map = item;
                    break;
                }
            }
            if(map == null)
            {
                map = new Record();
                resultList.add(map);
            }
            String userType = record.get("userType");
            int num =  record.getInt("num");
            map.set("gate",record.get("gate"));
            if(PersonType.STAFF.equals(userType)) {
                map.set("staffNum", num);
                staffTotal+=num;
            }else{
                map.set("visitorNum",num);
                visitorTotal+=num;
            }

        }
        resultMap.put("visitorTotal",visitorTotal);
        resultMap.put("staffTotal",staffTotal);
        resultMap.put("data",ApiDataUtils.apiList(resultList));

        monitorResult.setData(resultMap);
        monitorResult.setSucc(true);
        monitorResult.setType(MonitorType.GET_PASSTODAY);
        String msg =  JSON.toJSONString(monitorResult);
        sendMessageToAll(msg);
    }


    /**
     * 获取员工总数
     */
    public void getPersonNum()
    {
        int employeeNum  =  srv.getAllEmployeeNum();
        int yearVisitorNum  =  srv.getThisYearVisitorNum();
        HashMap<String,Object> map = new HashMap();
        map.put("employeeNum",employeeNum);
        map.put("yearVisitorNum",yearVisitorNum);

        MonitorResult monitorResult = new MonitorResult();
        monitorResult.setData(map);
        monitorResult.setSucc(true);
        monitorResult.setType(MonitorType.GET_PERSONNUM);
        String msg =  JSON.toJSONString(monitorResult);
        sendMessageToAll(msg);


    }

    /**
     * 今日访客统计
     */
    public void getVisitorStatToday() {
        int visitorNum = srv.getVisitorNumToday();
        int passNum = srv.getPassVisitorNumToday();
        int inNum = srv.getInVisitorNumToday();
        HashMap<String, Object> map = new HashMap<>();
        map.put("visitorNum", visitorNum);
        map.put("passNum", passNum);
        map.put("inNum", inNum);
        MonitorResult monitorResult = new MonitorResult();
        monitorResult.setData(map);
        monitorResult.setSucc(true);
        monitorResult.setType(MonitorType.GET_VISITORSTATTODAY);
        String msg =  JSON.toJSONString(monitorResult);
        sendMessageToAll(msg);

    }

    /**
     * 一周访客量
     */
    public void getVisitorNumWeek() {

        String yz_time = getTimeInterval(new Date());//获取本周时间
        String array[] = yz_time.split(",");
        String start_time = array[0];//本周第一天
        String end_time = array[1];  //本周最后一天
        List<Record> list =  srv.findVisitorStatByTime(start_time,end_time);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat resultSdf = new SimpleDateFormat("MM.dd");
        Date dBegin = null;
        Date dEnd = null;
        try {
            dBegin = sdf.parse(start_time);
            dEnd = sdf.parse(end_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //获取这周所有date
        List<Date> lDate = findDates(dBegin, dEnd);
        List<HashMap<String,Object>> resultList = new ArrayList<>();

        for(Date date : lDate)
        {
            int num = 0;
            HashMap<String ,Object> map = new HashMap<>();
            String strDate = sdf.format(date);
            String resultDate = resultSdf.format(date);
            map.put("visitDate",resultDate);

            for(Record record : list) {
                String visitDate = record.get("visitDate");
                if (strDate.equals(visitDate)) {
                    num = record.getInt("num");
                }
            }
            map.put("num",num);
            resultList.add(map);
        }


        MonitorResult monitorResult = new MonitorResult();
        monitorResult.setData(resultList);
        monitorResult.setSucc(true);
        monitorResult.setType(MonitorType.GET_VISITORNUMWEEK);
        String msg =  JSON.toJSONString(monitorResult);
        sendMessageToAll(msg);
    }


    /**
     * 今日入园统计
     */
    public void getInStatTodayByHour() {
        List<Record> list =  srv.getInStatTodayByHour();
        List<Record> resultList = new ArrayList<>();
        int maxHour = 24;
        for(int i =0 ;i<maxHour;i++)
        {
            String strHour = String.format("%02d",i);
            Record map = new Record();
            int total = 0;
            int staffNum =0;
            int visitorNum = 0;
            for(Record record : list)
            {
                String hour = record.get("hour");
                String userType = record.get("userType");
                int num =record.getInt("num");
                if(hour.equals(strHour))
                {
                    if(PersonType.STAFF.equals(userType)) {
                        staffNum = num;
                        total+=num;
                    }else{
                        visitorNum = num;
                        total+=num;
                    }
                }
            }
            map.set("hour",i);
            map.set("total",total);
            map.set("staffNum",staffNum);
            map.set("visitorNum",visitorNum);
            resultList.add(map);
        }
        MonitorResult monitorResult = new MonitorResult();
        monitorResult.setData(ApiDataUtils.apiList(resultList));
        monitorResult.setSucc(true);
        monitorResult.setType(MonitorType.GET_INSTATTODAYBYHOUR);
        String msg =  JSON.toJSONString(monitorResult);
        sendMessageToAll(msg);
    }




    public void getDeviceStatus()
    {
        List<Record> list =  srv.getDevices();
        List<HashMap<String,Object>> resultList = new ArrayList<>();

        for(Record record : list)
        {
            String type = record.getStr("type");
            //判断如果是上位机，那么遍历查找所有的子设备
            if(!"SWJ".equals(type)) {
                List<HashMap<String,Object>> childrenList = new ArrayList<>();
                HashMap<String,Object> map = new HashMap<>();
                String gate = record.getStr("gate");

                boolean hasChildren =false;
                for(HashMap<String,Object> item : resultList){
                    String itemGate = item.get("name").toString();
                    if(itemGate!=null && itemGate.equals(gate))
                    {
                        childrenList =  (ArrayList)item.get("children");
                        hasChildren =true;
                        break;
                    }
                }

                String name = record.getStr("device_name");
                String status = record.getStr("status");
                HashMap<String,Object> childrenMap = new HashMap<>();
                childrenMap.put("name",name);
                childrenMap.put("status","normal".equals(status)?0:1);
                childrenList.add(childrenMap);
                if(!hasChildren)
                {
                    map.put("name",gate);
                    map.put("children",childrenList);
                    resultList.add(map);
                }
            }
        }
        MonitorResult monitorResult = new MonitorResult();
        monitorResult.setData(resultList);
        monitorResult.setSucc(true);
        monitorResult.setType(MonitorType.GET_DEVICE_STATUS);
        String msg =  JSON.toJSONString(monitorResult);
        sendMessageToAll(msg);
    }



}