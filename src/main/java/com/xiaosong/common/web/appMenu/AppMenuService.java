package com.xiaosong.common.web.appMenu;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.xiaosong.model.VAppMenu;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月14日 下午5:36:28 
* 类说明 
*/
public class AppMenuService {
	public static final	AppMenuService me = new AppMenuService();
	
	public Page<VAppMenu> findList(int currentPage,int pageSize){
		return VAppMenu.dao.paginate(currentPage, pageSize, "select *", "from v_app_menu");
	}
	
	public boolean addAppMenu(VAppMenu menu) {
		return menu.save();
	}
	
	public boolean editAppMenu(VAppMenu menu) {
		return menu.update();
	}
	
	public boolean deleteAppMenu(Long id) {
		return VAppMenu.dao.deleteById(id);
	}
	
	public int updateByName(String function_name,String istop){
		//return VAppMenu.dao.find("select * from v_app_menu where function_name=?", function_name);
		return Db.update("update v_app_menu set istop=? where function_name=?", istop,function_name);
	}
}
