package com.xiaosong.routes;

import com.jfinal.config.Routes;
import com.xiaosong.common.admin.DemoController;
import com.xiaosong.common.api.appversion.AppVersionController;
import com.xiaosong.common.api.code.CodeController;
import com.xiaosong.common.api.deptUser.DeptUserController;
import com.xiaosong.common.api.notice.BannerController;
import com.xiaosong.common.api.notice.NoticeController;
import com.xiaosong.common.api.param.ParamController;
import com.xiaosong.common.imgServer.errorLog.ErrorLogController;
import com.xiaosong.common.imgServer.file.FileController;
import com.xiaosong.common.imgServer.img.ImageController;
import com.xiaosong.common.web.appMenu.AppMenuController;
import com.xiaosong.common.web.dept.DeptController;
import com.xiaosong.common.web.deptUser.DeptUsersController;
import com.xiaosong.common.web.key.KeyController;
import com.xiaosong.common.web.login.LoginController;
import com.xiaosong.common.web.news.NewsController;
import com.xiaosong.common.web.org.OrgController;
import com.xiaosong.common.web.sysAuth.SysAuthController;
import com.xiaosong.common.web.sysConfig.SysConfigController;
import com.xiaosong.common.web.sysRole.SysRoleController;
import com.xiaosong.common.web.sysUser.SysUserController;
import com.xiaosong.common.web.vipUser.VipUserController;
import com.xiaosong.common.web.visitor.VisitorsController;
import com.xiaosong.common.imgServer.inAndOut.InAndOutController;
import com.xiaosong.common.api.user.UserController;
import com.xiaosong.common.api.user.userApp.UserFriendController;
import com.xiaosong.common.api.visitorRecord.VisitorRecordController;


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
		//api
		String prefix="/visitor";
		String webprefix="/visitor/web";
		//图片服务器
		String imgServer="/goldccm-imgServer";
		//该处还可配置route级别的拦截器，对N个含有共同拦截器的控制层实现统一配置，减少代码冗余
		this.add(prefix+"/demo", DemoController.class);
//		this.add(prefix+"/companyUser", CompanyUserController.class);
//		this.add(prefix+"/companyUser", CompanyUserController.class);
		this.add(prefix+"/key", KeyController.class);
		this.add(prefix+"/user", UserController.class);
		this.add(prefix+"/code", CodeController.class);
		this.add(prefix+"/userFriend", UserFriendController.class);
		this.add(prefix+"/visitorRecord", VisitorRecordController.class);
		this.add(imgServer+"/image", ImageController.class);
		this.add(webprefix+"/login", LoginController.class);
		this.add(webprefix+"/sysUser", SysUserController.class);
		this.add(webprefix+"/sysRole", SysRoleController.class);
		this.add(webprefix+"/sysAuth", SysAuthController.class);
		this.add(webprefix+"/vSysKey", KeyController.class);
		this.add(webprefix+"/sysConfig", SysConfigController.class);
		this.add(webprefix+"/appMenu", AppMenuController.class);
		this.add(webprefix+"/dept", DeptController.class);
		this.add(webprefix+"/deptUser", DeptUsersController.class);
		this.add(webprefix+"/vipUser", VipUserController.class);
		this.add(webprefix+"/org", OrgController.class);
		this.add(webprefix+"/visitor", VisitorsController.class);
		this.add(webprefix+"/notice", com.xiaosong.common.web.notice.NoticeController.class);
		this.add(webprefix+"/news", NewsController.class);
		this.add(prefix+"/p", ParamController.class);
		this.add(prefix+"/appVersion", AppVersionController.class);
		this.add(prefix+"/errorLog", ErrorLogController.class);
		this.add(prefix+"/b", BannerController.class);//广告
		this.add(prefix+"/notice", NoticeController.class);//公告
		this.add(prefix+"/companyUser", DeptUserController.class);//api公司员工 改名为部门员工
		this.add(imgServer+"/goldccm/file", FileController.class);
		this.add(imgServer+"/goldccm/image", ImageController.class);
		this.add(imgServer+"/inAndOut", InAndOutController.class);
	}

}
