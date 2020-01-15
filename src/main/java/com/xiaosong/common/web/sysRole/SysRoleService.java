package com.xiaosong.common.web.sysRole;

import com.jfinal.plugin.activerecord.Page;
import com.xiaosong.model.VUserRole;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月13日 下午4:34:46 
* 类说明 
*/
public class SysRoleService {
	public static final	SysRoleService me = new SysRoleService();
	
	public Page<VUserRole> findList(int currentPage,int pageSize){
			return VUserRole.dao.paginate(currentPage, pageSize, "select *", "from v_user_role");
	}
	
	public boolean addSysRole(VUserRole user) {
		return user.save();
	}
	
	public boolean editSysRole(VUserRole user) {
		return user.update();
	}
	
	public boolean deleteSysRole(Long id) {
		return VUserRole.dao.deleteById(id);
	}
}
