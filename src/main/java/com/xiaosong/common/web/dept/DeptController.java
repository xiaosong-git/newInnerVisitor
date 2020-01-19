package com.xiaosong.common.web.dept;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.xiaosong.model.VDept;
import com.xiaosong.util.RetUtil;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月14日 下午8:26:14 
* 类说明 
*/
public class DeptController extends Controller{
	private Log log = Log.getLog(DeptController.class);
	public DeptService srv = DeptService.me;
	
	public void findList() {
		int currentPage = getInt("currentPage");
		int pageSize = getInt("pageSize");
		Page<VDept> pagelist = srv.findList(currentPage,pageSize);
		renderJson(pagelist);
	}
	
	public void findByOption() {
		renderJson(srv.findByOption());
		
	}
	
	public void addDept() throws Exception {
		String deptName = getPara("dept_name");
		String building = getPara("building");
		String floor = getPara("floor");
		VDept dept = getModel(VDept.class);
		dept.setDeptName(deptName);
		dept.setFloor(floor);
		boolean bool = srv.addDept(dept);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void editDept() {
		long id = getLong("id");
		String deptName = getPara("dept_name");
		String building = getPara("building");
		String floor = getPara("floor");
		VDept dept = getModel(VDept.class);
		dept.setDeptName(deptName);
		dept.setFloor(floor);
		dept.setId(id);
		boolean bool = srv.editDept(dept);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void delDept() {
		Long id = getLong("id");
		boolean bool = srv.deleteDept(id);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
}
