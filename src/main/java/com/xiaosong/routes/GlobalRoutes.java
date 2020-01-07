package com.xiaosong.routes;

import com.jfinal.config.Routes;
import com.sun.tools.javac.jvm.Code;
import com.xiaosong.common.access.companyUser.CompanyUserController;
//import com.xiaosong.common.access.inAndOut.InAndOutController;
import com.xiaosong.common.access.inAndOut.InAndOutController;
import com.xiaosong.common.admin.DemoController;
import com.xiaosong.common.code.CodeController;
import com.xiaosong.common.key.KeyController;
import com.xiaosong.common.user.UserController;
import com.xiaosong.common.user.userApp.UserFriendController;
import com.xiaosong.common.visitorRecord.VisitorRecordController;
import com.xiaosong.common.visitorRecord.VisitorRecordService;


/**
 * 所有控制器配置地址以及route级别过滤器
 * @author wgm
 * create by 2019-11-05
 *
 */
public class GlobalRoutes extends Routes{

	@Override
	public void config() {
		/**配置说明 controllerKey为Controller的前缀，如UserController的前缀为User
		 *   配置路径                                实际访问路径
		 * controllerKey        YourController.index()
		 * controllerKey/method YourController.method()
		 * controllerKey/method/v0-v1 YourController.method() 
		 * controllerKey/v0-v1 YourController.index()，所带 url 参数值为：v0-v1
		 */
		
		//this.add(controllerKey, controllerClass);
		String prefix="/visitor";
		//该处还可配置route级别的拦截器，对N个含有共同拦截器的控制层实现统一配置，减少代码冗余
		this.add(prefix+"/demo", DemoController.class);
		this.add(prefix+"/inAndOut", InAndOutController.class);
		this.add(prefix+"/companyUser", CompanyUserController.class);
		this.add(prefix+"/key", KeyController.class);
		this.add(prefix+"/user", UserController.class);
		this.add(prefix+"/code", CodeController.class);
		this.add(prefix+"/userFriend", UserFriendController.class);
		this.add(prefix+"/visitorRecord", VisitorRecordController.class);
	}

}
