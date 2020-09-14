package com.xiaosong.common.api.utils;

import com.jfinal.plugin.activerecord.Record;

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

}
