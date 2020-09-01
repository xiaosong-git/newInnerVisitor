package com.xiaosong.common.api.base;

import com.jfinal.plugin.activerecord.Record;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @description: 基础服务
 * @author: cwf
 * @create: 2020-01-11 11:00
 **/
public class MyBaseService {
    //转换为Api的listMap
    public List<Map<String,Object>> apiList(List<Record> records){
        List<Map<String, Object> > apiRecords=new LinkedList<>();
        for (Record record : records) {
            record.getColumns().forEach((s, o) ->{
                if (o==null) {
                        record.getColumns().put(s, "");
                }
            } );
            apiRecords.add(record.getColumns());
        }
        return apiRecords;
    }


    public Map<String,Object> apiMap(Record record){
           if(record!=null) {
               record.getColumns().forEach((s, o) -> {
                   if (o == null) {
                       record.getColumns().put(s, "");
                   }
               });
               return record.getColumns();
           }else{
               return null;
           }

    }
}
