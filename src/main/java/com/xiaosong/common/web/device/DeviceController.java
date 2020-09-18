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
	/**
	 * 	上位机管理列表
	 *
	 */
	public void findWincc(){
		int currentPage = getInt("currentPage");
		int pageSize = getInt("pageSize");
		String winccName =get("winccName");
		Page<Record> pagelist = srv.findWinccList(winccName,currentPage,pageSize);
		renderJson(pagelist);
	}

	public void editWincc(){
		String id =get("id");
		VDevice device = VDevice.dao.findById(id);

		String org_code =get("org_code")=="" ||get("org_code")==null ? device.getGate() : get("org_code");
		String device_name =get("device_name")==""||get("device_name")==null ? device.getDeviceName() : get("device_name");
		String ip =get("ip")=="" ||get("ip")==null? device.getIp() : get("ip");
		String status =get("status")=="" ||get("status")==null? device.getStatus() : get("status");

		device.setDeviceName(device_name);
		device.setGate(org_code);
		device.setIp(ip);
		device.setStatus(status);
		boolean isSuccess = device.update();
		if(isSuccess){
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}

	}

	public void delWincc(){
		String id =get("id");
		VDevice device = VDevice.dao.findById(id);
		if(device != null){
			boolean del = device.delete();
			if(del){
				renderJson(RetUtil.ok());
			}else{
				renderJson(RetUtil.fail());
			}
		}else{
			renderJson(RetUtil.fail());
		}
	}

	public void addWincc(){
		String ip = getPara("ip");
		String org_code = getPara("org_code");
		String device_name = getPara("device_name");
		String status = getPara("status");
		String type = getPara("type");
		VDevice device = getModel(VDevice.class);
		device.setIp(ip);
		device.setGate(org_code);
		device.setDeviceName(device_name);
		device.setStatus(status);
		device.setType(type);
		boolean save = device.save();
		if(save){
			renderJson(RetUtil.ok());
		}else{
			renderJson(RetUtil.fail());
		}
	}
}
