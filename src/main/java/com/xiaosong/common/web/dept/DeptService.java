package com.xiaosong.common.web.dept;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.model.VDept;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月14日 下午8:26:26 
* 类说明 
*/
public class DeptService {
	public static final	DeptService me = new DeptService();
	
	public Page<Record> findList(int currentPage,int pageSize){
		String sql = "SELECT d.*,o.org_name,du.realName from v_dept d left join v_org o on d.org_id=o.id\r\n" + 
					 "left join v_dept_user du on d.manage_user_id=du.id";
		return Db.paginate(currentPage, pageSize, "select *", "from ("+sql+") as a");
	}
	
	public List<VDept> findByOption(){
		return VDept.dao.find("select * from v_dept");
	}
	
	public List<Record> findByOrgOption(){
		return Db.find("select * from v_org");
	}
	
	public boolean addDept(VDept config) {
		return config.save();
	}
	
	public boolean editDept(VDept config) {
		return config.update();
	}
	
	public boolean deleteDept(Long id) {
		return VDept.dao.deleteById(id);
	}

	public List<Record> findDeptList(){
		return Db.find("select id as dept_id,dept_name from v_dept");
	}
}
