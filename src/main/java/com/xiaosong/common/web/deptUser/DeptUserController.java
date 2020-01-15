package com.xiaosong.common.web.deptUser;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.util.RetUtil;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月14日 下午8:27:07 
* 类说明 
*/
public class DeptUserController extends Controller{
	private Log log = Log.getLog(DeptUserController.class);
	public DeptUserService srv = DeptUserService.me;
	
	public void findList() {
		String tel = getPara("tel");
		int currentPage = getInt("currentPage");
		int pageSize = getInt("pageSize");
		Page<Record> pagelist = srv.findList(tel,currentPage,pageSize);
		renderJson(pagelist);
	}
	
	public void addDeptUser() throws Exception {
		String userName = getPara("userName");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createtime = df.format(new Date());
		String sex = getPara("sex");
		String tel = getPara("tel");
		VDeptUser deptUser = getModel(VDeptUser.class);
		deptUser.setUserName(userName);
		deptUser.setCreateDate(createtime);
		deptUser.setSex(sex);
		deptUser.setTel(tel);
		boolean bool = srv.addDeptUser(deptUser);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void editDeptUser() {
		long id = getLong("id");
		String userName = getPara("userName");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createtime = df.format(new Date());
		String sex = getPara("sex");
		String tel = getPara("tel");
		VDeptUser deptUser = getModel(VDeptUser.class);
		deptUser.setUserName(userName);
		deptUser.setCreateDate(createtime);
		deptUser.setSex(sex);
		deptUser.setTel(tel);
		deptUser.setId(id);
		boolean bool = srv.editDeptUser(deptUser);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void delDeptUser() {
		Long id = getLong("id");
		boolean bool = srv.deleteDeptUser(id);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
}
