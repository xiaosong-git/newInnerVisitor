package com.xiaosong.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseVDUser<M extends BaseVDUser<M>> extends Model<M> implements IBean {

	/**
	 * 白名单管理表
	 */
	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	/**
	 * 白名单管理表
	 */
	public java.lang.Long getId() {
		return getLong("id");
	}

	/**
	 * 公司id
	 */
	public M setCompanyId(java.lang.Long companyId) {
		set("companyId", companyId);
		return (M)this;
	}
	
	/**
	 * 公司id
	 */
	public java.lang.Long getCompanyId() {
		return getLong("companyId");
	}

	/**
	 * 部门id
	 */
	public M setSectionId(java.lang.Long sectionId) {
		set("sectionId", sectionId);
		return (M)this;
	}
	
	/**
	 * 部门id
	 */
	public java.lang.Long getSectionId() {
		return getLong("sectionId");
	}

	/**
	 * 人员Id
	 */
	public M setUserId(java.lang.Long userId) {
		set("userId", userId);
		return (M)this;
	}
	
	/**
	 * 人员Id
	 */
	public java.lang.Long getUserId() {
		return getLong("userId");
	}

	/**
	 * 人员姓名
	 */
	public M setUserName(java.lang.String userName) {
		set("userName", userName);
		return (M)this;
	}
	
	/**
	 * 人员姓名
	 */
	public java.lang.String getUserName() {
		return getStr("userName");
	}

	/**
	 * 创建日期yy:MM:dd
	 */
	public M setCreateDate(java.lang.String createDate) {
		set("createDate", createDate);
		return (M)this;
	}
	
	/**
	 * 创建日期yy:MM:dd
	 */
	public java.lang.String getCreateDate() {
		return getStr("createDate");
	}

	/**
	 * 创建时间HH:mm:ss
	 */
	public M setCreateTime(java.lang.String createTime) {
		set("createTime", createTime);
		return (M)this;
	}
	
	/**
	 * 创建时间HH:mm:ss
	 */
	public java.lang.String getCreateTime() {
		return getStr("createTime");
	}

	/**
	 * 角色:(staff:普通员工,manage:管理员,front:前台)
	 */
	public M setRoleType(java.lang.String roleType) {
		set("roleType", roleType);
		return (M)this;
	}
	
	/**
	 * 角色:(staff:普通员工,manage:管理员,front:前台)
	 */
	public java.lang.String getRoleType() {
		return getStr("roleType");
	}

	/**
	 * 状态：确认:applySuc/未确认:applying/确认不通过:applyFail
	 */
	public M setStatus(java.lang.String status) {
		set("status", status);
		return (M)this;
	}
	
	/**
	 * 状态：确认:applySuc/未确认:applying/确认不通过:applyFail
	 */
	public java.lang.String getStatus() {
		return getStr("status");
	}

	/**
	 * normal为正常，deleted为删除
	 */
	public M setCurrentStatus(java.lang.String currentStatus) {
		set("currentStatus", currentStatus);
		return (M)this;
	}
	
	/**
	 * normal为正常，deleted为删除
	 */
	public java.lang.String getCurrentStatus() {
		return getStr("currentStatus");
	}

	public M setPostId(java.lang.Long postId) {
		set("postId", postId);
		return (M)this;
	}
	
	public java.lang.Long getPostId() {
		return getLong("postId");
	}

	/**
	 * 拒绝理由
	 */
	public M setApplyfailAnsaesn(java.lang.String applyfailAnsaesn) {
		set("applyfailAnsaesn", applyfailAnsaesn);
		return (M)this;
	}
	
	/**
	 * 拒绝理由
	 */
	public java.lang.String getApplyfailAnsaesn() {
		return getStr("applyfailAnsaesn");
	}

	/**
	 * 性别
	 */
	public M setSex(java.lang.String sex) {
		set("sex", sex);
		return (M)this;
	}
	
	/**
	 * 性别
	 */
	public java.lang.String getSex() {
		return getStr("sex");
	}

	/**
	 * 是否涉密0可以被访问1不可访问
	 */
	public M setSecucode(java.lang.String secucode) {
		set("secucode", secucode);
		return (M)this;
	}
	
	/**
	 * 是否涉密0可以被访问1不可访问
	 */
	public java.lang.String getSecucode() {
		return getStr("secucode");
	}

	/**
	 * 授权类型0为自己授权1不可授权2为本部门授权3全体公司授权
	 */
	public M setAuthtype(java.lang.String authtype) {
		set("authtype", authtype);
		return (M)this;
	}
	
	/**
	 * 授权类型0为自己授权1不可授权2为本部门授权3全体公司授权
	 */
	public java.lang.String getAuthtype() {
		return getStr("authtype");
	}

	/**
	 * 拓展字段
	 */
	public M setExp1(java.lang.String exp1) {
		set("exp1", exp1);
		return (M)this;
	}
	
	/**
	 * 拓展字段
	 */
	public java.lang.String getExp1() {
		return getStr("exp1");
	}

	/**
	 * 拓展字段
	 */
	public M setExp2(java.lang.String exp2) {
		set("exp2", exp2);
		return (M)this;
	}
	
	/**
	 * 拓展字段
	 */
	public java.lang.String getExp2() {
		return getStr("exp2");
	}

	/**
	 * 拓展字段
	 */
	public M setExp3(java.lang.String exp3) {
		set("exp3", exp3);
		return (M)this;
	}
	
	/**
	 * 拓展字段
	 */
	public java.lang.String getExp3() {
		return getStr("exp3");
	}

}