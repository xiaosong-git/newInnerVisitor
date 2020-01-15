package com.xiaosong.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseVKey<M extends BaseVKey<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	public M setSwiCode(java.lang.String swiCode) {
		set("swi_code", swiCode);
		return (M)this;
	}
	
	public java.lang.String getSwiCode() {
		return getStr("swi_code");
	}

	/**
	 * license对应的设备类型
	 */
	public M setMac(java.lang.String mac) {
		set("mac", mac);
		return (M)this;
	}
	
	/**
	 * license对应的设备类型
	 */
	public java.lang.String getMac() {
		return getStr("mac");
	}

	public M setPrivateKey(java.lang.String privateKey) {
		set("private_key", privateKey);
		return (M)this;
	}
	
	public java.lang.String getPrivateKey() {
		return getStr("private_key");
	}

	public M setPublicKey(java.lang.String publicKey) {
		set("public_key", publicKey);
		return (M)this;
	}
	
	public java.lang.String getPublicKey() {
		return getStr("public_key");
	}

	public M setStatus(java.lang.String status) {
		set("status", status);
		return (M)this;
	}
	
	public java.lang.String getStatus() {
		return getStr("status");
	}

	public M setEndtime(java.lang.String endtime) {
		set("endtime", endtime);
		return (M)this;
	}
	
	public java.lang.String getEndtime() {
		return getStr("endtime");
	}

	public M setBegintime(java.lang.String begintime) {
		set("begintime", begintime);
		return (M)this;
	}
	
	public java.lang.String getBegintime() {
		return getStr("begintime");
	}

	public M setCreatetime(java.lang.String createtime) {
		set("createtime", createtime);
		return (M)this;
	}
	
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

	public M setLicense(java.lang.String license) {
		set("license", license);
		return (M)this;
	}
	
	public java.lang.String getLicense() {
		return getStr("license");
	}

}
