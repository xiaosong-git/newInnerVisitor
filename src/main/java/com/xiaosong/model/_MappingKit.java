package com.xiaosong.model;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;

/**
 * Generated by JFinal, do not modify this file.
 * <pre>
 * Example:
 * public void configPlugin(Plugins me) {
 *     ActiveRecordPlugin arp = new ActiveRecordPlugin(...);
 *     _MappingKit.mapping(arp);
 *     me.add(arp);
 * }
 * </pre>
 */
public class _MappingKit {
	
	public static void mapping(ActiveRecordPlugin arp) {
		arp.addMapping("v_access_config", "id", VAccessConfig.class);
		arp.addMapping("v_ad_banner", "id", VAdBanner.class);
		arp.addMapping("v_app_checkindate", "id", VAppCheckindate.class);
		arp.addMapping("v_app_checkintime", "id", VAppCheckintime.class);
		arp.addMapping("v_app_menu", "id", VAppMenu.class);
		arp.addMapping("v_app_role", "id", VAppRole.class);
		arp.addMapping("v_app_role_menu", "id", VAppRoleMenu.class);
		arp.addMapping("v_app_user", "id", VAppUser.class);
		arp.addMapping("v_app_user_account", "id", VAppUserAccount.class);
		arp.addMapping("v_app_user_message", "id", VAppUserMessage.class);
		arp.addMapping("v_app_user_notice", "id", VAppUserNotice.class);
		arp.addMapping("v_app_version", "id", VAppVersion.class);
		arp.addMapping("v_app_yaoyue_record", "id", VAppYaoyueRecord.class);
		arp.addMapping("v_business", "id", VBusiness.class);
		arp.addMapping("v_comp_vip_user", "id", VCompVipUser.class);
		arp.addMapping("v_company", "id", VCompany.class);
		arp.addMapping("v_d_inout", "id", VDInout.class);
		arp.addMapping("v_d_user", "id", VDUser.class);
		arp.addMapping("v_dept", "id", VDept.class);
		arp.addMapping("v_dept_user", "id", VDeptUser.class);
		arp.addMapping("v_device", "id", VDevice.class);
		arp.addMapping("v_dictionaries", "id", VDictionaries.class);
		arp.addMapping("v_error_log", "id", VErrorLog.class);
		arp.addMapping("v_key", "id", VKey.class);
		arp.addMapping("v_kq_attendrule", "id", VKqAttendrule.class);
		arp.addMapping("v_local_auth", "id", VLocalAuth.class);
		arp.addMapping("v_news", "id", VNews.class);
		arp.addMapping("v_notice", "id", VNotice.class);
		arp.addMapping("v_org", "id", VOrg.class);
		arp.addMapping("v_params", "id", VParams.class);
		arp.addMapping("v_posp", "id", VPosp.class);
		arp.addMapping("v_sys_user", "id", VSysUser.class);
		arp.addMapping("v_user_auth", "id", VUserAuth.class);
		arp.addMapping("v_user_friend", "id", VUserFriend.class);
		arp.addMapping("v_user_key", "id", VUserKey.class);
		arp.addMapping("v_user_role", "id", VUserRole.class);
		arp.addMapping("v_user_role_auth", "id", VUserRoleAuth.class);
		arp.addMapping("v_visitor_record", "id", VVisitorRecord.class);
	}
}

