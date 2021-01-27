package com.xiaosong.common.web.sysUser;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.model.VSysUser;
import org.apache.commons.lang3.StringUtils;


/**
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月11日 下午3:17:47 
* 类说明 
*/
public class SysUserService {
	public static final	SysUserService me = new SysUserService();
	
	public Page<Record> findList(String tel,String name,Long roleId,Long userId,int currentPage,int pageSize){
		StringBuilder sql = new StringBuilder(" from (select user.*,role.role_name FROM v_sys_user user LEFT JOIN v_user_role role on user.role_id=role.id");
		StringBuilder whereSql =new StringBuilder(" where user.parent_id=? ");
		if(StringUtils.isNotBlank(tel)) {
			whereSql.append(" and user.tel like CONCAT('%',").append(tel).append(",'%')");
		}
		if (StringUtils.isNotBlank(name)){
			whereSql.append(" and user.true_name like CONCAT('%',").append(name).append(",'%')");
		}
		if (roleId!=null){
			whereSql.append(" and user.role_id =").append(roleId);
		}
		return Db.paginate(currentPage, pageSize, "select *", sql.append(whereSql).append(") as a").toString(),userId);
	}
	
	public boolean addSysUser(VSysUser user) {
		return user.save();
	}
	
	public boolean editSysUser(VSysUser user) {
		return user.update();
	}
	
	public boolean deleteSysUser(Long id) {
		return VSysUser.dao.deleteById(id);
	}
	
	public boolean findUser(String username) {
		List<Record> list = Db.find("select * from v_sys_user where username='"+username+"'");
		if(list.size()==0) {
			return true;
		}
		return false;
	}
	
}
