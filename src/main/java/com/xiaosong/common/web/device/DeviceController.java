package com.xiaosong.common.web.device;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.model.VDept;
import com.xiaosong.model.VDevice;
import com.xiaosong.util.RetUtil;

import java.util.UUID;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月14日 下午8:26:14 
* 类说明 
*/
public class DeviceController extends Controller{
	private Log log = Log.getLog(DeviceController.class);
	public DeviceService srv = DeviceService.me;
	
	public void findList() {
		int currentPage = getInt("currentPage");
		int pageSize = getInt("pageSize");
		String ip =get("ip");
		String status =get("status");
		String type = get("type");
		String gate = get("gate");
		Page<Record> pagelist = srv.findList(ip,status,type,gate,currentPage,pageSize);
		renderJson(pagelist);
	}
	

	public void delDept() {
		Long id = getLong("id");
		boolean bool = srv.deleteDevice(id);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
}
