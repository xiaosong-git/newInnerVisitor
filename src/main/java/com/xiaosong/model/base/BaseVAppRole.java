package com.xiaosong.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseVAppRole<M extends BaseVAppRole<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	/**
	 * 角色名
	 */
	public M setRoleName(java.lang.String roleName) {
		set("role_name", roleName);
		return (M)this;
	}
	
	/**
	 * 角色名
	 */
	public java.lang.String getRoleName() {
		return getStr("role_name");
	}

	/**
	 * 父id
	 */
	public M setSid(java.lang.Long sid) {
		set("sid", sid);
		return (M)this;
	}
	
	/**
	 * 父id
	 */
	public java.lang.Long getSid() {
		return getLong("sid");
	}

	/**
	 * 角色关系
	 */
	public M setRoleRelationNo(java.lang.String roleRelationNo) {
		set("role_relation_no", roleRelationNo);
		return (M)this;
	}
	
	/**
	 * 角色关系
	 */
	public java.lang.String getRoleRelationNo() {
		return getStr("role_relation_no");
	}

	/**
	 * 修改时间
	 */
	public M setUpdateTime(java.lang.String updateTime) {
		set("update_time", updateTime);
		return (M)this;
	}
	
	/**
	 * 修改时间
	 */
	public java.lang.String getUpdateTime() {
		return getStr("update_time");
	}

	/**
	 * 角色描述
	 */
	public M setDescription(java.lang.String description) {
		set("description", description);
		return (M)this;
	}
	
	/**
	 * 角色描述
	 */
	public java.lang.String getDescription() {
		return getStr("description");
	}

	/**
	 * 拓展字段1
	 */
	public M setExt1(java.lang.String ext1) {
		set("ext1", ext1);
		return (M)this;
	}
	
	/**
	 * 拓展字段1
	 */
	public java.lang.String getExt1() {
		return getStr("ext1");
	}

	/**
	 * 拓展字段2
	 */
	public M setExt2(java.lang.String ext2) {
		set("ext2", ext2);
		return (M)this;
	}
	
	/**
	 * 拓展字段2
	 */
	public java.lang.String getExt2() {
		return getStr("ext2");
	}

	/**
	 * 拓展字段3
	 */
	public M setExt3(java.lang.String ext3) {
		set("ext3", ext3);
		return (M)this;
	}
	
	/**
	 * 拓展字段3
	 */
	public java.lang.String getExt3() {
		return getStr("ext3");
	}

}
