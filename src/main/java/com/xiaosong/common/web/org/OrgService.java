package com.xiaosong.common.web.org;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.model.VOrg;

import java.util.List;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月20日 上午9:42:24 
* 类说明 
*/
public class OrgService {
	public static final	OrgService me = new OrgService();
	
	public Page<Record> findList(String orgName,int currentPage,int pageSize){
		if(orgName!=null) {
			Page<Record> page = Db.paginate(currentPage, pageSize, "select *", "from v_org where org_name like CONCAT('%',?,'%')", orgName);
			return page;
		}else {
			return Db.paginate(currentPage, pageSize, "select *", " from v_org");
		}
	}
	
	public boolean addOrg(VOrg org) {
		return org.save();
	}
	
	public boolean editOrg(VOrg org) {
		return org.update();
	}
	
	public boolean deleteOrg(Long id) {
		return VOrg.dao.deleteById(id);
	}

	public List<Record> findOrgCodeAndName(){
		return Db.find("select org_code,org_name from v_org");
	}
}
