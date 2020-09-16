package com.xiaosong.common.web.monitor;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import java.util.Date;
import java.util.List;
import static com.xiaosong.util.DateUtil.getTimeInterval;

/**
 * Created by CNL on 2020/8/31.
 */
public class MonitorCenterService {

    public static final	MonitorCenterService me = new MonitorCenterService();
    /**
     * 今日通行情况
     */
    public List<Record> getPassToday()
    {
       // String sql = "select sum(a.num) num,a.userType,b.gate from (select count(1) num,userType,deviceIp from  (select * from  v_d_inout where scanDate = curdate() ) a group by deviceIp,userType ) a LEFT JOIN v_device b on a.deviceIp = b.ip  group by b.gate,a.userType";
        String sql ="select count(1) num,userType,org_name gate from  (select * from  v_d_inout where scanDate = curdate() ) a  left join v_org on orgCode = org_code group by orgCode,userType";
        List<Record> list = Db.find(sql);
        return list;
    }


    /**
     * 获取员工总数
     */
    public int getAllEmployeeNum()
    {
        int result = 0;
        Record record = Db.findFirst("select count(1) num from v_dept_user where currentStatus!='deleted'");
        if(record!=null)
        {
            result = record.getInt("num");
        }
        return result;
    }

    /**
     * 获取年访客总数
     */
    public int getThisYearVisitorNum()
    {
        int result = 0;
        Record record = Db.findFirst("select count(1) num from v_visitor_record where cstatus='applySuccess' and visitDate >= CONCAT(YEAR(NOW()),'-','01-01') and  visitDate <= CONCAT(YEAR(NOW()),'-','12-31')");
        if(record!=null)
        {
            result = record.getInt("num");
        }
        return result;
    }


    /**
     * 获取今日访客申请总数
     */
    public int getVisitorNumToday()
    {
        int result = 0;
        Record record = Db.findFirst("select count(1) as visitorNum from v_visitor_record where visitDate =  curdate()");
        if(record!=null)
        {
            result = record.getInt("visitorNum");
        }
        return result;
    }


    /**
     * 获取今日访客申请总数
     */
    public int getPassVisitorNumToday()
    {
        int result = 0;
        Record record = Db.findFirst("select count(1) as passNum from v_visitor_record where cstatus ='applySuccess' and visitDate =  curdate()");
        if(record!=null)
        {
            result = record.getInt("passNum");
        }
        return result;
    }


    /**
     * 获取今日入园人数总数
     */
    public int getInVisitorNumToday()
    {
        int result = 0;
        Record record = Db.findFirst("  select count(1) inNum from v_visitor_record a\n" +
                "    LEFT JOIN v_dept_user b on b.id = a.visitorId\n" +
                "    JOIN v_d_inout c on b.idNO = c.idCard and c.inOrOut='in' and a.visitDate = c.scanDate\n" +
                "    and  CONCAT(c.scanDate,' ',c.scanTime)>= a.startDate and  CONCAT(c.scanDate,' ',c.scanTime)<= a.endDate and a.visitDate =  curdate() and b.currentStatus!='deleted'");
        if(record!=null)
        {
            result = record.getInt("inNum");
        }
        return result;
    }



    public List<Record> findVisitorStatByTime(String startTime,String endTime) {
        String sql = "select count(1) as num ,visitDate from v_visitor_record where visitDate>=? and visitDate<=? GROUP BY visitDate";
        List<Record> result = Db.find(sql,startTime,endTime);
        return result;
    }


    public List<Record> getInStatTodayByHour()
    {
        String sql = "select count(1) num ,left(scanTime,2) as hour,userType from v_d_inout where scanDate = curdate() group BY hour,userType";
        List<Record> result = Db.find(sql);
        return result;
    }

    public List<Record> getDevices()
    {
        String sql = "select a.device_name,ip,type,a.status,org_code,org_name gate from v_device a join v_org b on gate = org_code";
        List<Record> result = Db.find(sql);
        return result;
    }

}