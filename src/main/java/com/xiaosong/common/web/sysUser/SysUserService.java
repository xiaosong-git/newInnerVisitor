package com.xiaosong.common.web.sysUser;

import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.xiaosong.model.VSysUser;




/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月11日 下午3:17:47 
* 类说明 
*/
public class SysUserService {
	public static final	SysUserService me = new SysUserService();
	
	public Page<VSysUser> findList(String tel,int currentPage,int pageSize){
		if(tel!=null) {
			Page<VSysUser> page = VSysUser.dao.paginate(currentPage, pageSize, "select *", "from v_sys_user where tel like CONCAT(?,'%')", tel);
			return page;
		}else {
			return VSysUser.dao.paginate(currentPage, pageSize, "select *", "from v_sys_user");
		}
	}
	
	public boolean addSysUser(VSysUser user) {
		return user.save();
	}
	
	public boolean editSysUser(VSysUser user) {
		return user.update();
	}
	
	public boolean deleteSysUser(Long id) {
		return VSysUser.dao.deleteById(id);
	}
	
}
