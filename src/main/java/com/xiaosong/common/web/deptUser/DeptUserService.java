package com.xiaosong.common.web.deptUser;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.util.DESUtil;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月14日 下午8:27:19 
* 类说明 
*/
public class DeptUserService {
	public static final	DeptUserService me = new DeptUserService();
	
	public Page<Record> findList(String realName, int currentPage, int pageSize){
		if(realName!=null &&realName!="") {
			return Db.paginate(currentPage, pageSize, "select *", "from (select u.*,d.dept_name from v_dept_user u left join v_dept d on u.deptId=d.id where u.realName like CONCAT('%',?,'%')) as a",realName);
			//return VDeptUser.dao.paginate(currentPage, pageSize, "select *", "from v_dept_user where tel like CONCAT(?,'%')",tel);
		}
		return Db.paginate(currentPage, pageSize, "select *", "from (select u.*,d.dept_name from v_dept_user u left join v_dept d on u.deptId=d.id) as a");
	}
	
	public boolean addDeptUser(VDeptUser config) {
		Record record = Db.findFirst("select * from v_user_key");
		String idNo = DESUtil.encode(record.getStr("workKey"), config.getIdNO());
		config.setIdNO(idNo);
		return config.save();
	}
	
	public boolean editDeptUser(VDeptUser config) {
		return config.update();
	}
	
	public boolean deleteDeptUser(Long id) {
		return VDeptUser.dao.deleteById(id);
	}
}
