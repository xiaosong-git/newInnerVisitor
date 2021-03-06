package com.xiaosong.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.date.DateUtil;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xiaosong.common.api.user.UserUtil;
import com.xiaosong.common.web.access.AccessController;
import com.xiaosong.constant.Constant;
import com.xiaosong.model.VSysUser;
import com.xiaosong.model.vo.UserVo;
import com.xiaosong.util.IPUtil;
import com.xiaosong.util.RetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * 登录session过滤器
 * @author Administrator
 *
 */
@Slf4j
public class LoginInterceptor implements Interceptor {

	Logger logger = Logger.getLogger(LoginInterceptor.class);
	@Override
	public void intercept(Invocation inv) {
		//跨域请求
		HttpServletResponse response = inv.getController().getResponse();
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type,Content-Length, Authorization, Accept,X-Requested-With,userId,token");//有些会把token放到header里,加在这里
		response.setHeader("Access-Control-Allow-Methods", "PUT,POST,GET,DELETE");
		Controller con = inv.getController();

		String s = inv.getActionKey();
//		String method = con.getRequest().getMethod();
//		if ("OPTIONS".equals(method)){ return;}
		Map<String, String> map = new HashMap<String, String>();
		/*
		 * if(s.contains("visitor/web/login")) { inv.invoke(); }
		 */
		try {
			String ipAddress = IPUtil.getIp(con.getRequest());
			logger.info("时间："+ DateUtil.now() +",IP:"+ipAddress+"请求"+s);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		 if(s.contains("visitor/web")) {
			 HttpServletRequest request=con.getRequest();
			 String token = request.getHeader("token");

			 if (StringUtils.isEmpty(token)){
				 token=request.getParameter("token");
			 }

			 String userId = request.getHeader("userId");
			 if (StringUtils.isEmpty(userId)){
				 userId=request.getParameter("userId");
			 }
			 logger.info("headToken:"+token+",userId:"+userId);
			 UserVo user=CacheKit.get(Constant.SYS_ACCOUNT, userId);

			 if(user!=null) {
				 logger.info("管理平台用户："+user.getUsername());
				 if(user.getToken().equals(token)) {
					 inv.invoke();
				 }else {
					 con.renderJson(RetUtil.fail("登入过期"));
				 }
			 }else {
				 con.renderJson(RetUtil.fail("重复登入"));
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
