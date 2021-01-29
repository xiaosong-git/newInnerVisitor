package com.xiaosong.common.web.dept;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.model.VDept;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.util.RetUtil;

import java.util.UUID;

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
		String realName = get("realName");
		String phone = get("phone");
		String accessName = get("accessName");
		Page<Record> pagelist = srv.findList(currentPage,pageSize,deptName,realName,phone,accessName);
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
		String manageName = getPara("realName");
		String phone = getPara("phone");
		VDeptUser first = VDeptUser.dao.findFirst("select * from v_dept_user where realName=? and phone=? ", manageName, phone);
		if(first==null){
			renderJson(RetUtil.fail("该部门管理员不存在！请输入正确的姓名与手机号"));
			return;
		}
		Long[] accessIds = getParaValuesToLong("accessIds");
		VDept dept = getModel(VDept.class);
		dept.setManageUserId(first.getId());

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
		String manageName = getPara("realName");
		String phone = getPara("phone");
		VDeptUser first = VDeptUser.dao.findFirst("select * from v_dept_user where realName=? and phone=? ", manageName, phone);
		if(first==null){
			renderJson(RetUtil.fail("该部门管理员不存在！请输入正确的姓名与手机号"));
			return;
		}
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
