package com.xiaosong.common.web.visitor;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.web.appMenu.AppMenuService;
import com.xiaosong.common.web.sysConfig.SysConfigController;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月18日 上午10:53:03 
* 类说明 
*/
public class VisitorsController extends Controller{
	private Log log = Log.getLog(SysConfigController.class);
	public VisitorService srv = VisitorService.me;
	public AppMenuService menuservice = AppMenuService.me;
	public void findList() {
		String realName = getPara("queryString");
		int currentPage = getInt("currentPage");
		int pageSize = getInt("pageSize");
		Page<Record> pagelist = srv.findList(realName,currentPage,pageSize);
		renderJson(pagelist);
	}
}
