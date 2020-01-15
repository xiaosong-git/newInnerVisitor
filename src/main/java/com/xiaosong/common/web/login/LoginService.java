package com.xiaosong.common.web.login;

import com.xiaosong.model.VSysUser;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月8日 上午10:58:50 
* 类说明 
*/
public class LoginService {
	public static final	LoginService me = new LoginService();
	public VSysUser checkLoginUser(String userName, String passWord) {
		return VSysUser.dao.findFirst("select * from v_sys_user where username='"+userName+"' and password='"+passWord+"'");
	}
}
