package com.xiaosong.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseTblSms<M extends BaseTblSms<M>> extends Model<M> implements IBean {

	/**
	 * 短信记录id
	 */
	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	/**
	 * 短信记录id
	 */
	public java.lang.Long getId() {
		return getLong("id");
	}

	/**
	 * 使用者
	 */
	public M setUser(java.lang.String user) {
		set("user", user);
		return (M)this;
	}
	
	/**
	 * 使用者
	 */
	public java.lang.String getUser() {
		return getStr("user");
	}

	/**
	 * 发送消息
	 */
	public M setContent(java.lang.String content) {
		set("content", content);
		return (M)this;
	}
	
	/**
	 * 发送消息
	 */
	public java.lang.String getContent() {
		return getStr("content");
	}

	/**
	 * 时间 年月日
	 */
	public M setDate(java.lang.String date) {
		set("date", date);
		return (M)this;
	}
	
	/**
	 * 时间 年月日
	 */
	public java.lang.String getDate() {
		return getStr("date");
	}

	/**
	 * 日期
	 */
	public M setTime(java.lang.String time) {
		set("time", time);
		return (M)this;
	}
	
	/**
	 * 日期
	 */
	public java.lang.String getTime() {
		return getStr("time");
	}

	/**
	 * 短信类型 1--云片网 2--阿里云
	 */
	public M setType(java.lang.Integer type) {
		set("type", type);
		return (M)this;
	}
	
	/**
	 * 短信类型 1--云片网 2--阿里云
	 */
	public java.lang.Integer getType() {
		return getInt("type");
	}

	/**
	 * 成功 0 失败 1
	 */
	public M setStatus(java.lang.Integer status) {
		set("status", status);
		return (M)this;
	}
	
	/**
	 * 成功 0 失败 1
	 */
	public java.lang.Integer getStatus() {
		return getInt("status");
	}

}
