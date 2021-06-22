package com.xiaosong.common.web.sysAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.util.StringUtils;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xiaosong.constant.Constant;
import com.xiaosong.model.VUserAuth;
import com.xiaosong.model.vo.UserVo;
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
	/*
	 * 查询所有菜单
	 * *
	 */
	public void findMenu() {
		Long roleId = getLong("userRole");
		List<Record> pagelist = srv.MenusTree(roleId);
		renderJson(pagelist);
	}
	/**
	 * 查询某个角色下的菜单id
	 */
	public void checkMenu() {
		Long roleId = getLong("roleId");
		List<Object> list = new ArrayList<Object>();
		List<Record> recordList = srv.checkList(roleId);
		for(Record rec:recordList) {
			list.add(rec.get("id"));
		}
		renderJson(list);
	}
	/**
	 * 角色配置有拥有的菜单
	 */
	public void addRoleAuth() {
		Long id = getLong("id");
		String authIds = getPara("authIds");
		srv.addRoleAuth(id, authIds);
		renderJson(RetUtil.ok());
	}
	/**
	 * 用户登录后获取菜单列表
	 */
	public void loginMenu() {
//		String userId = getHeader("userId");
//		if (StringUtils.isEmpty(userId)){
//			userId=get("userId");
//		}
//		UserVo user= CacheKit.get(Constant.SYS_ACCOUNT, userId);
//		List<Record> pagelist = srv.getUserAuth(user.getUserRole());
		Long id = getLong("userRole");
		List<Record> pagelist = srv.getUserAuth(id);

		renderJson(pagelist);
	}
}
