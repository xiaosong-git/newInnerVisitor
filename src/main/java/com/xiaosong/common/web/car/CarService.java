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

        sql.append("from v_car c left join v_dept_user u on u.id = c.replyUserId where c.id is not null ");

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

        return Db.paginate(pageNum,pageSize,"select userName,visitName,plate,c.num,c.idNO,u.realName,concat(replyDate,' ',replyTime) replayDate,(case cStatus when 'applyConfirm' then '申请中' when 'applySuccess' then '接受访问' when 'applyFail' then '拒绝访问' end)cStatus,visitDept ",sql.toString(),objects.toArray());
    }

    public List<Record> downReport(String userName, String visitName, String plate, String startTime, String endTime, String visitDept) {
        StringBuilder sql = new StringBuilder();

        List<Object> objects = new LinkedList<>();

        sql.append("select userName,visitName,plate,ifnull(c.num,0) num,c.idNO,u.realName,concat(replyDate,' ',replyTime) replayDate,(case cStatus when 'applyConfirm' then '申请中' when 'applySuccess' then '接受访问' when 'applyFail' then '拒绝访问' end)cStatus,visitDept from v_car c left join v_dept_user u on u.id = c.replyUserId where c.id is not null ");

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
