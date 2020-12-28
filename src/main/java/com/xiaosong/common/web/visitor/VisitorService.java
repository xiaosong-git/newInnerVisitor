package com.xiaosong.common.web.visitor;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 作者 : xiaojf
 * @Date 创建时间：2020年1月18日 上午10:53:15
 * 类说明
 */
public class VisitorService {

	public static final	VisitorService me = new VisitorService();

	public Page<Record> findList(String realName, String visitorName,String stratTime,String endTime,String cStatus,int currentPage, int pageSize){
		StringBuilder strWhere =new StringBuilder("where 1=1");
		List<Object> params = new ArrayList();
/*		String sqls = "select * from (select case cstatus when 'applyConfirm' then '等待审核' when 'applySuccess' then '审核通过' when 'applyFail' then '拒绝访问' end as cstatusName,cstatus ,visitDateTime, b.id,b.userName,b.userPhone,b.startDate,b.endDate,CONCAT(DATE_FORMAT(b.startDate,'%H:%m'),'~',DATE_FORMAT(b.endDate,'%H:%m')) as visitTimePeriod,b.visitorName,b.visitorPhone,max(b.inTime) as inTime,MAX(b.outTime) as outTime \n" +
				"from\n" +
				"(select a.*,case when iot.inOrOut ='in' then min(CONCAT(iot.scanDate,' ',iot.scanTime)) end as inTime ,\n" +
				"case when  iot.inOrOut ='out' then max(CONCAT(iot.scanDate,' ',iot.scanTime)) end as outTime FROM \n" +
				"(select vi.*,u.realName as userName,u.phone as userPhone,du.realName as visitorName,du.phone as visitorPhone,CONCAT(vi.visitDate,' ',vi.visitTime) as visitDateTime  from v_visitor_record vi left join v_dept_user u on vi.userId=u.id \n" +
				//"LEFT JOIN v_dept_user du on vi.visitorId=du.id WHERE vi.userType='in' and vi.visitorType='in' \n" +
				"LEFT JOIN v_dept_user du on vi.visitorId=du.id  \n" +

				//区分了内外网还不知道怎么用暂时注释
				//"UNION\n" +
				//"select vi.*,u.realName as userName,u.phone as userPhone,du.realName as visitorName,du.phone as visitorPhone ,CONCAT(vi.visitDate,' ',vi.visitTime) as visitDateTime from v_visitor_record vi left join v_out_visitor u on vi.userId=u.id \n" +
				//"LEFT JOIN v_dept_user du on vi.visitorId=du.id WHERE vi.userType='out' and vi.visitorType='in'"+
				") AS a\n" +
				"LEFT JOIN v_d_inout iot on a.userName=iot.userName and DATE_ADD(STR_TO_DATE(a.startDate,'%Y-%m-%d %H:%i:%s'),INTERVAL -30 minute)<=STR_TO_DATE(CONCAT(iot.scanDate,' ',iot.scanTime),'%Y-%m-%d %H:%i:%s') \n" +
				"and DATE_ADD(STR_TO_DATE(a.endDate,'%Y-%m-%d %H:%i:%s'),INTERVAL 30 minute)>=STR_TO_DATE(CONCAT(iot.scanDate,' ',iot.scanTime),'%Y-%m-%d %H:%i:%s') \n" +
				"GROUP BY iot.inOrOut\n" +
				") as b \n" ;*/
/*
		String sqls ="select * from (select case cstatus when 'applyConfirm' then '等待审核' when 'applySuccess' then '审核通过' when 'applyFail' then '拒绝访问' end as cstatusName,cstatus ,visitDateTime, b.id,b.userName,b.userPhone,b.startDate,b.endDate,CONCAT(DATE_FORMAT(b.startDate,'%H:%m'),'~',DATE_FORMAT(b.endDate,'%H:%m')) as visitTimePeriod,b.visitorName,b.visitorPhone,b.inTime as inTime,b.outTime as outTime \n" +
				"from\n" +
				"(select a.*,case when iot.inOrOut ='in' then CONCAT(iot.scanDate,' ',iot.scanTime) end as inTime ,\n" +
				"case when  iot.inOrOut ='out' then CONCAT(iot.scanDate,' ',iot.scanTime) end as outTime FROM \n" +
				"(select vi.*,u.realName as userName,u.phone as userPhone,du.realName as visitorName,du.phone as visitorPhone,CONCAT(vi.visitDate,' ',vi.visitTime) as visitDateTime  from v_visitor_record vi left join v_dept_user u on vi.userId=u.id \n" +
				"LEFT JOIN v_dept_user du on vi.visitorId=du.id  \n" +
				") AS a\n" +
				"LEFT JOIN v_d_inout iot on a.userName=iot.userName\n" +
				"\n" +
				"\n" +
				") as b " ;

*/
         String sqls ="  select su.true_name,su.username, du1.idNO,case cstatus when 'applyConfirm' then '等待审核' when 'applySuccess' then '审核通过' when 'applyFail' then '拒绝访问' end as cstatusName,cstatus ,CONCAT(visitDate,\" \",visitTime) visitDateTime, du1.realName userName,du1.phone userPhone,startDate,endDate,CONCAT(DATE_FORMAT(startDate,'%H:%m'),'~',DATE_FORMAT(endDate,'%H:%m')) as visitTimePeriod,du2.realName visitorName,du2.phone visitorPhone   from v_visitor_record  v left join v_dept_user  du1 on v.userId = du1.id LEFT JOIN v_dept_user du2 on v.visitorId =du2.id left join v_sys_user su on v.createUser = su.id";

		if(realName!=null &&realName!="") {
			params.add(realName);
			strWhere.append(" and du1.realName like CONCAT('%',?,'%') ");
		}

		if(!StringUtils.isBlank(visitorName)) {
			params.add(visitorName);
			strWhere.append(" and du2.realName like CONCAT('%',?,'%') ");
		}

		if(!StringUtils.isBlank(stratTime))
		{
			params.add(stratTime);
			strWhere.append(" and CONCAT(visitDate,' ',visitTime) >=?");
		}

		if(!StringUtils.isBlank(endTime))
		{
			params.add(endTime);
			strWhere.append(" and CONCAT(visitDate,' ',visitTime) <=?");
		}

		if(!StringUtils.isBlank(cStatus))
		{
			params.add(cStatus);
			strWhere.append(" and  cstatus =?");
		}
		Page<Record> pageList = Db.paginate(currentPage, pageSize, "select *", "from ("+sqls+strWhere.toString()+" order by v.id desc ) as d",params.toArray());

		List<Record> list = pageList.getList();

		if(list!=null && list.size()>0) {
			String strStartTime ="";
			String strEndTime ="";
			List<String> idNOList = new ArrayList<>();
			for (Record record : list) {
				String idNO = record.getStr("idNO");
				if(StringUtils.isBlank(idNO))
				{
					continue;
				}
				if (!idNOList.contains(idNO)) {
					idNOList.add(idNO);
				}
				String startDate = record.getStr("startDate");
				String endDate = record.getStr("endDate");
				if (StringUtils.isBlank(strStartTime) || strStartTime.compareTo(startDate) > 0) {
					strStartTime = startDate;
				}
				if (StringUtils.isBlank(endDate) || strEndTime.compareTo(endDate) < 0) {
					strEndTime = endDate;
				}

			}

			strStartTime = strStartTime.substring(0, 10);
			strEndTime = strEndTime.substring(0, 10);

		  	String strIdNOs =String.join("','",idNOList);
			String sql = "select * from v_d_inout i where i.idCard in ('"+strIdNOs+"')  and  scanDate >=? and scanDate<=?  ";
			List<Record> inoutList = Db.find(sql,strStartTime,strEndTime);



			for (Record record : list) {

				String idNO = record.getStr("idNO");
				if(StringUtils.isBlank(idNO))
				{
					continue;
				}
				String startDate = record.getStr("startDate").substring(11,16);
				String endDate = record.getStr("endDate").substring(11,16);

				List<Record> list2 = inoutList.stream().filter(x->idNO.equals(x.getStr("idCard")) && x.getStr("scanTime").compareTo(startDate)>=0 && x.getStr("scanTime").compareTo(endDate)<=0).collect(Collectors.toList());

				System.out.println("idNO:"+list2.size());

				Record inTimeRecord =  list2.stream().filter(x->"in".equals(x.getStr("inOrOut"))).min(Comparator.comparing(i->i.getStr("scanTime"))).orElse(null);
				Record outTimeRecord =  list2.stream().filter(x->"out".equals(x.getStr("inOrOut"))).max(Comparator.comparing(i->i.getStr("scanTime"))).orElse(null);
				String inTime ="";
				String outTime ="";
				if(inTimeRecord!=null)
				{
					 inTime = inTimeRecord.getStr("scanTime");
				}
				record.set("inTime",inTime);

				if(outTimeRecord!=null)
				{
					outTime = outTimeRecord.getStr("scanTime");
				}
				record.set("outTime",outTime);
			}


		}
		return pageList;

	}



	public List<Record> findList(String realName, String visitorName,String stratTime,String endTime,String cStatus){
		StringBuilder strWhere =new StringBuilder("where 1=1");
		List<Object> params = new ArrayList();
/*		String sqls = "select * from (select case cstatus when 'applyConfirm' then '等待审核' when 'applySuccess' then '审核通过' when 'applyFail' then '拒绝访问' end as cstatusName,cstatus ,visitDateTime, b.id,b.userName,b.userPhone,b.startDate,b.endDate,CONCAT(DATE_FORMAT(b.startDate,'%H:%m'),'~',DATE_FORMAT(b.endDate,'%H:%m')) as visitTimePeriod,b.visitorName,b.visitorPhone,max(b.inTime) as inTime,MAX(b.outTime) as outTime \n" +
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
				") as b \n" ;*/

	/*	String sqls ="select * from (select case cstatus when 'applyConfirm' then '等待审核' when 'applySuccess' then '审核通过' when 'applyFail' then '拒绝访问' end as cstatusName,cstatus ,visitDateTime, b.id,b.userName,b.userPhone,b.startDate,b.endDate,CONCAT(DATE_FORMAT(b.startDate,'%H:%m'),'~',DATE_FORMAT(b.endDate,'%H:%m')) as visitTimePeriod,b.visitorName,b.visitorPhone,b.inTime as inTime,b.outTime as outTime \n" +
				"from\n" +
				"(select a.*,case when iot.inOrOut ='in' then CONCAT(iot.scanDate,' ',iot.scanTime) end as inTime ,\n" +
				"case when  iot.inOrOut ='out' then CONCAT(iot.scanDate,' ',iot.scanTime) end as outTime FROM \n" +
				"(select vi.*,u.realName as userName,u.phone as userPhone,du.realName as visitorName,du.phone as visitorPhone,CONCAT(vi.visitDate,' ',vi.visitTime) as visitDateTime  from v_visitor_record vi left join v_dept_user u on vi.userId=u.id \n" +
				"LEFT JOIN v_dept_user du on vi.visitorId=du.id  \n" +
				") AS a\n" +
				"LEFT JOIN v_d_inout iot on a.userName=iot.userName\n" +
				"\n" +
				"\n" +
				") as b ";


		if(realName!=null &&realName!="") {
			params.add(realName);
			strWhere.append(" and b.userName like CONCAT('%',?,'%') ");
		}

		if(!StringUtils.isBlank(visitorName)) {
			params.add(visitorName);
			strWhere.append(" and b.visitorName like CONCAT('%',?,'%') ");
		}

		if(!StringUtils.isBlank(stratTime))
		{
			params.add(stratTime);
			strWhere.append(" and CONCAT(b.visitDate,' ',b.visitTime) >=?");
		}

		if(!StringUtils.isBlank(endTime))
		{
			params.add(endTime);
			strWhere.append(" and CONCAT(b.visitDate,' ',b.visitTime) <=?");
		}

		if(!StringUtils.isBlank(cStatus))
		{
			params.add(cStatus);
			strWhere.append(" and  b.cstatus =?");
		}*/


		String sqls ="  select du1.idNO,case cstatus when 'applyConfirm' then '等待审核' when 'applySuccess' then '审核通过' when 'applyFail' then '拒绝访问' end as cstatusName,cstatus ,CONCAT(visitDate,\" \",visitTime) visitDateTime, du1.realName userName,du1.phone userPhone,startDate,endDate,CONCAT(DATE_FORMAT(startDate,'%H:%m'),'~',DATE_FORMAT(endDate,'%H:%m')) as visitTimePeriod,du2.realName visitorName,du2.phone visitorPhone   from v_visitor_record  v left join v_dept_user  du1 on v.userId = du1.id LEFT JOIN v_dept_user du2 on v.visitorId =du2.id ";

		if(realName!=null &&realName!="") {
			params.add(realName);
			strWhere.append(" and du1.realName like CONCAT('%',?,'%') ");
		}

		if(!StringUtils.isBlank(visitorName)) {
			params.add(visitorName);
			strWhere.append(" and du2.realName like CONCAT('%',?,'%') ");
		}

		if(!StringUtils.isBlank(stratTime))
		{
			params.add(stratTime);
			strWhere.append(" and CONCAT(visitDate,' ',visitTime) >=?");
		}

		if(!StringUtils.isBlank(endTime))
		{
			params.add(endTime);
			strWhere.append(" and CONCAT(visitDate,' ',visitTime) <=?");
		}

		if(!StringUtils.isBlank(cStatus))
		{
			params.add(cStatus);
			strWhere.append(" and  cstatus =?");
		}
		return Db.find( "select * from ("+sqls+strWhere.toString()+") as d",params.toArray());
	}

}
