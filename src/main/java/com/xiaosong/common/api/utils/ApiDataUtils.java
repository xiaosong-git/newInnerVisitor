package com.xiaosong.common.api.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gexin.fastjson.JSONArray;
import com.jfinal.kit.HttpKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.util.DESUtil;
import com.xiaosong.util.HttpClientUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by CNL on 2020/9/9.
 */
public class ApiDataUtils {

    //转换为Api的listMap
    public static List<Map<String,Object>> apiList(List<Record> records){
        List<Map<String, Object> > apiRecords=new LinkedList<>();
        for (Record record : records) {
            record.getColumns().forEach((s, o) ->{
                if (o==null) {
                    record.getColumns().put(s, "");
                }
            });
            apiRecords.add(record.getColumns());
        }
        return apiRecords;
    }



    public  static void main(String []args)
    {

        String idNo = DESUtil.encode("iB4drRzSrC", "321281199406082903");
        System.out.println(idNo);

//        String url = "http://127.0.0.1:8081/visitor/inout/save";
///*
//        JSONArray jsonArray = new JSONArray();
//        for(int i=0;i<10;i++) {
//            HashMap<String, String> params = new HashMap<>();
//            params.put("ip", "127.0.0.1");
//            params.put("deviceName", "11111");
//            params.put("type", "111");
//            params.put("gate", "2222");
//            params.put("status", "1");
//            params.put("avg", "1"+i);
//            jsonArray.add(params);
//        }
//        String result = HttpKit.post(url, JSON.toJSONString(jsonArray),null);*/
//
//        HashMap<String,Object> params = new HashMap<>();
//        params.put("deviceIp","192.168.1.1");
//        params.put("idCard","1111");
//        params.put("deviceType","2222");
//        params.put("inOrOut","out");
//        params.put("orgCode","222");
//        params.put("scanDate","2020-01-01");
//        params.put("scanTime","10:10:10");
//        params.put("userName","322");
//        params.put("userType","visitor");
//        String result = HttpKit.post(url, JSON.toJSONString(params),null);
//        JSONObject jsonResult = JSONObject.parseObject(result);
//        JSONObject jsonVerify =jsonResult.getJSONObject("verify");
//
//        if("success".equals(jsonVerify.getString("sign")))
//        {
//            System.out.println("success");
//        }
//
//        System.out.println(result);

    }

}
