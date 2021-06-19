package com.xiaosong.common.web.inOut;


import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.LinkedList;
import java.util.List;

public class InOutService {

    public static final InOutService me = new InOutService();

    public Page<Record> findList(String userName,String userType,String deptName,String startTime, String endTime ,String inOrOut,int currentPage, int pageSize){

        StringBuilder sql = new StringBuilder();
        List<Object> objects = new LinkedList<>();
        sql.append(" from v_d_inout a left join v_dept_user b on a.idCard = b.idNO and a.userName = b.realName left join v_dept d on b.deptId = d.id where 1=1 ");
        if(userName!=null){
            sql.append(" and a.userName like concat('%',?,'%')");
            objects.add(userName);
        }
        if(userType != null){
            sql.append(" and a.userType = ?");
            objects.add(userType);
        }

        if(deptName != null){
            sql.append(" and d.dept_name like concat('%',?,'%')");
            objects.add(deptName);
        }

        if(startTime != null && endTime != null){

            sql.append(" and concat( a.scanDate,' ',a.scanTime) between ? and ?");
            objects.add(startTime.replace("+"," "));
            objects.add(endTime.replace("+"," "));
        }

        if(inOrOut != null){
            sql.append(" and a.inOrOut = ?");
            objects.add(inOrOut);
        }

        sql.append(" order by a.id desc");
        return Db.paginate(currentPage, pageSize, "select deviceIp,scanDate,scanTime,a.userName,b.phone,(case a.userType when 'staff' then '员工' when 'visitor' then '访客' end) userType,idNO,d.dept_name deptName,(case inOrOut when 'in' then '进' when 'out' then '出' end ) inOrOut,(case a.deviceType when 'FACE' then '人脸通行' when 'RFID' then '刷卡通行' when 'QRCODE' then '二维码通行' end) deviceType ", sql.toString(),objects.toArray());
    }

    public List<Record> downReport(String userName,String userType,String deptName,String startTime, String endTime,String inOrOut){
        StringBuilder sql = new StringBuilder();
        List<Object> objects = new LinkedList<>();
        sql.append("select scanDate,scanTime,a.userName,b.phone,(case a.userType when 'staff' then '员工' when 'visitor' then '访客' end) userType,idNO,d.dept_name deptName,(case inOrOut when 'in' then '进' when 'out' then '出' end ) inOrOut,(case a.deviceType when 'FACE' then '人脸通行' when 'RFID' then '刷卡通行' when 'QRCODE' then '二维码通行' end) deviceType from v_d_inout a left join v_dept_user b on a.idCard = b.idNO and a.userName = b.realName left join v_dept d on b.deptId = d.id where 1=1 ");
        if(userName!=null){
            sql.append(" and a.userName like concat('%',?,'%')");
            objects.add(userName);
        }
        if(userType != null){
            sql.append(" and a.userType = ?");
            objects.add(userType);
        }

        if(deptName != null){
            sql.append(" and d.dept_name like concat('%',?,'%')");
            objects.add(deptName);
        }
        if(startTime != null && endTime != null){
            sql.append(" and  concat( a.scanDate,' ',a.scanTime) between ? and ?");
            objects.add(startTime);
            objects.add(endTime);
        }

        if(inOrOut != null){
            sql.append(" and a.inOrOut = ?");
            objects.add(inOrOut);
        }
        sql.append(" order by a.id desc");
        return Db.find(sql.toString(),objects.toArray());
    }

}
