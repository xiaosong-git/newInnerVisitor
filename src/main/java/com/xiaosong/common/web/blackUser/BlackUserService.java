package com.xiaosong.common.web.blackUser;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.model.VBlackUser;

import java.util.LinkedList;
import java.util.List;

public class BlackUserService {
    public static final BlackUserService me = new BlackUserService();

    public static final VBlackUser dao = VBlackUser.dao;

    public Page<Record> findList(int currentPage, int pageSize , String realName, String idCard, String level){
        StringBuilder sql = new StringBuilder();
        List<Object> params = new LinkedList<>();
        sql.append("from v_black_user where 1=1");
        if(realName != null){
            sql.append(" and realName like CONCAT('%',?,'%') ");
            params.add(realName);
        }
        if(idCard != null){
            sql.append(" and idCard = ?");
            params.add(idCard);
        }
        if(level != null){
            sql.append(" and level = ?");
            params.add(level);
        }
        return Db.paginate(currentPage,pageSize,"select * ",sql.toString(),params.toArray());
    }

    public VBlackUser findBalckUser(String name,String idNO){
        return dao.findFirst("select * from v_black_user where realName = ? and idNO = ?",name,idNO);
    }
}
