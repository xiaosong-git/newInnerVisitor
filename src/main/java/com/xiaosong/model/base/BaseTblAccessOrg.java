package com.xiaosong.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseTblAccessOrg<M extends BaseTblAccessOrg<M>> extends Model<M> implements IBean {

	/**
	 * 门禁org表
	 */
	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	/**
	 * 门禁org表
	 */
	public java.lang.Long getId() {
		return getLong("id");
	}

	/**
	 * 门禁id
	 */
	public M setAccessId(java.lang.Long accessId) {
		set("access_id", accessId);
		return (M)this;
	}
	
	/**
	 * 门禁id
	 */
	public java.lang.Long getAccessId() {
		return getLong("access_id");
	}

	/**
	 *  机构id
	 */
	public M setOrgId(java.lang.Long orgId) {
		set("org_id", orgId);
		return (M)this;
	}
	
	/**
	 *  机构id
	 */
	public java.lang.Long getOrgId() {
		return getLong("org_id");
	}

	/**
	 * 创建时间
	 */
	public M setCreateTime(java.lang.String createTime) {
		set("create_time", createTime);
		return (M)this;
	}
	
	/**
	 * 创建时间
	 */
	public java.lang.String getCreateTime() {
		return getStr("create_time");
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
	 * 状态：1-启用；2-禁用；3-删除
	 */
	public M setStatus(java.lang.Integer status) {
		set("status", status);
		return (M)this;
	}
	
	/**
	 * 状态：1-启用；2-禁用；3-删除
	 */
	public java.lang.Integer getStatus() {
		return getInt("status");
	}

}
