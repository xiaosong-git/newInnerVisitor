package com.xiaosong.constant;

/**
 * @program: xiaosong
 * @description: 表名称
 * @author: cwf
 * @create: 2019-12-28 11:35
 **/
public class TableList {


    public static final String PARAM="v_params";//用户账户表
    public static final String KEY = " v_user_key";    //密钥表
    public static final String USER_NOTICE = "v_app_user_notice ";// 用户已推送的最大公告
    public static final String NOTICE = " v_notice ";    // 公告
    public static final String ORG = " v_org ";    // 大楼
    public static final String APP_MENU = " v_app_menu ";//app菜单
    public static final String APP_ROLE = " v_app_role ";//app角色
    public static final String APP_ROLE_MENU= "v_app_role_menu"; //APP角色菜单关系
    public static final String USER_FRIEND= "v_user_friend"; //APP角色菜单关系
    public static final String DEPT_USER= "v_dept_user"; //原公司员工 现部门员工

    public static final String VISITOR_RECORD ="v_visitor_record ";//访问记录
    public static final String LOCAL_AUTH ="v_local_auth" ;
    public static final String APP_VERSION ="v_app_version" ;
    public static final String POSP = "v_posp";
    public static final String INOUT = "v_d_inout";
    public static final String AD_BANNER ="v_ad_banner" ;
    public static final String DEPT = "v_dept";
    public static final String NEWS = "v_news";//新闻
    public static final String OUT_VISIT = "v_out_visitor";//新闻
    public static final String USER_MESSAGE = "v_app_user_message";
    //-----------考勤---------------
    public static final String WK_GROUP = "wk_group";//考勤规则
    public static final String WK_DATE_TIME_RLAT = "wk_date_time_rlat";//时间关系表
    public static final String WK_CHECKINDATE = "wk_checkindate";//打卡日期
    public static final String WK_CHECKINTIME = "wk_checkintime";//打卡时间
    public static final String WK_LOC_INFOS = "wk_loc_infos";
    public static final String WK_USER_GROUP_RLAT ="wk_user_group_rlat" ;
    public static final String WK_WHITE_LIST = "wk_white_list";
    public static final String WK_SPE_DAYS_TIME_RLAT ="wk_spe_days_time_rlat" ;
    public static final String WK_SPE_DAYS ="wk_spe_days" ;
    public static final String WK_RECORD = "wk_record";
    public static final String WK_DAY_STATISTICS = "wk_day_statistics";
}
