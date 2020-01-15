package com.xiaosong.common.web.sysAuth;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.xiaosong.model.VUserAuth;
import com.xiaosong.util.RetUtil;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月13日 下午5:13:01 
* 类说明 
*/
public class SysAuthController extends Controller{
	private Log log = Log.getLog(SysAuthController.class);
	public SysAuthService srv = SysAuthService.me;
	
	public void findList() {
		int currentPage = getInt("currentPage");
		int pageSize = getInt("pageSize");
		Page<VUserAuth> pagelist = srv.findList(currentPage,pageSize);
		renderJson(pagelist);
	}
	
	public void addSysAuth() throws Exception {
		String authName = getPara("auth_name");
		String description = getPara("description");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createtime = df.format(new Date());
		VUserAuth auth = getModel(VUserAuth.class);
		auth.setAuthName(authName);
		auth.setCreatetime(createtime);
		auth.setDescription(description);
		boolean bool = srv.addSysAuth(auth);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void editSysAuth() {
		long id = getLong("id");
		String authName = getPara("auth_name");
		String description = getPara("description");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createtime = df.format(new Date());
		VUserAuth auth = getModel(VUserAuth.class);
		auth.setAuthName(authName);
		auth.setCreatetime(createtime);
		auth.setDescription(description);
		auth.setId(id);
		boolean bool = srv.editSysAuth(auth);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void delSysAuth() {
		Long id = getLong("id");
		boolean bool = srv.deleteSysAuth(id);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
}
