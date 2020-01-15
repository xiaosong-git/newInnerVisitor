package com.xiaosong.common.web.vipUser;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.model.VCompVipUser;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月14日 下午11:09:57 
* 类说明 
*/
public class VipUserService {
	public static final	VipUserService me = new VipUserService();
	
	public Page<VCompVipUser> findList(String tel, int currentPage, int pageSize){
		Page<Record> page = Db.paginate(currentPage, pageSize, "select *", "from v_comp_vip_user");
		if(tel!=null &&tel!="") {
			return VCompVipUser.dao.paginate(currentPage, pageSize, "select *", "from v_comp_vip_user where tel like CONCAT(?,'%')",tel);
		}
		return VCompVipUser.dao.paginate(currentPage, pageSize, "select *", "from v_comp_vip_user");
	}
	
	public boolean addVipUser(VCompVipUser config) {
		return config.save();
	}
	
	public boolean editVipUser(VCompVipUser config) {
		return config.update();
	}
	
	public boolean deleteVipUser(Long id) {
		return VCompVipUser.dao.deleteById(id);
	}
}
