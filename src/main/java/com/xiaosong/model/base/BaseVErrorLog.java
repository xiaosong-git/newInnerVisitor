package com.xiaosong.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseVErrorLog<M extends BaseVErrorLog<M>> extends Model<M> implements IBean {

	/**
	 * 自增id
	 */
	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	/**
	 * 自增id
	 */
	public java.lang.Long getId() {
		return getLong("id");
	}

	/**
	 * 大楼编码
	 */
	public M setOrgCode(java.lang.String orgCode) {
		set("orgCode", orgCode);
		return (M)this;
	}
	
	/**
	 * 大楼编码
	 */
	public java.lang.String getOrgCode() {
		return getStr("orgCode");
	}

	/**
	 * 错误原因
	 */
	public M setLogContext(java.lang.String logContext) {
		set("logContext", logContext);
		return (M)this;
	}
	
	/**
	 * 错误原因
	 */
	public java.lang.String getLogContext() {
		return getStr("logContext");
	}

	/**
	 * 错误类型
	 */
	public M setErrorType(java.lang.String errorType) {
		set("errorType", errorType);
		return (M)this;
	}
	
	/**
	 * 错误类型
	 */
	public java.lang.String getErrorType() {
		return getStr("errorType");
	}

	/**
	 * 设备Id
	 */
	public M setDeviceId(java.lang.String deviceId) {
		set("deviceId", deviceId);
		return (M)this;
	}
	
	/**
	 * 设备Id
	 */
	public java.lang.String getDeviceId() {
		return getStr("deviceId");
	}

	/**
	 * 错误时间
	 */
	public M setErrorTime(java.lang.String errorTime) {
		set("errorTime", errorTime);
		return (M)this;
	}
	
	/**
	 * 错误时间
	 */
	public java.lang.String getErrorTime() {
		return getStr("errorTime");
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
