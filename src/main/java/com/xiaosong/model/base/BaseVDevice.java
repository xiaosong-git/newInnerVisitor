package com.xiaosong.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseVDevice<M extends BaseVDevice<M>> extends Model<M> implements IBean {

	public M setId(Long id) {
		set("id", id);
		return (M)this;
	}

	public Long getId() {
		return getLong("id");
	}

	/**
	 * 设备名
	 */
	public M setDeviceName(String deviceName) {
		set("device_name", deviceName);
		return (M)this;
	}

	/**
	 * 设备名
	 */
	public String getDeviceName() {
		return getStr("device_name");
	}

	/**
	 * IP地址
	 */
	public M setIp(String ip) {
		set("ip", ip);
		return (M)this;
	}

	/**
	 * IP地址
	 */
	public String getIp() {
		return getStr("ip");
	}

	/**
	 * 设备类型（SWJ,QRCODE,RELAY,FACE）
	 */
	public M setType(String type) {
		set("type", type);
		return (M)this;
	}

	/**
	 * 设备类型（SWJ,QRCODE,RELAY,FACE）
	 */
	public String getType() {
		return getStr("type");
	}

	/**
	 * 控制的闸机
	 */
	public M setGate(String gate) {
		set("gate", gate);
		return (M)this;
	}

	/**
	 * 控制的闸机
	 */
	public String getGate() {
		return getStr("gate");
	}

	/**
	 * 控制的楼层
	 */
	public M setFloors(String floors) {
		set("floors", floors);
		return (M)this;
	}

	/**
	 * 控制的楼层
	 */
	public String getFloors() {
		return getStr("floors");
	}

	/**
	 * 创建时间
	 */
	public M setCreatetime(String createtime) {
		set("createtime", createtime);
		return (M)this;
	}

	/**
	 * 创建时间
	 */
	public String getCreatetime() {
		return getStr("createtime");
	}

	public M setExtra1(String extra1) {
		set("extra1", extra1);
		return (M)this;
	}

	public String getExtra1() {
		return getStr("extra1");
	}

	public M setExtra2(String extra2) {
		set("extra2", extra2);
		return (M)this;
	}

	public String getExtra2() {
		return getStr("extra2");
	}

	public M setExtra3(String extra3) {
		set("extra3", extra3);
		return (M)this;
	}

	public String getExtra3() {
		return getStr("extra3");
	}

}
