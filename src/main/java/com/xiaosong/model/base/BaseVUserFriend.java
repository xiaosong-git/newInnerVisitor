package com.xiaosong.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseVUserFriend<M extends BaseVUserFriend<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	/**
	 * 用户id
	 */
	public M setUserId(java.lang.Long userId) {
		set("userId", userId);
		return (M)this;
	}
	
	/**
	 * 用户id
	 */
	public java.lang.Long getUserId() {
		return getLong("userId");
	}

	/**
	 * 用户对应的好友id
	 */
	public M setFriendId(java.lang.Long friendId) {
		set("friendId", friendId);
		return (M)this;
	}
	
	/**
	 * 用户对应的好友id
	 */
	public java.lang.Long getFriendId() {
		return getLong("friendId");
	}

	/**
	 * 1--通过 0,null---不通过 2已删除
	 */
	public M setApplyType(java.lang.Integer applyType) {
		set("applyType", applyType);
		return (M)this;
	}
	
	/**
	 * 1--通过 0,null---不通过 2已删除
	 */
	public java.lang.Integer getApplyType() {
		return getInt("applyType");
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