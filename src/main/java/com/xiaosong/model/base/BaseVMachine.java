package com.xiaosong.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseVMachine<M extends BaseVMachine<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	/**
	 * 设备名
	 */
	public M setMachineName(java.lang.String machineName) {
		set("machine_name", machineName);
		return (M)this;
	}
	
	/**
	 * 设备名
	 */
	public java.lang.String getMachineName() {
		return getStr("machine_name");
	}

	/**
	 * IP地址
	 */
	public M setIp(java.lang.String ip) {
		set("ip", ip);
		return (M)this;
	}
	
	/**
	 * IP地址
	 */
	public java.lang.String getIp() {
		return getStr("ip");
	}

	/**
	 * 设备编号
	 */
	public M setMachineCode(java.lang.String machineCode) {
		set("machine_code", machineCode);
		return (M)this;
	}
	
	/**
	 * 设备编号
	 */
	public java.lang.String getMachineCode() {
		return getStr("machine_code");
	}

	/**
	 * 设备类型（SWJ,QRCODE,RELAY,FACE）
	 */
	public M setType(java.lang.String type) {
		set("type", type);
		return (M)this;
	}
	
	/**
	 * 设备类型（SWJ,QRCODE,RELAY,FACE）
	 */
	public java.lang.String getType() {
		return getStr("type");
	}

	/**
	 * 所在楼宇
	 */
	public M setOrgCode(java.lang.String orgCode) {
		set("org_code", orgCode);
		return (M)this;
	}
	
	/**
	 * 所在楼宇
	 */
	public java.lang.String getOrgCode() {
		return getStr("org_code");
	}

	/**
	 * 控制的楼层
	 */
	public M setFloors(java.lang.String floors) {
		set("floors", floors);
		return (M)this;
	}
	
	/**
	 * 控制的楼层
	 */
	public java.lang.String getFloors() {
		return getStr("floors");
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

	public M setPid(java.lang.Long pid) {
		set("pid", pid);
		return (M)this;
	}
	
	public java.lang.Long getPid() {
		return getLong("pid");
	}

	public M setStatus(java.lang.String status) {
		set("status", status);
		return (M)this;
	}
	
	public java.lang.String getStatus() {
		return getStr("status");
	}

	public M setPing(java.lang.Integer ping) {
		set("ping", ping);
		return (M)this;
	}
	
	public java.lang.Integer getPing() {
		return getInt("ping");
	}

}