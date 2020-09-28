package com.xiaosong.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseVDeptUser<M extends BaseVDeptUser<M>> extends Model<M> implements IBean {

	/**
	 * 员工管理表--原company_user表
	 */
	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	/**
	 * 员工管理表--原company_user表
	 */
	public java.lang.Long getId() {
		return getLong("id");
	}

	/**
	 * 部门id
	 */
	public M setDeptId(java.lang.Long deptId) {
		set("deptId", deptId);
		return (M)this;
	}
	
	/**
	 * 部门id
	 */
	public java.lang.Long getDeptId() {
		return getLong("deptId");
	}

	/**
	 * 工号
	 */
	public M setUserNo(java.lang.String userNo) {
		set("userNo", userNo);
		return (M)this;
	}
	
	/**
	 * 工号
	 */
	public java.lang.String getUserNo() {
		return getStr("userNo");
	}

	/**
	 * 员工姓名
	 */
	public M setRealName(java.lang.String realName) {
		set("realName", realName);
		return (M)this;
	}
	
	/**
	 * 员工姓名
	 */
	public java.lang.String getRealName() {
		return getStr("realName");
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
	 * 职位 manage 部门管理员 staff 员工 
	 */
	public M setRoleType(java.lang.String roleType) {
		set("roleType", roleType);
		return (M)this;
	}
	
	/**
	 * 职位 manage 部门管理员 staff 员工 
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
	 * 员工电话
	 */
	public M setPhone(java.lang.String phone) {
		set("phone", phone);
		return (M)this;
	}
	
	/**
	 * 员工电话
	 */
	public java.lang.String getPhone() {
		return getStr("phone");
	}

	/**
	 * 证件号 用密钥加密，取出来再解密
	 */
	public M setIdNO(java.lang.String idNO) {
		set("idNO", idNO);
		return (M)this;
	}
	
	/**
	 * 证件号 用密钥加密，取出来再解密
	 */
	public java.lang.String getIdNO() {
		return getStr("idNO");
	}

	/**
	 * 是否实名 F:未实名 T：实名;N:审核中
	 */
	public M setIsAuth(java.lang.String isAuth) {
		set("isAuth", isAuth);
		return (M)this;
	}
	
	/**
	 * 是否实名 F:未实名 T：实名;N:审核中
	 */
	public java.lang.String getIsAuth() {
		return getStr("isAuth");
	}

	/**
	 * 手持证件照
	 */
	public M setIdHandleImgUrl(java.lang.String idHandleImgUrl) {
		set("idHandleImgUrl", idHandleImgUrl);
		return (M)this;
	}
	
	/**
	 * 手持证件照
	 */
	public java.lang.String getIdHandleImgUrl() {
		return getStr("idHandleImgUrl");
	}

	/**
	 * 头像照片位置
	 */
	public M setHeadImgUrl(java.lang.String headImgUrl) {
		set("headImgUrl", headImgUrl);
		return (M)this;
	}
	
	/**
	 * 头像照片位置
	 */
	public java.lang.String getHeadImgUrl() {
		return getStr("headImgUrl");
	}

	/**
	 * 地址
	 */
	public M setAddr(java.lang.String addr) {
		set("addr", addr);
		return (M)this;
	}
	
	/**
	 * 地址
	 */
	public java.lang.String getAddr() {
		return getStr("addr");
	}

	/**
	 * 入职时间
	 */
	public M setIntime(java.lang.String intime) {
		set("intime", intime);
		return (M)this;
	}
	
	/**
	 * 入职时间
	 */
	public java.lang.String getIntime() {
		return getStr("intime");
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

	/**
	 * token
	 */
	public M setToken(java.lang.String token) {
		set("token", token);
		return (M)this;
	}
	
	/**
	 * token
	 */
	public java.lang.String getToken() {
		return getStr("token");
	}

	/**
	 * 默认密码
	 */
	public M setSysPwd(java.lang.String sysPwd) {
		set("sysPwd", sysPwd);
		return (M)this;
	}
	
	/**
	 * 默认密码
	 */
	public java.lang.String getSysPwd() {
		return getStr("sysPwd");
	}

	/**
	 * 个推cid
	 */
	public M setDeviceToken(java.lang.String deviceToken) {
		set("deviceToken", deviceToken);
		return (M)this;
	}
	
	/**
	 * 个推cid
	 */
	public java.lang.String getDeviceToken() {
		return getStr("deviceToken");
	}

	/**
	 * APP在线情况
	 */
	public M setIsOnlineApp(java.lang.String isOnlineApp) {
		set("isOnlineApp", isOnlineApp);
		return (M)this;
	}
	
	/**
	 * APP在线情况
	 */
	public java.lang.String getIsOnlineApp() {
		return getStr("isOnlineApp");
	}

	/**
	 * 1--ios 2--andriod
	 */
	public M setDeviceType(java.lang.String deviceType) {
		set("deviceType", deviceType);
		return (M)this;
	}
	
	/**
	 * 1--ios 2--andriod
	 */
	public java.lang.String getDeviceType() {
		return getStr("deviceType");
	}

	/**
	 * 修改时间
	 */
	public M setUpdateTime(java.lang.String updateTime) {
		set("updateTime", updateTime);
		return (M)this;
	}
	
	/**
	 * 修改时间
	 */
	public java.lang.String getUpdateTime() {
		return getStr("updateTime");
	}

	/**
	 * 扩展字段1
	 */
	public M setExp1(java.lang.String exp1) {
		set("exp1", exp1);
		return (M)this;
	}
	
	/**
	 * 扩展字段1
	 */
	public java.lang.String getExp1() {
		return getStr("exp1");
	}

	/**
	 * 扩展字段2
	 */
	public M setExp2(java.lang.String exp2) {
		set("exp2", exp2);
		return (M)this;
	}
	
	/**
	 * 扩展字段2
	 */
	public java.lang.String getExp2() {
		return getStr("exp2");
	}

	public M setAuthDate(java.lang.String authDate) {
		set("authDate", authDate);
		return (M)this;
	}
	
	public java.lang.String getAuthDate() {
		return getStr("authDate");
	}

	/**
	 * 是否已经发送完成 T :已发送  F:未发送
	 */
	public M setIsReceive(java.lang.String isReceive) {
		set("isReceive", isReceive);
		return (M)this;
	}
	
	/**
	 * 是否已经发送完成 T :已发送  F:未发送
	 */
	public java.lang.String getIsReceive() {
		return getStr("isReceive");
	}

	/**
	 * 卡号
	 */
	public M setCardNO(java.lang.String cardNO) {
		set("cardNO", cardNO);
		return (M)this;
	}
	
	/**
	 * 卡号
	 */
	public java.lang.String getCardNO() {
		return getStr("cardNO");
	}

	/**
	 * 人员类型，staff-员工，visitor-访客
	 */
	public M setUserType(java.lang.String userType) {
		set("userType", userType);
		return (M)this;
	}
	
	/**
	 * 人员类型，staff-员工，visitor-访客
	 */
	public java.lang.String getUserType() {
		return getStr("userType");
	}

}
