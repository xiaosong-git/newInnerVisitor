package com.xiaosong.common.web.inOut;


import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.LinkedList;
import java.util.List;

public class InOutService {

    public static final InOutService me = new InOutService();

    public Page<Record> findList(String userName,String userType,String inOrOut,String startDate, String endDate ,int currentPage, int pageSize){

        StringBuilder sql = new StringBuilder();
        List<Object> objects = new LinkedList<>();
        sql.append("from v_d_inout where 1=1");
        if(userName!=null){
            sql.append(" and userName = ?");
            objects.add(userName);
        }
        if(userType != null){
            sql.append(" and userType = ?");
            objects.add(userType);
        }

        if(inOrOut != null){
            sql.append(" and inOrOut = ?");
            objects.add(inOrOut);
        }
        if(startDate != null){
            sql.append(" and scanDate >= ?");
            objects.add(startDate);
        }
        if(endDate != null){
            sql.append(" and scanDate <= ?");
            objects.add(endDate);
        }
        return Db.paginate(currentPage, pageSize, "select *", sql.toString(),objects.toArray());
    }
}
