package com.xiaosong.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseVAccessConfig<M extends BaseVAccessConfig<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	/**
	 * 通行方式(QRCODE:FACE)
	 */
	public M setPassMethod(java.lang.String passMethod) {
		set("pass_method", passMethod);
		return (M)this;
	}
	
	/**
	 * 通行方式(QRCODE:FACE)
	 */
	public java.lang.String getPassMethod() {
		return getStr("pass_method");
	}

	/**
	 * 产品id
	 */
	public M setProductId(java.lang.Long productId) {
		set("product_id", productId);
		return (M)this;
	}
	
	/**
	 * 产品id
	 */
	public java.lang.Long getProductId() {
		return getLong("product_id");
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

}
