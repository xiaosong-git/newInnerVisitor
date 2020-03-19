package com.xiaosong.common.web.sysRole;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.xiaosong.model.VUserRole;
import com.xiaosong.util.RetUtil;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月13日 下午4:34:25 
* 类说明 
*/
public class SysRoleController extends Controller{
	private Log log = Log.getLog(SysRoleController.class);
	public SysRoleService srv = SysRoleService.me;
	
	public void findList() {
		int currentPage = getInt("currentPage");
		int pageSize = getInt("pageSize");
		Page<VUserRole> pagelist = srv.findList(currentPage,pageSize);
		renderJson(pagelist);
	}
	
	public void findByOption() {
		renderJson(srv.findByOption());
	}
	
	public void addSysRole() throws Exception {
		String roleName = getPara("role_name");
		String description = getPara("description");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createtime = df.format(new Date());
		VUserRole role = getModel(VUserRole.class);
		role.setRoleName(roleName);
		role.setCreatetime(createtime);
		role.setDescription(description);
		boolean bool = srv.addSysRole(role);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void editSysRole() {
		long id = getLong("id");
		String roleName = getPara("role_name");
		String description = getPara("description");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createtime = df.format(new Date());
		VUserRole role = getModel(VUserRole.class);
		role.setRoleName(roleName);
		role.setCreatetime(createtime);
		role.setDescription(description);
		role.setId(id);
		boolean bool = srv.editSysRole(role);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void delSysRole() {
		Long id = getLong("id");
		boolean bool = srv.deleteSysRole(id);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
}
