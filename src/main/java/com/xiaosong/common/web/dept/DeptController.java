package com.xiaosong.common.web.dept;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
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
		String deptName = get("deptName");
		Page<Record> pagelist = srv.findList(currentPage,pageSize,deptName);
		renderJson(pagelist);
	}
	
	public void findByOption() {
		renderJson(srv.findByOption());
		
	}
	
	public void findByOrgOption() {
		renderJson(srv.findByOrgOption());
		
	}
	
	public void addDept() throws Exception {
		String deptName = getPara("dept_name");
		Long orgId = getLong("org_id");
		String floor = getPara("floor");
		Long[] accessIds = getParaValuesToLong("accessIds");
		VDept dept = getModel(VDept.class);
		dept.setDeptName(deptName);
		dept.setOrgId(orgId);
		dept.setFloor(floor);
		dept.setCode(UUID.randomUUID().toString());
		boolean bool = srv.addDept(dept,accessIds);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void editDept() {
		long id = getLong("id");
		String deptName = getPara("dept_name");
		Long orgId = getLong("org_id");
		String floor = getPara("floor");
		Long[] accessIds = getParaValuesToLong("accessIds");
		VDept dept = getModel(VDept.class);
		dept.setDeptName(deptName);
		dept.setOrgId(orgId);
		dept.setFloor(floor);
		dept.setId(id);
//		String collect = String.join(",", accessCodes);
//		dept.setAccessCodes(collect);
		boolean bool = srv.editDept(dept,accessIds);
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
