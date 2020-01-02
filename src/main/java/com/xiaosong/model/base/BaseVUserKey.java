package com.xiaosong.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseVUserKey<M extends BaseVUserKey<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	/**
	 * 密钥的key
	 */
	public M setWorkKey(java.lang.String workKey) {
		set("workKey", workKey);
		return (M)this;
	}
	
	/**
	 * 密钥的key
	 */
	public java.lang.String getWorkKey() {
		return getStr("workKey");
	}

	/**
	 * 状态，normal正常，disable:禁用
	 */
	public M setCstatus(java.lang.String cstatus) {
		set("cstatus", cstatus);
		return (M)this;
	}
	
	/**
	 * 状态，normal正常，disable:禁用
	 */
	public java.lang.String getCstatus() {
		return getStr("cstatus");
	}

	/**
	 * 创建时间
	 */
	public M setCreateDate(java.lang.String createDate) {
		set("createDate", createDate);
		return (M)this;
	}
	
	/**
	 * 创建时间
	 */
	public java.lang.String getCreateDate() {
		return getStr("createDate");
	}

}