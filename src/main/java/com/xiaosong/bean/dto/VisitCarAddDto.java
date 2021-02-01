package com.xiaosong.bean.dto;

/**
 * @program: newInnerVisitor
 * @description: 车辆访问dto
 * @author: cwf
 * @create: 2021-01-30 15:49
 **/
public class VisitCarAddDto {
    //车牌号
    private String plate;
    //来访人姓名
    private String userName;
    //来访人身份证
    private String idNO;
    //当前登录人员id
    private Long replyUserId;
    //访问开始时间
    private String startDate;
    //时长
    private Integer endDate;
    //访问结束时间
    private Integer inOutType;

    //访客手机号
    private String phone;
    //访客手机号
    private String gate; //访客手机号
    //原因
    private String reason="无";
    //原因
    private Integer num=0;



    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdNO() {
        return idNO;
    }

    public void setIdNO(String idNO) {
        this.idNO = idNO;
    }



    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(Long replyUserId) {
        this.replyUserId = replyUserId;
    }

    public String getGate() {
        return gate;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }


    public Integer getInOutType() {
        return inOutType;
    }

    public void setInOutType(Integer inOutType) {
        this.inOutType = inOutType;
    }

    public Integer getEndDate() {
        return endDate;
    }

    public void setEndDate(Integer endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
