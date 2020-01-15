package com.xiaosong.common.web.sysAuth;

import com.jfinal.plugin.activerecord.Page;
import com.xiaosong.model.VUserAuth;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月13日 下午5:13:22 
* 类说明 
*/
public class SysAuthService {
	public static final	SysAuthService me = new SysAuthService();
	
	public Page<VUserAuth> findList(int currentPage,int pageSize){
			return VUserAuth.dao.paginate(currentPage, pageSize, "select *", "from v_user_auth");
	}
	
	public boolean addSysAuth(VUserAuth user) {
		return user.save();
	}
	
	public boolean editSysAuth(VUserAuth user) {
		return user.update();
	}
	
	public boolean deleteSysAuth(Long id) {
		return VUserAuth.dao.deleteById(id);
	}
}
