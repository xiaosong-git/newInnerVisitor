package com.xiaosong.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseVDept<M extends BaseVDept<M>> extends Model<M> implements IBean {

	/**
	 * 原company_section表
	 */
	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	/**
	 * 原company_section表
	 */
	public java.lang.Long getId() {
		return getLong("id");
	}

	/**
	 * 部门编码
	 */
	public M setSectionCode(java.lang.String sectionCode) {
		set("sectionCode", sectionCode);
		return (M)this;
	}
	
	/**
	 * 部门编码
	 */
	public java.lang.String getSectionCode() {
		return getStr("sectionCode");
	}

	/**
	 * 部门名称
	 */
	public M setSectionName(java.lang.String sectionName) {
		set("sectionName", sectionName);
		return (M)this;
	}
	
	/**
	 * 部门名称
	 */
	public java.lang.String getSectionName() {
		return getStr("sectionName");
	}

	/**
	 * 公司Id
	 */
	public M setCompanyId(java.lang.Long companyId) {
		set("companyId", companyId);
		return (M)this;
	}
	
	/**
	 * 公司Id
	 */
	public java.lang.Long getCompanyId() {
		return getLong("companyId");
	}

}
