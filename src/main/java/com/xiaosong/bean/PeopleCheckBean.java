package com.xiaosong.bean;

/**
 * @Author: gx
 * @Date: 2021/1/29 9:35
 * @Description: CTID人员核查列表
*/
public class PeopleCheckBean {

    /** 用户id */
    private Long id;

    /** 姓名 */
    private String realName;

    /** 身份证号码 */
    private String idNO;

    /** 核查时间 */
    private String authDate;

    /** 认证结果：T、F */
    private String isAuth = "F";

    /** 是否重点关注：T、F（目前默认F） */
    private String isFocus = "F";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdNO() {
        return idNO;
    }

    public void setIdNO(String idNO) {
        this.idNO = idNO;
    }

    public String getAuthDate() {
        return authDate;
    }

    public void setAuthDate(String authDate) {
        this.authDate = authDate;
    }

    public String getIsAuth() {
        return isAuth;
    }

    public void setIsAuth(String isAuth) {
        this.isAuth = isAuth;
    }

    public String getIsFocus() {
        return isFocus;
    }

    public void setIsFocus(String isFocus) {
        this.isFocus = isFocus;
    }
}
