package com.xiaosong.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xiaosong.constant.Constant;
import com.xiaosong.model.VSysUser;

/**
 * 登录session过滤器
 * @author Administrator
 *
 */

public class LoginInterceptor implements Interceptor {

	public void intercept(Invocation inv) {
		//跨域请求
		HttpServletResponse response = inv.getController().getResponse();
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type,Content-Length, Authorization, Accept,X-Requested-With");//有些会把token放到header里,加在这里
		response.setHeader("Access-Control-Allow-Methods", "PUT,POST,GET,DELETE,OPTIONS");
		Controller con = inv.getController();
		String s = inv.getActionKey();
		Map<String, String> map = new HashMap<String, String>();
		/*
		 * if(s.contains("visitor/web/login")) { inv.invoke(); }
		 */
		 if(s.contains("visitor/web")) {

			 HttpServletRequest request=con.getRequest();
			 String token = request.getParameter("token");
			 String userId = request.getParameter("userId");
			 VSysUser user=CacheKit.get(Constant.SYS_ACCOUNT, userId);
			 if(user!=null) {
				 if(user.getToken().equals(token)) {
					 inv.invoke();
				 }else {
					 map.put("result", "overLogin");
					 con.renderJson(map);
					 //con.redirect("127.0.0.1:8088/#/login");
					 return;
				 }
			 }else {
				 inv.invoke();
				 map.put("result", "loginOut");
/*				 con.renderJson(map);
				 //con.redirect("127.0.0.1:8088/#/login");
				 return ;*/
			 }
		 }else {
			 inv.invoke();
		 }
//		String sessionId = con.getHeader(Constant.ADMIN_SESSION_ID);
//		if (StrKit.isBlank(sessionId)) {
//			con.renderJson(RetKit.fail(RetConstant.CODE_NO_LOGIN, "未登錄"));
//			return;
//		}
//		SysAccount account = LoginService.me.getAccountCacheWithSessionId(sessionId);
//		if (account == null) {
//			account = LoginService.me.loginWithSessionId(sessionId, Ipkit.getRealIp(con.getRequest()));
//		}
//		if (account == null) {
//			con.renderJson(RetKit.fail(RetConstant.CODE_LOGIN_EXPIRE, "會話過期，請重新登陸"));
//			return;
//		}
	}

}
