package com.xiaosong.common.web.deptUser;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.model.VDeptUser;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月14日 下午8:27:19 
* 类说明 
*/
public class DeptUserService {
	public static final	DeptUserService me = new DeptUserService();
	
	public Page<Record> findList(String tel, int currentPage, int pageSize){
		if(tel!=null &&tel!="") {
			return Db.paginate(currentPage, pageSize, "select *", "from (select u.*,d.dept_name from v_dept_user u left join v_dept d on u.sectionId=d.id where u.tel like CONCAT(?,'%')) as a",tel);
			//return VDeptUser.dao.paginate(currentPage, pageSize, "select *", "from v_dept_user where tel like CONCAT(?,'%')",tel);
		}
		return Db.paginate(currentPage, pageSize, "select *", "from (select u.*,d.dept_name from v_dept_user u left join v_dept d on u.sectionId=d.id) as a");
	}
	
	public boolean addDeptUser(VDeptUser config) {
		return config.save();
	}
	
	public boolean editDeptUser(VDeptUser config) {
		return config.update();
	}
	
	public boolean deleteDeptUser(Long id) {
		return VDeptUser.dao.deleteById(id);
	}
}
