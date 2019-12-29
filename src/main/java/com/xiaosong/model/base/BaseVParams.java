package com.xiaosong.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseVParams<M extends BaseVParams<M>> extends Model<M> implements IBean {

	/**
	 * 系统参数
	 */
	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	/**
	 * 系统参数
	 */
	public java.lang.Long getId() {
		return getLong("id");
	}

	/**
	 * 参数名
	 */
	public M setParamName(java.lang.String paramName) {
		set("paramName", paramName);
		return (M)this;
	}
	
	/**
	 * 参数名
	 */
	public java.lang.String getParamName() {
		return getStr("paramName");
	}

	/**
	 * 参数用法
	 */
	public M setParamText(java.lang.String paramText) {
		set("paramText", paramText);
		return (M)this;
	}
	
	/**
	 * 参数用法
	 */
	public java.lang.String getParamText() {
		return getStr("paramText");
	}

	/**
	 * 备注
	 */
	public M setRemark(java.lang.String remark) {
		set("remark", remark);
		return (M)this;
	}
	
	/**
	 * 备注
	 */
	public java.lang.String getRemark() {
		return getStr("remark");
	}

}
