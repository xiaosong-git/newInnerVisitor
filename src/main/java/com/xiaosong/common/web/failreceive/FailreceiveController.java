package com.xiaosong.common.web.failreceive;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.web.device.DeviceService;
import com.xiaosong.model.VDevice;
import com.xiaosong.util.RetUtil;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月14日 下午8:26:14 
* 类说明 
*/
public class FailreceiveController extends Controller{
	private Log log = Log.getLog(FailreceiveController.class);
	public FailreceiveService srv = FailreceiveService.me;
	
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


}
