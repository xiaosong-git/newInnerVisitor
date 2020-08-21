package com.xiaosong.bean;

/**
 * Created by CNL on 2020/8/19.
 */
public class InOutBean {

    /**
     * 序号
     */
    private Long id;
    /**
     * 通行日期
     */
    private String scanDate;
    /**
     * 通行时间
     */
    private String scanTime;
    /**
     * 姓名
     */
    private String userName;
    /**
     * 人员类型
     */
    private String userType;
    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 设备IP
     */
    private String deviceIp;

    /**
     * 进出类型
     */
    private String inOrOut;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScanDate() {
        return scanDate;
    }

    public void setScanDate(String scanDate) {
        this.scanDate = scanDate;
    }

    public String getScanTime() {
        return scanTime;
    }

    public void setScanTime(String scanTime) {
        this.scanTime = scanTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }

    public String getInOrOut() {
        return inOrOut;
    }

    public void setInOrOut(String inOrOut) {
        this.inOrOut = inOrOut;
    }
}




