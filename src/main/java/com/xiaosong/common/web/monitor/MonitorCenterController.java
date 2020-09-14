package com.xiaosong.common.web.monitor;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.constant.PersonType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.xiaosong.util.DateUtil.findDates;
import static com.xiaosong.util.DateUtil.getTimeInterval;

/**
 * Created by CNL on 2020/8/28.
 */
public class MonitorCenterController extends Controller {

    private Log log = Log.getLog(MonitorCenterController.class);
    public MonitorCenterService srv = MonitorCenterService.me;
    /**
     * 今日通行情况
     */
    public void getPassToday()
    {
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
        resultMap.put("data",resultList);
        renderJson(resultMap);
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
        renderJson(map);

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
        renderJson(map);
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
        renderJson(resultList);
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
        renderJson(resultList);
    }

}
