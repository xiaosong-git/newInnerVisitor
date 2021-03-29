package com.xiaosong.common.api.code;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by CNL on 2021-01-30.
 */
public class CodeMsg {

    //ali短信code 通行申请
    public static final String MSG_STAFF_APPROVE_CODE = "SMS_213692575";
    public static final String MSG_STAFF_APPROVE = " 您有一条人员访客通行申请待审核，请及时处理！";

    //ali短信code 审核通过
    public static final String MSG_VISITOR_PASS_CODE = "SMS_213677643";
    public static final String MSG_VISITOR_PASS = " 您的访问申请已审核通过，请您在申请时间内通行！";

    //ali短信code 审核不ton过
    public static final String MSG_VISITOR_NOPASS_CODE = "SMS_213742609";
    public static final String MSG_VISITOR_NOPASS = "您的访问申请审核不通过，详情请登录江西机关事务APP！";

    //ali短信code 车辆访客通行申请待审核
    public static final String MSG_CAR_APPROVE_CODE = "SMS_213772547";
    public static final String MSG_CAR_APPROVE = " 您有一条车辆访客通行申请待审核，请及时处理！";
    //ali短信code 车辆访客通行申请已审批通过
    public static final String MSG_CAR_APPROVE_PASS_CODE = "SMS_213677647";
    public static final String MSG_CAR_APPROVE_PASS = " 您提交的车辆访客通行申请已审批通过！";

    //ali短信code 车辆审批未通过
    public static final String MSG_CAR_APPROVE_NOPASS_CODE = "SMS_213692581";
    public static final String MSG_CAR_APPROVE_NOPASS = " 您提交的车辆访客通行申请审批未通过，详情请登录江西机关事务APP！";

    //ali短信code 车辆通行申请已审核通过
    public static final String MSG_CAR_PASS_CODE = "SMS_213742612";
    public static final String MSG_CAR_PASS = "您的车辆通行申请已审核通过，请您从申请的出入口通行！";

    //ali短信code
    public static final String MSG_CAR_NOPASS_CODE = "SMS_213692584";
    public static final String MSG_CAR_NOPASS = "您的车辆通行申请审核未通过，请您重新预约，详情请登录江西机关事务APP！";

    public static final Map<String, String> MAP;

    static {
        MAP=new HashMap<>();
        MAP.put(MSG_STAFF_APPROVE,MSG_STAFF_APPROVE_CODE);
        MAP.put(MSG_VISITOR_PASS,MSG_VISITOR_PASS_CODE);
        MAP.put(MSG_VISITOR_NOPASS,MSG_VISITOR_NOPASS_CODE);
        MAP.put(MSG_CAR_APPROVE,MSG_CAR_APPROVE_CODE);
        MAP.put(MSG_CAR_APPROVE_PASS,MSG_CAR_APPROVE_PASS_CODE);
        MAP.put(MSG_CAR_APPROVE_NOPASS,MSG_CAR_APPROVE_NOPASS_CODE);
        MAP.put(MSG_CAR_PASS,MSG_CAR_PASS_CODE);
        MAP.put(MSG_CAR_NOPASS,MSG_CAR_NOPASS_CODE);
    }

}
