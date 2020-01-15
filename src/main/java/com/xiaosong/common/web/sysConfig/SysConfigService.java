package com.xiaosong.common.web.sysConfig;

import com.jfinal.plugin.activerecord.Page;
import com.xiaosong.model.VSysConfig;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月14日 下午4:27:36 
* 类说明 
*/
public class SysConfigService {
	public static final	SysConfigService me = new SysConfigService();
	
	public Page<VSysConfig> findList(int currentPage,int pageSize){
		return VSysConfig.dao.paginate(currentPage, pageSize, "select *", "from v_sys_config");
	}
	
	public boolean addSysConfig(VSysConfig config) {
		return config.save();
	}
	
	public boolean editSysConfig(VSysConfig config) {
		return config.update();
	}
	
	public boolean deleteSysConfig(Long id) {
		return VSysConfig.dao.deleteById(id);
	}
}
