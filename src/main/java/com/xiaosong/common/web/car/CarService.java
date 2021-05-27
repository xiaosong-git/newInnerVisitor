package com.xiaosong.common.web.car;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.LinkedList;
import java.util.List;

public class CarService {

    public static final CarService me = new CarService();

    public Page<Record> findList(String userName, String visitName, String plate, String startTime, String endTime, String visitDept, int pageNum, int pageSize) {
        StringBuilder sql = new StringBuilder();

        List<Object> objects = new LinkedList<>();

        sql.append("from v_car c   left join v_dept_user ru on ru.id = c.replyUserId left join v_dept_user u on u.id = c.approvalUserId left join v_dept_user vu on vu.id =c.visitId where c.id is not null ");

        if (userName != null) {
            sql.append("and userName like concat('%',?,'%') ");
            objects.add(userName);
        }

        if (visitName != null) {
            sql.append("and visitName like concat('%',?,'%') ");
            objects.add(visitName);
        }


        if (plate != null) {
            sql.append("and plate like concat('%',?,'%') ");
            objects.add(plate);
        }

        if (startTime != null && endTime != null) {
            sql.append("and visitTime between ? and ? ");
            objects.add(startTime);
            objects.add(endTime);
        }

        if (visitDept != null) {
            sql.append("and visitDept like concat('%',?,'%') ");
            objects.add(visitDept);
        }

        sql.append(" order by c.id desc");

        return Db.paginate(pageNum,pageSize,"select c.entourages,vu.phone,userName,visitName,plate,c.num,c.idNO,u.realName,ru.realName replyUser,concat(replyDate,' ',replyTime) replayDate,(case cStatus when 'applyConfirm' then '申请中' when 'applySuccess' then '接受访问' when 'applyFail' then '拒绝访问' when 'applyPass' then '已放行' end)cStatus,visitDept ",sql.toString(),objects.toArray());
    }

    public List<Record> downReport(String userName, String visitName, String plate, String startTime, String endTime, String visitDept) {
        StringBuilder sql = new StringBuilder();

        List<Object> objects = new LinkedList<>();

        sql.append("select  c.entourages,vu.phone,userName,visitName,plate,ifnull(c.num,0) num,c.idNO,u.realName,ru.realName replyUser,concat(replyDate,' ',replyTime) replayDate,(case cStatus when 'applyConfirm' then '申请中' when 'applySuccess' then '接受访问' when 'applyFail' then '拒绝访问'  when 'applyPass' then '已放行' end)cStatus,visitDept from v_car c left join v_dept_user ru on ru.id = c.replyUserId left join v_dept_user u on u.id = c.approvalUserId   left join v_dept_user vu on vu.id = c.visitId where c.id is not null ");

        if (userName != null) {
            sql.append("and userName like concat('%',?,'%') ");
            objects.add(userName);
        }

        if (visitName != null) {
            sql.append("and visitName like concat('%',?,'%') ");
            objects.add(visitName);
        }


        if (plate != null) {
            sql.append("and plate like concat('%',?,'%') ");
            objects.add(plate);
        }

        if (startTime != null && endTime != null) {
            sql.append("and visitTime between ? and ? ");
            objects.add(startTime);
            objects.add(endTime);
        }

        if (visitDept != null) {
            sql.append("and visitDept like concat('%',?,'%') ");
            objects.add(visitDept);
        }

        sql.append(" order by c.id desc");

        return Db.find(sql.toString(),objects.toArray());
    }
}
