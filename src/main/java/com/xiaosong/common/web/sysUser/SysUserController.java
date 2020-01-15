package com.xiaosong.common.web.sysUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.xiaosong.model.VSysUser;
import com.xiaosong.util.MD5Util;
import com.xiaosong.util.RetUtil;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月11日 下午3:08:02 
* 类说明 
*/
public class SysUserController extends Controller{
	
	private Log log = Log.getLog(SysUserController.class);
	public SysUserService srv = SysUserService.me;
	
	public void findList() {
		String tel = getPara("queryString");
		int currentPage = getInt("currentPage");
		int pageSize = getInt("pageSize");
		Page<VSysUser> pagelist = srv.findList(tel,currentPage,pageSize);
		renderJson(pagelist);
	}
	
	public void addSysUser() throws Exception {
		String username = getPara("username");
		String password = MD5Util.MD5(getPara("password"));
		String tel = getPara("tel");
		String trueName = getPara("true_name");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createtime = df.format(new Date());
		VSysUser user = getModel(VSysUser.class);
		user.setUsername(username);
		user.setPassword(password);
		user.setTel(tel);
		user.setCreatetime(createtime);
		user.setTrueName(trueName);
		boolean bool = srv.addSysUser(user);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void editSysUser() {
		long id = getLong("id");
		String username = getPara("username");
		String password = getPara("password");
		String tel = getPara("tel");
		String trueName = getPara("true_name");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createtime = df.format(new Date());
		VSysUser user = getModel(VSysUser.class);
		user.setUsername(username);
		user.setPassword(password);
		user.setTel(tel);
		user.setCreatetime(createtime);
		user.setTrueName(trueName);
		user.setId(id);
		boolean bool = srv.editSysUser(user);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void delSysUser() {
		Long id = getLong("id");
		boolean bool = srv.deleteSysUser(id);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
}