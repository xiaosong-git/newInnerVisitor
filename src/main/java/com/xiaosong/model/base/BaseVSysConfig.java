package com.xiaosong.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseVSysConfig<M extends BaseVSysConfig<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	/**
	 * 对应的子系统名称
	 */
	public M setFunctionName(java.lang.String functionName) {
		set("function_name", functionName);
		return (M)this;
	}
	
	/**
	 * 对应的子系统名称
	 */
	public java.lang.String getFunctionName() {
		return getStr("function_name");
	}

	/**
	 * 状态（是否启动）
	 */
	public M setStatus(java.lang.String status) {
		set("status", status);
		return (M)this;
	}
	
	/**
	 * 状态（是否启动）
	 */
	public java.lang.String getStatus() {
		return getStr("status");
	}

	/**
	 * 创建时间
	 */
	public M setCreatetime(java.lang.String createtime) {
		set("createtime", createtime);
		return (M)this;
	}
	
	/**
	 * 创建时间
	 */
	public java.lang.String getCreatetime() {
		return getStr("createtime");
	}

	public M setExtra1(java.lang.String extra1) {
		set("extra1", extra1);
		return (M)this;
	}
	
	public java.lang.String getExtra1() {
		return getStr("extra1");
	}

	public M setExtra2(java.lang.String extra2) {
		set("extra2", extra2);
		return (M)this;
	}
	
	public java.lang.String getExtra2() {
		return getStr("extra2");
	}

	public M setExtra3(java.lang.String extra3) {
		set("extra3", extra3);
		return (M)this;
	}
	
	public java.lang.String getExtra3() {
		return getStr("extra3");
	}

	public M setTrueName(java.lang.String trueName) {
		set("trueName", trueName);
		return (M)this;
	}
	
	public java.lang.String getTrueName() {
		return getStr("trueName");
	}

}
