package com.xiaosong.common.web.sysAuth;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.model.VUserAuth;
import com.xiaosong.model.VUserRoleAuth;
import com.xiaosong.util.MenuUtil;
import com.xiaosong.util.TreeUtil;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月13日 下午5:13:22 
* 类说明 
*/
public class SysAuthService {
	public static final	SysAuthService me = new SysAuthService();
	
	public Page<VUserAuth> findList(int currentPage,int pageSize){
			return VUserAuth.dao.paginate(currentPage, pageSize, "select *", "from v_user_auth");
	}
	
	public boolean addSysAuth(VUserAuth user) {
		return user.save();
	}
	
	public boolean editSysAuth(VUserAuth user) {
		return user.update();
	}
	
	public boolean deleteSysAuth(Long id) {
		return VUserAuth.dao.deleteById(id);
	}
	
	/**
	  * 根据所有菜单权限和用户拥有的菜单权限得到当前可以显示在页面的菜单
	  * @param allMenuPerms
	  * @param userMenuPerms
	  * @return
	  */
	public List<Record> MenusTree(Long roleId) {
		List<VUserAuth> list = VUserAuth.dao.find("SELECT b.* from v_user_role_auth a LEFT JOIN v_user_auth b on a.auth_id=b.id where a.role_id="+roleId);
		List<Record> menustree = new ArrayList<Record>() ;
		for(VUserAuth userauth : list) {
			Record record = new Record();
			record.set("id", userauth.getId());
			record.set("label", userauth.getAuthName());
			record.set("parentId", userauth.getParentId());
			menustree.add(record);
		}
		List<Record> menusTrees = TreeUtil.getTreeList(menustree);
		return menusTrees;
	}
	
	public List<Record> checkList(long roleId){
		String sql = "SELECT b.id from v_user_role_auth a LEFT JOIN v_user_auth b on a.auth_id=b.id where a.role_id="+roleId;
		sql+=" AND b.id NOT in(SELECT parent_id from v_user_auth)";
		return Db.find(sql);
	}
	
	public void addRoleAuth(Long id,String ids) {
		Db.delete("delete from v_user_role_auth where role_id="+id);
		String[] authId = ids.split(",");
		for(String s:authId) {
			VUserRoleAuth roleAuth = new VUserRoleAuth();
			roleAuth.setRoleId(id);
			roleAuth.setAuthId(Long.parseLong(s));
			roleAuth.save();
		}
		
	}
	public List<Record> getUserAuth(Long roleId) {
		List<VUserAuth> list = VUserAuth.dao.find("SELECT b.* from v_user_role_auth a LEFT JOIN v_user_auth b on a.auth_id=b.id where a.role_id="+roleId+" ORDER BY b.id");
		List<Record> menustree = new ArrayList<Record>() ;
		for(VUserAuth userauth : list) {
			Record record = new Record();
			record.set("id", userauth.getId());
			record.set("icon", userauth.getDescription());
			record.set("title", userauth.getAuthName());
			record.set("index", userauth.getMenuUrl());
			record.set("parentId", userauth.getParentId());
			menustree.add(record);
		}
		List<Record> menusTrees = MenuUtil.getTreeList(menustree);
		return menusTrees;
	}
	
}
