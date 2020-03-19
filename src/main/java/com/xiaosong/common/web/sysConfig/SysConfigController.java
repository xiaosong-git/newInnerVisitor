package com.xiaosong.common.web.sysConfig;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.xiaosong.common.web.appMenu.AppMenuService;
import com.xiaosong.model.VSysConfig;
import com.xiaosong.util.RetUtil;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月14日 下午4:27:12 
* 类说明 
*/
public class SysConfigController extends Controller{
	private Log log = Log.getLog(SysConfigController.class);
	public SysConfigService srv = SysConfigService.me;
	public AppMenuService menuservice = AppMenuService.me;
	public void findList() {
		int currentPage = getInt("currentPage");
		int pageSize = getInt("pageSize");
		Page<VSysConfig> pagelist = srv.findList(currentPage,pageSize);
		renderJson(pagelist);
	}
	
	public void addSysConfig() throws Exception {
		String functionName = getPara("function_name");
		String trueName = getPara("true_name");
		String status = getPara("status");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createtime = df.format(new Date());
		VSysConfig config = getModel(VSysConfig.class);
		config.setFunctionName(functionName);
		config.setTrueName(trueName);
		config.setStatus(status);
		menuservice.updateByName(functionName, status);
		config.setCreatetime(createtime);
		boolean bool = srv.addSysConfig(config);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void editSysConfig() {
		long id = getLong("id");
		String functionName = getPara("function_name");
		String trueName = getPara("true_name");
		String status = getPara("status");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createtime = df.format(new Date());
		VSysConfig config = getModel(VSysConfig.class);
		config.setFunctionName(functionName);
		config.setTrueName(trueName);
		config.setCreatetime(createtime);
		config.setStatus(status);
		menuservice.updateByName(functionName, status);
		config.setId(id);
		boolean bool = srv.editSysConfig(config);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void delSysConfig() {
		Long id = getLong("id");
		boolean bool = srv.deleteSysConfig(id);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
}
