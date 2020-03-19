package com.xiaosong.common.web.appMenu;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.xiaosong.model.VAppMenu;
import com.xiaosong.util.RetUtil;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月14日 下午5:36:16 
* 类说明 
*/
public class AppMenuController extends Controller{
	private Log log = Log.getLog(AppMenuController.class);
	public AppMenuService srv = AppMenuService.me;
	
	public void findList() {
		int currentPage = getInt("currentPage");
		int pageSize = getInt("pageSize");
		Page<VAppMenu> pagelist = srv.findList(currentPage,pageSize);
		renderJson(pagelist);
	}
	
	public void addAppMenu() throws Exception {
		String functionName = getPara("function_name");
		String menuCode = getPara("menu_code");
		String menuName = getPara("menu_name");
		String menuUrl= getPara("menu_url");
		Long sid= getLong("sid");
		String istop= getPara("istop");
		VAppMenu menu = getModel(VAppMenu.class);
		menu.setFunctionName(functionName);
		menu.setMenuCode(menuCode);
		menu.setMenuName(menuName);
		menu.setMenuUrl(menuUrl);
		menu.setSid(sid);
		menu.setIstop(istop);
		menu.setFunctionName(functionName);
		boolean bool = srv.addAppMenu(menu);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void editAppMenu() {
		long id = getLong("id");
		String functionName = getPara("function_name");
		String menuCode = getPara("menu_code");
		String menuName = getPara("menu_name");
		String menuUrl= getPara("menu_url");
		Long sid= getLong("sid");
		String istop= getPara("istop");
		VAppMenu menu = getModel(VAppMenu.class);
		menu.setFunctionName(functionName);
		menu.setMenuCode(menuCode);
		menu.setMenuName(menuName);
		menu.setMenuUrl(menuUrl);
		menu.setSid(sid);
		menu.setIstop(istop);
		menu.setFunctionName(functionName);
		boolean bool = srv.addAppMenu(menu);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void delAppMenu() {
		Long id = getLong("id");
		boolean bool = srv.deleteAppMenu(id);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
}
