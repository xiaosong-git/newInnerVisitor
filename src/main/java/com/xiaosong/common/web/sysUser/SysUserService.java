package com.xiaosong.common.web.sysUser;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.model.VSysUser;




/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月11日 下午3:17:47 
* 类说明 
*/
public class SysUserService {
	public static final	SysUserService me = new SysUserService();
	
	public Page<Record> findList(String tel,int currentPage,int pageSize){
		if(tel!=null) {
			Page<Record> page = Db.paginate(currentPage, pageSize, "select *", "from (select user.*,role.role_name FROM v_sys_user user LEFT JOIN v_user_role role on user.role_id=role.id where user.tel like CONCAT(?,'%')) as a", tel);
			return page;
		}else {
			return Db.paginate(currentPage, pageSize, "select *", " from (select user.*,role.role_name FROM v_sys_user user LEFT JOIN v_user_role role on user.role_id=role.id) as a");
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
