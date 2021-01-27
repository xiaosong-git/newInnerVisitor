package com.xiaosong.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseActReDeployment<M extends BaseActReDeployment<M>> extends Model<M> implements IBean {

	public M setId(java.lang.String id) {
		set("ID_", id);
		return (M)this;
	}
	
	public java.lang.String getId() {
		return getStr("ID_");
	}

	public M setName(java.lang.String name) {
		set("NAME_", name);
		return (M)this;
	}
	
	public java.lang.String getName() {
		return getStr("NAME_");
	}

	public M setCategory(java.lang.String category) {
		set("CATEGORY_", category);
		return (M)this;
	}
	
	public java.lang.String getCategory() {
		return getStr("CATEGORY_");
	}

	public M setTenantId(java.lang.String tenantId) {
		set("TENANT_ID_", tenantId);
		return (M)this;
	}
	
	public java.lang.String getTenantId() {
		return getStr("TENANT_ID_");
	}

	public M setDeployTime(java.util.Date deployTime) {
		set("DEPLOY_TIME_", deployTime);
		return (M)this;
	}
	
	public java.util.Date getDeployTime() {
		return get("DEPLOY_TIME_");
	}

}
