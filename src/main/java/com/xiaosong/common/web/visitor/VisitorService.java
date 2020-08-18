package com.xiaosong.common.web.visitor;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.template.expr.ast.Array;
import com.xiaosong.common.web.vipUser.VipUserService;
import com.xiaosong.model.VCompVipUser;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月18日 上午10:53:15 
* 类说明 
*/
public class VisitorService {
public static final	VisitorService me = new VisitorService();
	
	public Page<Record> findList(String realName, String visitDate,int currentPage, int pageSize){
        StringBuilder strWhere =new StringBuilder("where 1=1");
		List<Object> params = new ArrayList();
		String sqls = "select * from (select visitDateTime, b.id,b.userName,b.userPhone,b.startDate,b.endDate,b.visitorName,b.visitorPhone,max(b.inTime) as inTime,MAX(b.outTime) as outTime \n" +
				"from\n" +
				"(select a.*,case when iot.inOrOut ='in' then min(CONCAT(iot.scanDate,' ',iot.scanTime)) end as inTime ,\n" +
				"case when  iot.inOrOut ='out' then max(CONCAT(iot.scanDate,' ',iot.scanTime)) end as outTime FROM \n" +
				"(select vi.*,u.realName as userName,u.phone as userPhone,du.realName as visitorName,du.phone as visitorPhone,CONCAT(vi.visitDate,' ',vi.visitTime) as visitDateTime  from v_visitor_record vi left join v_dept_user u on vi.userId=u.id \n" +
				"LEFT JOIN v_dept_user du on vi.visitorId=du.id WHERE vi.userType='in' and vi.visitorType='in' \n" +
				"UNION\n" +
				"select vi.*,u.realName as userName,u.phone as userPhone,du.realName as visitorName,du.phone as visitorPhone ,CONCAT(vi.visitDate,' ',vi.visitTime) as visitDateTime from v_visitor_record vi left join v_out_visitor u on vi.userId=u.id \n" +
				"LEFT JOIN v_dept_user du on vi.visitorId=du.id WHERE vi.userType='out' and vi.visitorType='in' ) AS a\n" +
				"LEFT JOIN v_d_inout iot on a.userName=iot.userName and DATE_ADD(STR_TO_DATE(a.startDate,'%Y-%m-%d %H:%i:%s'),INTERVAL -30 minute)<=STR_TO_DATE(CONCAT(iot.scanDate,' ',iot.scanTime),'%Y-%m-%d %H:%i:%s') \n" +
				"and DATE_ADD(STR_TO_DATE(a.endDate,'%Y-%m-%d %H:%i:%s'),INTERVAL 30 minute)>=STR_TO_DATE(CONCAT(iot.scanDate,' ',iot.scanTime),'%Y-%m-%d %H:%i:%s') \n" +
				"GROUP BY iot.inOrOut\n" +
				") as b \n" ;

				if(realName!=null &&realName!="") {
					params.add(realName);
					strWhere.append(" and b.userName like CONCAT('%',?,'%') ");
				}

				if(!StringUtils.isBlank(visitDate))
				{
					params.add(visitDate);
					strWhere.append(" and b.visitDate =?");

				}

		return Db.paginate(currentPage, pageSize, "select *", "from ("+sqls+strWhere.toString()+") as d) as d",params.toArray());
	}
}
