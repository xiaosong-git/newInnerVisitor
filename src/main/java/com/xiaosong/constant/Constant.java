package com.xiaosong.constant;

import com.jfinal.kit.PathKit;

/**
 * 
 * @author wgm
 *create by 2019-11-05
 * 系统常量
 */
public class Constant {
	
	
	/**
	 * 默认上传临时文件夹 PathKit.getWebRootPath() +"/upload/temp";
	 */
	public static final String BASE_UPLOAD_PATH = PathKit.getWebRootPath() +"/upload/temp";
	
	
	/**
	 * 默认下载临时文件夹 PathKit.getWebRootPath() +"/download/temp";
	 */
	public static final String BASE_DOWNLOAD_PATH = PathKit.getWebRootPath() +"/download/temp";

	public static final String DATE_FORMAT_DEFAULT ="yyyy-MM-dd HH:mm:ss";

	/**
	 * 账号密码管理session 放入缓存session中
	 */
	public final static String SYS_ACCOUNT = "SYS_ACCOUNT";
	/**
	 * 权限缓存也放入session
	 */
	public final static String SYS_ROLE_MENU = "SYS_ROLE_MENU";
	
	/**
	 * 字典表缓存key
	 */
	public final static String DICTIONARY_MENU = "DICTIONARY_MENU";
	
	/**
	 * 是否开发模式-生产是自动加载改为false即可，自动加载生产配置文件
	 */
	public final static Boolean DEV_MODE =  true;
	//表示 tbl_key中的密钥的正常状态
	public static  final  String KEY_STATUS_NORMAL = "normal";
	public static final int MSG_NOMAL=1;//普通消息
	public static final int MSG_VISITOR=2;//访客
	public static final int MSG_REPLY=3;//回应邀请
	public static final String DB="db";//redis库别名
	public static final Integer VISITOR=1;//访问
	public static final Integer INVITE=2;//邀约信息
    public static final Integer MASSEGETYPE_REPLY = 3;
	public static final String DB40_PATH="D:\\HJKJ\\WIN_X64\\DB40";
	public static final String DB40_LINUX_PATH="/usr/local/src/DB40";
	public static final String APPLY_STATUS_NORMAL = "normal";
    public static final int RECORDTYPE_VISITOR = 1;
	public static final int RECORDTYPE_INVITE =2 ;
	public static final int MASSEGETYPE_VISITOR =2 ;

}
