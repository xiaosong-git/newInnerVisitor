package com.xiaosong.common.web.visitor;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.web.vipUser.VipUserService;
import com.xiaosong.model.VCompVipUser;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月18日 上午10:53:15 
* 类说明 
*/
public class VisitorService {
public static final	VisitorService me = new VisitorService();
	
	public Page<Record> findList(String realName, int currentPage, int pageSize){
		String sql = "select b.id,b.userName,b.userPhone,b.startDate,b.endDate,b.visitorName,b.visitorPhone,max(b.inTime) as inTime,MAX(b.outTime) as outTime \r\n" + 
				"from\r\n" + 
				"(select a.*,case when iot.inOrOut ='in' then min(CONCAT(iot.scanDate,' ',iot.scanTime)) end as inTime ,\r\n" + 
				"case when  iot.inOrOut ='out' then max(CONCAT(iot.scanDate,' ',iot.scanTime)) end as outTime FROM \r\n" + 
				"(select vi.*,u.realName as userName,u.phone as userPhone,du.realName as visitorName,du.phone as visitorPhone  from v_visitor_record vi left join v_dept_user u on vi.userId=u.id \r\n" + 
				"LEFT JOIN v_dept_user du on vi.visitorId=du.id WHERE vi.userType='in' and vi.visitorType='in' \r\n" + 
				"UNION\r\n" + 
				"select vi.*,u.realName as userName,u.phone as userPhone,du.realName as visitorName,du.phone as visitorPhone  from v_visitor_record vi left join v_out_visitor u on vi.userId=u.id \r\n" + 
				"LEFT JOIN v_dept_user du on vi.visitorId=du.id WHERE vi.userType='out' and vi.visitorType='in' ) AS a\r\n" + 
				"LEFT JOIN v_d_inout iot on a.userName=iot.userName and DATE_ADD(STR_TO_DATE(a.startDate,'%Y-%m-%d %H:%i:%s'),INTERVAL -30 minute)<=STR_TO_DATE(CONCAT(iot.scanDate,' ',iot.scanTime),'%Y-%m-%d %H:%i:%s') \r\n" + 
				"and DATE_ADD(STR_TO_DATE(a.endDate,'%Y-%m-%d %H:%i:%s'),INTERVAL 30 minute)>=STR_TO_DATE(CONCAT(iot.scanDate,' ',iot.scanTime),'%Y-%m-%d %H:%i:%s') \r\n" + 
				"GROUP BY iot.inOrOut\r\n" + 
				") as b \r\n" + 
				"where (b.userName like CONCAT('%',?,'%') or b.visitorName like CONCAT('%',?,'%')) \r\n"+
				"GROUP BY b.id";
		String sqls = "select b.id,b.userName,b.userPhone,b.startDate,b.endDate,b.visitorName,b.visitorPhone,max(b.inTime) as inTime,MAX(b.outTime) as outTime \r\n" + 
				"from\r\n" + 
				"(select a.*,case when iot.inOrOut ='in' then min(CONCAT(iot.scanDate,' ',iot.scanTime)) end as inTime ,\r\n" + 
				"case when  iot.inOrOut ='out' then max(CONCAT(iot.scanDate,' ',iot.scanTime)) end as outTime FROM \r\n" + 
				"(select vi.*,u.realName as userName,u.phone as userPhone,du.realName as visitorName,du.phone as visitorPhone  from v_visitor_record vi left join v_dept_user u on vi.userId=u.id \r\n" + 
				"LEFT JOIN v_dept_user du on vi.visitorId=du.id WHERE vi.userType='in' and vi.visitorType='in' \r\n" + 
				"UNION\r\n" + 
				"select vi.*,u.realName as userName,u.phone as userPhone,du.realName as visitorName,du.phone as visitorPhone  from v_visitor_record vi left join v_out_visitor u on vi.userId=u.id \r\n" + 
				"LEFT JOIN v_dept_user du on vi.visitorId=du.id WHERE vi.userType='out' and vi.visitorType='in' ) AS a\r\n" + 
				"LEFT JOIN v_d_inout iot on a.userName=iot.userName and DATE_ADD(STR_TO_DATE(a.startDate,'%Y-%m-%d %H:%i:%s'),INTERVAL -30 minute)<=STR_TO_DATE(CONCAT(iot.scanDate,' ',iot.scanTime),'%Y-%m-%d %H:%i:%s') \r\n" + 
				"and DATE_ADD(STR_TO_DATE(a.endDate,'%Y-%m-%d %H:%i:%s'),INTERVAL 30 minute)>=STR_TO_DATE(CONCAT(iot.scanDate,' ',iot.scanTime),'%Y-%m-%d %H:%i:%s') \r\n" + 
				"GROUP BY iot.inOrOut\r\n" + 
				") as b \r\n" + 
				"GROUP BY b.id";
		if(realName!=null &&realName!="") {
			return Db.paginate(currentPage, pageSize, "select *", "from ("+sql+") as d",realName,realName);
		}
		return Db.paginate(currentPage, pageSize, "select *", "from ("+sqls+") as d");
	}
}
