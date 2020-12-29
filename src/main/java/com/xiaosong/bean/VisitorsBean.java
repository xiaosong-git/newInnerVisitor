package com.xiaosong.bean;

/**
 * Created by CNL on 2020/8/19.
 */
public class VisitorsBean {

    /**
     * 来访人
     */
    private String userName;
    /**
     * 受访人
     */
    private String visitorName;
    /**
     * 申请时间
     */
    private String visitDateTime;
    /**
     * 访问时段
     */
    private String visitTimePeriod;
    /**
     * 状态
     */
    private String cstatusName;
    /**
     * 进入时间
     */
    private String inTime;
    /**
     * 离开时间
     */
    private String outTime;

    private String trueName;

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getVisitDateTime() {
        return visitDateTime;
    }

    public void setVisitDateTime(String visitDateTime) {
        this.visitDateTime = visitDateTime;
    }

    public String getVisitTimePeriod() {
        return visitTimePeriod;
    }

    public void setVisitTimePeriod(String visitTimePeriod) {
        this.visitTimePeriod = visitTimePeriod;
    }

    public String getCstatusName() {
        return cstatusName;
    }

    public void setCstatusName(String cstatusName) {
        this.cstatusName = cstatusName;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }
}




