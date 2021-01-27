package com.xiaosong.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseActEvtLog<M extends BaseActEvtLog<M>> extends Model<M> implements IBean {

	public M setLogNr(java.lang.Long logNr) {
		set("LOG_NR_", logNr);
		return (M)this;
	}
	
	public java.lang.Long getLogNr() {
		return getLong("LOG_NR_");
	}

	public M setType(java.lang.String type) {
		set("TYPE_", type);
		return (M)this;
	}
	
	public java.lang.String getType() {
		return getStr("TYPE_");
	}

	public M setProcDefId(java.lang.String procDefId) {
		set("PROC_DEF_ID_", procDefId);
		return (M)this;
	}
	
	public java.lang.String getProcDefId() {
		return getStr("PROC_DEF_ID_");
	}

	public M setProcInstId(java.lang.String procInstId) {
		set("PROC_INST_ID_", procInstId);
		return (M)this;
	}
	
	public java.lang.String getProcInstId() {
		return getStr("PROC_INST_ID_");
	}

	public M setExecutionId(java.lang.String executionId) {
		set("EXECUTION_ID_", executionId);
		return (M)this;
	}
	
	public java.lang.String getExecutionId() {
		return getStr("EXECUTION_ID_");
	}

	public M setTaskId(java.lang.String taskId) {
		set("TASK_ID_", taskId);
		return (M)this;
	}
	
	public java.lang.String getTaskId() {
		return getStr("TASK_ID_");
	}

	public M setTimeStamp(java.util.Date timeStamp) {
		set("TIME_STAMP_", timeStamp);
		return (M)this;
	}
	
	public java.util.Date getTimeStamp() {
		return get("TIME_STAMP_");
	}

	public M setUserId(java.lang.String userId) {
		set("USER_ID_", userId);
		return (M)this;
	}
	
	public java.lang.String getUserId() {
		return getStr("USER_ID_");
	}

	public M setData(byte[] data) {
		set("DATA_", data);
		return (M)this;
	}
	
	public byte[] getData() {
		return get("DATA_");
	}

	public M setLockOwner(java.lang.String lockOwner) {
		set("LOCK_OWNER_", lockOwner);
		return (M)this;
	}
	
	public java.lang.String getLockOwner() {
		return getStr("LOCK_OWNER_");
	}

	public M setLockTime(java.util.Date lockTime) {
		set("LOCK_TIME_", lockTime);
		return (M)this;
	}
	
	public java.util.Date getLockTime() {
		return get("LOCK_TIME_");
	}

	public M setIsProcessed(java.lang.Integer isProcessed) {
		set("IS_PROCESSED_", isProcessed);
		return (M)this;
	}
	
	public java.lang.Integer getIsProcessed() {
		return getInt("IS_PROCESSED_");
	}

}