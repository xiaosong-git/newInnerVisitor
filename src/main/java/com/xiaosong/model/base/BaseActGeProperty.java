package com.xiaosong.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseActGeProperty<M extends BaseActGeProperty<M>> extends Model<M> implements IBean {

	public M setName(java.lang.String name) {
		set("NAME_", name);
		return (M)this;
	}
	
	public java.lang.String getName() {
		return getStr("NAME_");
	}

	public M setValue(java.lang.String value) {
		set("VALUE_", value);
		return (M)this;
	}
	
	public java.lang.String getValue() {
		return getStr("VALUE_");
	}

	public M setRev(java.lang.Integer rev) {
		set("REV_", rev);
		return (M)this;
	}
	
	public java.lang.Integer getRev() {
		return getInt("REV_");
	}

}
