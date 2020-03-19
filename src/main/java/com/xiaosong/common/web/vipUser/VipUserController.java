package com.xiaosong.common.web.vipUser;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.xiaosong.model.VCompVipUser;
import com.xiaosong.util.RetUtil;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月14日 下午11:09:42 
* 类说明 
*/
public class VipUserController extends Controller{
	private Log log = Log.getLog(VipUserController.class);
	public VipUserService srv = VipUserService.me;
	
	public void findList() {
		String tel = getPara("queryString");
		int currentPage = getInt("currentPage");
		int pageSize = getInt("pageSize");
		Page<VCompVipUser> pagelist = srv.findList(tel,currentPage,pageSize);
		renderJson(pagelist);
	}
	
	public void addVipUser() throws Exception {
		String userName = getPara("userName");
		String sex = getPara("sex");
		String phone = getPara("phone");
		String company = getPara("company");
		String position = getPara("position");
		String createtime = getPara("createtime");
		String endtime = getPara("endtime");
		String authorizeReason = getPara("authorize_reason");
		VCompVipUser vipUser = getModel(VCompVipUser.class);
		vipUser.setUserName(userName);
		vipUser.setPhone(phone);
		vipUser.setSex(sex);
		vipUser.setCompany(company);
		vipUser.setPosition(position);
		vipUser.setAuthorizeReason(authorizeReason);
		vipUser.setCreatetime(createtime);
		vipUser.setEndtime(endtime);
		boolean bool = srv.addVipUser(vipUser);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void editVipUser() {
		long id = getLong("id");
		String userName = getPara("userName");
		String sex = getPara("sex");
		String phone = getPara("phone");
		String company = getPara("company");
		String position = getPara("position");
		String createtime = getPara("createtime");
		String endtime = getPara("endtime");
		String authorizeReason = getPara("authorize_reason");
		VCompVipUser vipUser = getModel(VCompVipUser.class);
		vipUser.setUserName(userName);
		vipUser.setPhone(phone);
		vipUser.setSex(sex);
		vipUser.setCompany(company);
		vipUser.setPosition(position);
		vipUser.setAuthorizeReason(authorizeReason);
		vipUser.setCreatetime(createtime);
		vipUser.setEndtime(endtime);
		vipUser.setId(id);
		boolean bool = srv.editVipUser(vipUser);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void delVipUser() {
		Long id = getLong("id");
		boolean bool = srv.deleteVipUser(id);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
}
