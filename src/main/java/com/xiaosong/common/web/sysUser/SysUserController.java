package com.xiaosong.common.web.sysUser;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.model.VSysUser;
import com.xiaosong.util.MD5Util;
import com.xiaosong.util.RetUtil;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月11日 下午3:08:02 
* 类说明 
*/
public class SysUserController extends Controller{
	
	private Log log = Log.getLog(SysUserController.class);
	public SysUserService srv = SysUserService.me;
	
	public void findList() {
		//todo 修改查询条件
		String tel = get("tel");
		String name = get("true_name");
		Long roleId = getLong("role_id");
		Long userId = getLong("userId");
		int currentPage = getInt("currentPage");
		int pageSize = getInt("pageSize");
		Page<Record> pagelist = srv.findList(tel,name,roleId,userId,currentPage,pageSize);
		renderJson(pagelist);
	}
	
	public void addSysUser() throws Exception {
		Long userId = getLong("userId");
		String username = getPara("username");
		String password = MD5Util.MD5(getPara("password"));
		String tel = getPara("tel");
		String trueName = getPara("true_name");
		Long roleId = getLong("role_id");
		String idCard = getPara("idcard");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createtime = df.format(new Date());
		VSysUser user = getModel(VSysUser.class);
		user.setUsername(username);
		user.setPassword(password);
		user.setTel(tel);
		user.setRoleId(roleId);
		user.setCreatetime(createtime);
		user.setTrueName(trueName);
		user.setParentId(BigInteger.valueOf(userId));
		if(srv.findUser(username)) {
			boolean bool = srv.addSysUser(user);
			if(bool) {
//				String organCode= null;
//				String token = SSOService.me.getToken();
//				boolean result = SSOService.me.userSync(token,username,"000000",trueName,tel,idCard,organCode);
//				if(result)
//				{
//				//	user.setIsSync("T");
//					user.update();
//				}
				renderJson(RetUtil.ok());
			}else {
				renderJson(RetUtil.fail());
			}
		}else {
			renderJson(RetUtil.fail());
		}
		
	}
	
	public void editSysUser() {
		long id = getLong("id");
		String username = getPara("username");
		String password = getPara("password");
		String tel = getPara("tel");
		Long roleId = getLong("role_id");
		String trueName = getPara("true_name");
		String idCard = getPara("idcard");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createtime = df.format(new Date());
		VSysUser user = getModel(VSysUser.class);
		user.setUsername(username);
		user.setPassword(password);
		user.setTel(tel);
		user.setRoleId(roleId);
		user.setCreatetime(createtime);
		user.setTrueName(trueName);
		user.setId(id);
		boolean bool = srv.editSysUser(user);
		if(bool) {
//			String organCode= null;
//			String token = SSOService.me.getToken();
//			boolean result = SSOService.me.userSync(token,username,"000000",trueName,tel,organCode);
//			if(result)
//			{
//				//user.setIsSync("T");
//				user.update();
//			}
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
