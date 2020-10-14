package com.xiaosong.common.web.sysn;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.model.VSync;
import com.xiaosong.model.VUserAuth;
import com.xiaosong.model.VUserRoleAuth;
import com.xiaosong.util.MenuUtil;
import com.xiaosong.util.TreeUtil;

import java.util.ArrayList;
import java.util.List;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月13日 下午5:13:22 
* 类说明 
*/
public class SysnService {

	public static final SysnService me = new SysnService();

	private void setIsReceiveF(Long realtionId,String type) {
		Db.delete("delete from v_sync where relationId = ? and type=?",realtionId,type);
	}

	public void setStaffIsReceiveF(Long realtionId) {
		setIsReceiveF(realtionId,"staff");
	}

	public void setVisitorIsReceiveF(Long realtionId) {
		setIsReceiveF(realtionId,"visitor");
	}
	
}
