package com.xiaosong.common.web.dept;

import com.jfinal.plugin.activerecord.Page;
import com.xiaosong.model.VDept;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月14日 下午8:26:26 
* 类说明 
*/
public class DeptService {
	public static final	DeptService me = new DeptService();
	
	public Page<VDept> findList(int currentPage,int pageSize){
		return VDept.dao.paginate(currentPage, pageSize, "select *", "from v_dept");
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
}
