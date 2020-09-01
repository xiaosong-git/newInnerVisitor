package com.xiaosong.common.web.monitor;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;

import java.util.HashMap;
import java.util.List;

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
        List<Record> list =  srv.getPassToday();
        renderJson(list);
    }


    /**
     * 获取员工总数
     */
    public void getAllEmployeeNum()
    {
        int num  =  srv.getAllEmployeeNum();
        renderText(String.valueOf(num));
    }


    /**
     * 获取今年访客总数
     */
    public void getThisYearVisitorNum()
    {
        int num  =  srv.getThisYearVisitorNum();
        renderText(String.valueOf(num));
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
        List<Record> list =  srv.findVisitorStatWeek();
        renderJson(list);
    }



    /**
     * 今日入园统计
     */
    public void getInStatTodayByHour() {
        List<Record> list =  srv.getInStatTodayByHour();
        renderJson(list);
    }








}
