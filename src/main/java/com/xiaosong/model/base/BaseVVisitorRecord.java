package com.xiaosong.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseVVisitorRecord<M extends BaseVVisitorRecord<M>> extends Model<M> implements IBean {

	/**
	 * 智慧访客
	 */
	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	/**
	 * 智慧访客
	 */
	public java.lang.Long getId() {
		return getLong("id");
	}

	/**
	 * 访问日期
	 */
	public M setVisitDate(java.lang.String visitDate) {
		set("visitDate", visitDate);
		return (M)this;
	}
	
	/**
	 * 访问日期
	 */
	public java.lang.String getVisitDate() {
		return getStr("visitDate");
	}

	/**
	 * 访问时间
	 */
	public M setVisitTime(java.lang.String visitTime) {
		set("visitTime", visitTime);
		return (M)this;
	}
	
	/**
	 * 访问时间
	 */
	public java.lang.String getVisitTime() {
		return getStr("visitTime");
	}

	/**
	 * 访客id
	 */
	public M setUserId(java.lang.Long userId) {
		set("userId", userId);
		return (M)this;
	}
	
	/**
	 * 访客id
	 */
	public java.lang.Long getUserId() {
		return getLong("userId");
	}

	/**
	 * 被访者id
	 */
	public M setVisitorId(java.lang.Long visitorId) {
		set("visitorId", visitorId);
		return (M)this;
	}
	
	/**
	 * 被访者id
	 */
	public java.lang.Long getVisitorId() {
		return getLong("visitorId");
	}

	/**
	 * 访问原因
	 */
	public M setReason(java.lang.String reason) {
		set("reason", reason);
		return (M)this;
	}
	
	/**
	 * 访问原因
	 */
	public java.lang.String getReason() {
		return getStr("reason");
	}

	/**
	 * 状态 applying:申请中，applySuccess:接受访问，applyFail:拒绝访问
	 */
	public M setCstatus(java.lang.String cstatus) {
		set("cstatus", cstatus);
		return (M)this;
	}
	
	/**
	 * 状态 applying:申请中，applySuccess:接受访问，applyFail:拒绝访问
	 */
	public java.lang.String getCstatus() {
		return getStr("cstatus");
	}

	/**
	 * 日期类型:无期：Indefinite,有限期:limitPeriod
	 */
	public M setDateType(java.lang.String dateType) {
		set("dateType", dateType);
		return (M)this;
	}
	
	/**
	 * 日期类型:无期：Indefinite,有限期:limitPeriod
	 */
	public java.lang.String getDateType() {
		return getStr("dateType");
	}

	/**
	 * 开始日期
	 */
	public M setStartDate(java.lang.String startDate) {
		set("startDate", startDate);
		return (M)this;
	}
	
	/**
	 * 开始日期
	 */
	public java.lang.String getStartDate() {
		return getStr("startDate");
	}

	/**
	 * 结束日期
	 */
	public M setEndDate(java.lang.String endDate) {
		set("endDate", endDate);
		return (M)this;
	}
	
	/**
	 * 结束日期
	 */
	public java.lang.String getEndDate() {
		return getStr("endDate");
	}

	/**
	 * 被访者回复
	 */
	public M setAnswerContent(java.lang.String answerContent) {
		set("answerContent", answerContent);
		return (M)this;
	}
	
	/**
	 * 被访者回复
	 */
	public java.lang.String getAnswerContent() {
		return getStr("answerContent");
	}

	/**
	 * 被访者大楼编码
	 */
	public M setOrgCode(java.lang.String orgCode) {
		set("orgCode", orgCode);
		return (M)this;
	}
	
	/**
	 * 被访者大楼编码
	 */
	public java.lang.String getOrgCode() {
		return getStr("orgCode");
	}

	/**
	 * 被访者公司Id
	 */
	public M setCompanyId(java.lang.Long companyId) {
		set("companyId", companyId);
		return (M)this;
	}
	
	/**
	 * 被访者公司Id
	 */
	public java.lang.Long getCompanyId() {
		return getLong("companyId");
	}

	/**
	 * 访问类型b浏览器
	 */
	public M setVitype(java.lang.String vitype) {
		set("vitype", vitype);
		return (M)this;
	}
	
	/**
	 * 访问类型b浏览器
	 */
	public java.lang.String getVitype() {
		return getStr("vitype");
	}

	/**
	 * 1--访问，2--邀约
	 */
	public M setRecordType(java.lang.Integer recordType) {
		set("recordType", recordType);
		return (M)this;
	}
	
	/**
	 * 1--访问，2--邀约
	 */
	public java.lang.Integer getRecordType() {
		return getInt("recordType");
	}

	/**
	 * 审核日期
	 */
	public M setReplyDate(java.lang.String replyDate) {
		set("replyDate", replyDate);
		return (M)this;
	}
	
	/**
	 * 审核日期
	 */
	public java.lang.String getReplyDate() {
		return getStr("replyDate");
	}

	/**
	 * 审核时间
	 */
	public M setReplyTime(java.lang.String replyTime) {
		set("replyTime", replyTime);
		return (M)this;
	}
	
	/**
	 * 审核时间
	 */
	public java.lang.String getReplyTime() {
		return getStr("replyTime");
	}

	/**
	 * 审核人ID
	 */
	public M setReplyUserId(java.lang.Long replyUserId) {
		set("replyUserId", replyUserId);
		return (M)this;
	}
	
	/**
	 * 审核人ID
	 */
	public java.lang.Long getReplyUserId() {
		return getLong("replyUserId");
	}

	/**
	 * 是否已下发用户 T--是 F--否
	 */
	public M setIsReceive(java.lang.String isReceive) {
		set("isReceive", isReceive);
		return (M)this;
	}
	
	/**
	 * 是否已下发用户 T--是 F--否
	 */
	public java.lang.String getIsReceive() {
		return getStr("isReceive");
	}

	/**
	 * 备注名 非好友邀约用
	 */
	public M setRemarkName(java.lang.String remarkName) {
		set("remarkName", remarkName);
		return (M)this;
	}
	
	/**
	 * 备注名 非好友邀约用
	 */
	public java.lang.String getRemarkName() {
		return getStr("remarkName");
	}

	/**
	 * in 内网 --out 外网
	 */
	public M setUserType(java.lang.String userType) {
		set("userType", userType);
		return (M)this;
	}
	
	/**
	 * in 内网 --out 外网
	 */
	public java.lang.String getUserType() {
		return getStr("userType");
	}

	/**
	 * in 内网 --out 外网
	 */
	public M setVisitorType(java.lang.String visitorType) {
		set("visitorType", visitorType);
		return (M)this;
	}
	
	/**
	 * in 内网 --out 外网
	 */
	public java.lang.String getVisitorType() {
		return getStr("visitorType");
	}

	/**
	 * 外部访问记录id
	 */
	public M setOutRecordId(java.lang.Long outRecordId) {
		set("outRecordId", outRecordId);
		return (M)this;
	}
	
	/**
	 * 外部访问记录id
	 */
	public java.lang.Long getOutRecordId() {
		return getLong("outRecordId");
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

}
