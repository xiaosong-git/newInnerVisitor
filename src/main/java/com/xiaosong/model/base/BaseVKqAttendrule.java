package com.xiaosong.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseVKqAttendrule<M extends BaseVKqAttendrule<M>> extends Model<M> implements IBean {

	/**
	 * 考勤规则
	 */
	public M setId(Long id) {
		set("id", id);
		return (M)this;
	}

	/**
	 * 考勤规则
	 */
	public Long getId() {
		return getLong("id");
	}

	/**
	 * 公司ID
	 */
	public M setCompanyID(Long companyID) {
		set("companyID", companyID);
		return (M)this;
	}

	/**
	 * 公司ID
	 */
	public Long getCompanyID() {
		return getLong("companyID");
	}

	/**
	 * 大楼编码
	 */
	public M setOrgCode(String orgCode) {
		set("orgCode", orgCode);
		return (M)this;
	}

	/**
	 * 大楼编码
	 */
	public String getOrgCode() {
		return getStr("orgCode");
	}

	/**
	 * 打卡次数
	 */
	public M setPunchCount(Integer punchCount) {
		set("punchCount", punchCount);
		return (M)this;
	}

	/**
	 * 打卡次数
	 */
	public Integer getPunchCount() {
		return getInt("punchCount");
	}

	/**
	 * 早上上班时间
	 */
	public M setMornTime(String mornTime) {
		set("mornTime", mornTime);
		return (M)this;
	}

	/**
	 * 早上上班时间
	 */
	public String getMornTime() {
		return getStr("mornTime");
	}

	/**
	 * 最早打卡时间
	 */
	public M setMornOffTime(String mornOffTime) {
		set("mornOffTime", mornOffTime);
		return (M)this;
	}

	/**
	 * 最早打卡时间
	 */
	public String getMornOffTime() {
		return getStr("mornOffTime");
	}

	/**
	 * 提前多长时间不算早退
	 */
	public M setAfterTime(String afterTime) {
		set("afterTime", afterTime);
		return (M)this;
	}

	/**
	 * 提前多长时间不算早退
	 */
	public String getAfterTime() {
		return getStr("afterTime");
	}

	/**
	 * 下午下班时间
	 */
	public M setAfterOffTime(String afterOffTime) {
		set("afterOffTime", afterOffTime);
		return (M)this;
	}

	/**
	 * 下午下班时间
	 */
	public String getAfterOffTime() {
		return getStr("afterOffTime");
	}

	/**
	 * 晚上最迟打卡时间
	 */
	public M setMiniOverTime(String miniOverTime) {
		set("miniOverTime", miniOverTime);
		return (M)this;
	}

	/**
	 * 晚上最迟打卡时间
	 */
	public String getMiniOverTime() {
		return getStr("miniOverTime");
	}

	/**
	 * 迟到多长时间算迟到
	 */
	public M setLateTime(String lateTime) {
		set("lateTime", lateTime);
		return (M)this;
	}

	/**
	 * 迟到多长时间算迟到
	 */
	public String getLateTime() {
		return getStr("lateTime");
	}

	/**
	 * 考勤备注
	 */
	public M setAttendNote(String attendNote) {
		set("attendNote", attendNote);
		return (M)this;
	}

	/**
	 * 考勤备注
	 */
	public String getAttendNote() {
		return getStr("attendNote");
	}

	/**
	 * 操作时间
	 */
	public M setOperTime(String operTime) {
		set("operTime", operTime);
		return (M)this;
	}

	/**
	 * 操作时间
	 */
	public String getOperTime() {
		return getStr("operTime");
	}

	/**
	 * 工作日工时
	 */
	public M setWorkHours(String workHours) {
		set("workHours", workHours);
		return (M)this;
	}

	/**
	 * 工作日工时
	 */
	public String getWorkHours() {
		return getStr("workHours");
	}

	/**
	 * 工作日几点打卡算加班
	 */
	public M setWorkOverTime(String workOverTime) {
		set("workOverTime", workOverTime);
		return (M)this;
	}

	/**
	 * 工作日几点打卡算加班
	 */
	public String getWorkOverTime() {
		return getStr("workOverTime");
	}

	/**
	 * 节假日工时
	 */
	public M setHolidayHours(String holidayHours) {
		set("holidayHours", holidayHours);
		return (M)this;
	}

	/**
	 * 节假日工时
	 */
	public String getHolidayHours() {
		return getStr("holidayHours");
	}

	/**
	 * 节假日几点打卡算加班
	 */
	public M setHolidayOverTime(String holidayOverTime) {
		set("holidayOverTime", holidayOverTime);
		return (M)this;
	}

	/**
	 * 节假日几点打卡算加班
	 */
	public String getHolidayOverTime() {
		return getStr("holidayOverTime");
	}

	/**
	 * 可允许最晚打卡时间上午
	 */
	public M setExt1(String ext1) {
		set("ext1", ext1);
		return (M)this;
	}

	/**
	 * 可允许最晚打卡时间上午
	 */
	public String getExt1() {
		return getStr("ext1");
	}

	/**
	 * 可允许最晚打卡时间中午
	 */
	public M setExt2(String ext2) {
		set("ext2", ext2);
		return (M)this;
	}

	/**
	 * 可允许最晚打卡时间中午
	 */
	public String getExt2() {
		return getStr("ext2");
	}

	/**
	 * 可允许最迟打卡时间
	 */
	public M setExt3(String ext3) {
		set("ext3", ext3);
		return (M)this;
	}

	/**
	 * 可允许最迟打卡时间
	 */
	public String getExt3() {
		return getStr("ext3");
	}

	/**
	 * 考勤类型0：门禁1：考勤
	 */
	public M setAttendType(String attendType) {
		set("attendType", attendType);
		return (M)this;
	}

	/**
	 * 考勤类型0：门禁1：考勤
	 */
	public String getAttendType() {
		return getStr("attendType");
	}

	/**
	 * 上午下班时间
	 */
	public M setMornCloseTime(String mornCloseTime) {
		set("mornCloseTime", mornCloseTime);
		return (M)this;
	}

	/**
	 * 上午下班时间
	 */
	public String getMornCloseTime() {
		return getStr("mornCloseTime");
	}

	/**
	 * 下午上班时间
	 */
	public M setAfternoonCloseTime(String afternoonCloseTime) {
		set("afternoonCloseTime", afternoonCloseTime);
		return (M)this;
	}

	/**
	 * 下午上班时间
	 */
	public String getAfternoonCloseTime() {
		return getStr("afternoonCloseTime");
	}

}
