package com.xiaosong.common.web.org;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.model.VOrg;
import com.xiaosong.util.RetUtil;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月20日 上午9:42:08 
* 类说明 
*/
public class OrgController extends Controller{
	private Log log = Log.getLog(OrgController.class);
	public OrgService srv = OrgService.me;
	
	public void findList() {
		String orgName = getPara("queryString");
		int currentPage = getInt("currentPage");
		int pageSize = getInt("pageSize");
		Page<Record> pagelist = srv.findList(orgName,currentPage,pageSize);
		renderJson(pagelist);
	}
	
	public void addOrg() throws Exception {
		String orgCode = getPara("org_code");
		String orgName = getPara("org_name");
		String staffAccessType = getPara("staff_access_type");
		String visitorAccessType = getPara("visitor_access_type");
		String shareAccessType = getPara("share_access_type");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createDate = df.format(new Date());
		VOrg org = getModel(VOrg.class);
		org.setOrgCode(orgCode);
		org.setOrgName(orgName);
		org.setStaffAccessType(staffAccessType);
		org.setVisitorAccessType(visitorAccessType);
		org.setShareAccessType(shareAccessType);
		org.setCreateDate(createDate);
		boolean bool = srv.addOrg(org);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void editOrg() {
		long id = getLong("id");
		String orgCode = getPara("org_code");
		String orgName = getPara("org_name");
		String staffAccessType = getPara("staff_access_type");
		String visitorAccessType = getPara("visitor_access_type");
		String shareAccessType = getPara("share_access_type");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createDate = df.format(new Date());
		VOrg org = getModel(VOrg.class);
		org.setOrgCode(orgCode);
		org.setOrgName(orgName);
		org.setStaffAccessType(staffAccessType);
		org.setVisitorAccessType(visitorAccessType);
		org.setShareAccessType(shareAccessType);
		org.setCreateDate(createDate);
		org.setId(id);
		boolean bool = srv.editOrg(org);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void delOrg() {
		Long id = getLong("id");
		boolean bool = srv.deleteOrg(id);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
}
