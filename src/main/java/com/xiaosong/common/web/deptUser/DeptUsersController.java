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
public class DeptUsersController extends Controller{
	private Log log = Log.getLog(DeptUsersController.class);
	public DeptUserService srv = DeptUserService.me;
	
	public void findList() {
		String tel = getPara("tel");
		int currentPage = getInt("currentPage");
		int pageSize = getInt("pageSize");
		Page<Record> pagelist = srv.findList(tel,currentPage,pageSize);
		renderJson(pagelist);
	}
	
	public void addDeptUser() throws Exception {
		Long id = getLong("id");
		String realName = getPara("realName");
		String userId = getPara("userId");
		String sex = getPara("sex");
		Long deptId = getLong("deptId");
		String idNO = getPara("idNO");
		String phone = getPara("phone");
		String intime = getPara("intime");
		String addr = getPara("addr");
		String remark = getPara("remark");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createDate = df.format(new Date());
		VDeptUser deptUser = getModel(VDeptUser.class);
		deptUser.setRealName(realName);
		deptUser.setUserId(userId);
		deptUser.setSex(sex);
		deptUser.setDeptId(deptId);
		deptUser.setIdNO(idNO);
		deptUser.setPhone(phone);
		deptUser.setIntime(intime);
		deptUser.setRemark(remark);
		deptUser.setCreateDate(createDate);
		deptUser.setAddr(addr);
		deptUser.setId(id);
		boolean bool = srv.addDeptUser(deptUser);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void editDeptUser() {
		long id = getLong("id");
		String realName = getPara("realName");
		String userId = getPara("userId");
		String sex = getPara("sex");
		Long deptId = getLong("deptId");
		String idNO = getPara("idNO");
		String phone = getPara("phone");
		String intime = getPara("intime");
		String addr = getPara("addr");
		String remark = getPara("remark");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createDate = df.format(new Date());
		VDeptUser deptUser = getModel(VDeptUser.class);
		deptUser.setRealName(realName);
		deptUser.setUserId(userId);
		deptUser.setSex(sex);
		deptUser.setDeptId(deptId);
		deptUser.setIdNO(idNO);
		deptUser.setPhone(phone);
		deptUser.setIntime(intime);
		deptUser.setRemark(remark);
		deptUser.setCreateDate(createDate);
		deptUser.setAddr(addr);
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
