package com.xiaosong.common.web.visitor;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 作者 : xiaojf
 * @Date 创建时间：2020年1月18日 上午10:53:15
 * 类说明
 */
public class VisitorService {

	public static final	VisitorService me = new VisitorService();

	public Page<Record> findList(String userName, String visitName, String startTime, String endTime, String visitDept, int pageNum, int pageSize){
		StringBuilder sql = new StringBuilder();

		List<Object> objects = new LinkedList<>();

		sql.append("from v_visitor_record  v left join v_dept_user  du1 on v.userId = du1.id LEFT JOIN v_dept_user du2 on v.visitorId =du2.id left join v_dept_user du3 on v.replyUserId = du3.id " +
				"left join (select a.userName,CONCAT(a.scanDate,' ',a.inTime) inTime,a.scanDate,a.idCard,id.extra2 inGate,CONCAT(a.scanDate,' ',b.outTime) outTime,od.extra2 outGate from (select id,userName,scanDate,inOrOut,idCard,min(scanTime) inTime,deviceIp from v_d_inout where inOrOut='in' GROUP BY idCard,scanDate,inOrOut)a left join (select id,userName,scanDate,inOrOut,idCard,max(scanTime) outTime,deviceIp from v_d_inout where inOrOut='out' GROUP BY idCard,scanDate,inOrOut) b on a.scanDate=b.scanDate and a.idCard=b.idCard " +
				" left join v_device id on id.ip=a.deviceIp left join v_device od on od.ip=b.deviceIp  " +
				" ) c on du1.idNO=c.idCard and c.scanDate=v.visitDate where v.id is not null ");

		if (userName != null) {
			sql.append("and du1.realName like concat('%',?,'%') ");
			objects.add(userName);
		}

		if (visitName != null) {
			sql.append("and du2.realName like concat('%',?,'%') ");
			objects.add(visitName);
		}

		if (startTime != null && endTime != null) {
			sql.append("and v.startDate between ? and ? ");
			objects.add(startTime);
			objects.add(endTime);
		}

		if (visitDept != null) {
			sql.append("and v.visitDept like concat('%',?,'%') ");
			objects.add(visitDept);
		}

		sql.append(" order by v.id desc");

		return Db.paginate(pageNum,pageSize,"select du1.realName userName,du2.realName visitName, v.visitDept,concat(v.visitDate,' ',v.visitTime) applyTime,v.startDate visitTime,(case cstatus when 'applyConfirm' then '申请中' when 'applySuccess' then '接受访问' when 'applyFail' then '拒绝访问' end) cstatus,du3.realName replyName,concat(v.replyDate,' ',v.replyTime) replyTime,c.inTime,c.inGate,c.outTime,c.outGate ",sql.toString(),objects.toArray());
	}



	public List<Record> downReport(String userName, String visitName, String startTime, String endTime, String visitDept) {
		StringBuilder sql = new StringBuilder();

		List<Object> objects = new LinkedList<>();

		sql.append("select du1.realName userName,du2.realName visitName, v.visitDept,concat(v.visitDate,' ',v.visitTime) applyTime,v.startDate visitTime,(case cstatus when 'applyConfirm' then '申请中' when 'applySuccess' then '接受访问' when 'applyFail' then '拒绝访问' end) cstatus,du3.realName replyName,concat(v.replyDate,' ',v.replyTime) replyTime,c.inTime,c.inGate,c.outTime,c.outGate " +
		"from v_visitor_record  v left join v_dept_user  du1 on v.userId = du1.id LEFT JOIN v_dept_user du2 on v.visitorId =du2.id left join v_dept_user du3 on v.replyUserId = du3.id " +
		"left join (select a.userName,CONCAT(a.scanDate,' ',a.inTime) inTime,a.scanDate,a.idCard,id.extra2 inGate,CONCAT(a.scanDate,' ',b.outTime) outTime,od.extra2 outGate from (select id,userName,scanDate,inOrOut,idCard,min(scanTime) inTime,deviceIp from v_d_inout where inOrOut='in' GROUP BY idCard,scanDate,inOrOut)a left join (select id,userName,scanDate,inOrOut,idCard,max(scanTime) outTime,deviceIp from v_d_inout where inOrOut='out' GROUP BY idCard,scanDate,inOrOut) b on a.scanDate=b.scanDate and a.idCard=b.idCard " +
		" left join v_device id on id.ip=a.deviceIp left join v_device od on od.ip=b.deviceIp  " +
		" ) c on du1.idNO=c.idCard and c.scanDate=v.visitDate where v.id is not null ");

		if (userName != null) {
			sql.append("and du1.realName like concat('%',?,'%') ");
			objects.add(userName);
		}

		if (visitName != null) {
			sql.append("and du2.realName like concat('%',?,'%') ");
			objects.add(visitName);
		}

		if (startTime != null && endTime != null) {
			sql.append("and v.startDate between ? and ? ");
			objects.add(startTime);
			objects.add(endTime);
		}

		if (visitDept != null) {
			sql.append("and v.visitDept like concat('%',?,'%') ");
			objects.add(visitDept);
		}

		sql.append(" order by v.id desc");

		return Db.find(sql.toString(),objects.toArray());
	}

}
