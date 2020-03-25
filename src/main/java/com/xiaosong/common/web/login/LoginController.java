package com.xiaosong.common.web.login;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xiaosong.constant.Constant;
import com.xiaosong.interceptor.LoginInterceptor;
import com.xiaosong.model.VSysUser;
import com.xiaosong.util.MD5Util;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月8日 上午10:58:22 
* 类说明 
*/
@Clear(LoginInterceptor.class)
public class LoginController extends Controller{
	
	private Log log = Log.getLog(LoginController.class);
	public LoginService srv = LoginService.me;
	
	public void userlogin() throws Throwable {
		//跨域请求
		HttpServletResponse response = getResponse();
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type,Content-Length, Authorization, Accept,X-Requested-With");//有些会把token放到header里,加在这里
		response.setHeader("Access-Control-Allow-Methods", "PUT,POST,GET,DELETE,OPTIONS");
		String token = UUID.randomUUID().toString();
		Map<String, Object> map = new HashMap<String, Object>();
		//getResponse().addHeader("Access-Control-Allow-Origin", "*");
		String userName = getPara("username");
		String passWord = getPara("password");
		passWord = MD5Util.MD5(passWord);
		VSysUser user = srv.checkLoginUser(userName, passWord);
		if(user!=null) {
			user.setToken(token);
			user.update();
			map.put("token", token);
			map.put("result", "success");
			map.put("username", userName);
			map.put("userRole", user.getRoleId());
			map.put("userId", user.getId().toString());
			map.put("resultMSG", "登录成功");
			CacheKit.put(Constant.SYS_ACCOUNT, user.getId().toString(), user);
			renderJson(map);
		}else {
			map.put("resultMSG", "登录失败，用户名或者密码不正确");
			renderJson(map);
		}
		
		
	}
}
