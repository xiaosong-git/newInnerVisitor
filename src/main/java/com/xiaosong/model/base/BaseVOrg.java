package com.xiaosong.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseVOrg<M extends BaseVOrg<M>> extends Model<M> implements IBean {

	/**
	 * 大楼
	 */
	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	/**
	 * 大楼
	 */
	public java.lang.Long getId() {
		return getLong("id");
	}

	/**
	 * 大楼编码
	 */
	public M setOrgCode(java.lang.String orgCode) {
		set("org_code", orgCode);
		return (M)this;
	}
	
	/**
	 * 大楼编码
	 */
	public java.lang.String getOrgCode() {
		return getStr("org_code");
	}

	/**
	 * 大楼名
	 */
	public M setOrgName(java.lang.String orgName) {
		set("org_name", orgName);
		return (M)this;
	}
	
	/**
	 * 大楼名
	 */
	public java.lang.String getOrgName() {
		return getStr("org_name");
	}

	/**
	 * 创建人姓名---主管人
	 */
	public M setRealName(java.lang.String realName) {
		set("realName", realName);
		return (M)this;
	}
	
	/**
	 * 创建人姓名---主管人
	 */
	public java.lang.String getRealName() {
		return getStr("realName");
	}

	/**
	 * 手机
	 */
	public M setPhone(java.lang.String phone) {
		set("phone", phone);
		return (M)this;
	}
	
	/**
	 * 手机
	 */
	public java.lang.String getPhone() {
		return getStr("phone");
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

	/**
	 * 员工通行方式 0：人脸识别1：二维码 2：人脸or二维码
	 */
	public M setStaffAccessType(java.lang.String staffAccessType) {
		set("staff_access_type", staffAccessType);
		return (M)this;
	}
	
	/**
	 * 员工通行方式 0：人脸识别1：二维码 2：人脸or二维码
	 */
	public java.lang.String getStaffAccessType() {
		return getStr("staff_access_type");
	}

	/**
	 * 访客通行方式 0：人脸识别1：二维码 2：人脸or二维码
	 */
	public M setVisitorAccessType(java.lang.String visitorAccessType) {
		set("visitor_access_type", visitorAccessType);
		return (M)this;
	}
	
	/**
	 * 访客通行方式 0：人脸识别1：二维码 2：人脸or二维码
	 */
	public java.lang.String getVisitorAccessType() {
		return getStr("visitor_access_type");
	}

	/**
	 * 共享通行方式 0：人脸识别1：二维码 2：人脸or二维码
	 */
	public M setShareAccessType(java.lang.String shareAccessType) {
		set("share_access_type", shareAccessType);
		return (M)this;
	}
	
	/**
	 * 共享通行方式 0：人脸识别1：二维码 2：人脸or二维码
	 */
	public java.lang.String getShareAccessType() {
		return getStr("share_access_type");
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

	public M setExt1(java.lang.String ext1) {
		set("ext1", ext1);
		return (M)this;
	}
	
	public java.lang.String getExt1() {
		return getStr("ext1");
	}

	public M setExt2(java.lang.String ext2) {
		set("ext2", ext2);
		return (M)this;
	}
	
	public java.lang.String getExt2() {
		return getStr("ext2");
	}

}
