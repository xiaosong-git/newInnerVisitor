package com.xiaosong.common.web.visitor;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.bean.VisitorsBean;
import com.xiaosong.common.web.appMenu.AppMenuService;
import com.xiaosong.common.web.sysConfig.SysConfigController;
import com.xiaosong.constant.Constant;
import com.xiaosong.constant.ErrorCodeDef;
import com.xiaosong.util.ExcelUtil;
import com.xiaosong.util.RetUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月18日 上午10:53:03 
* 类说明 
*/
public class VisitorsController extends Controller{
	private Log log = Log.getLog(SysConfigController.class);
	public VisitorService srv = VisitorService.me;
	public AppMenuService menuservice = AppMenuService.me;
	public void findList() {
		String realName = getPara("realName");
		String endTime = getPara("endTime");
		String startTime = getPara("startTime");
		String visitorName = getPara("visitorName");
		String cStatus = getPara("cStatus");
		int currentPage = getInt("currentPage");
		int pageSize = getInt("pageSize");
		Page<Record> pagelist = srv.findList(realName, visitorName, startTime, endTime, cStatus,currentPage,pageSize);
		renderJson(pagelist);
	}




	public void downReport() {

		String realName = getPara("realName");
		String endTime = getPara("endTime");
		String startTime = getPara("startTime");
		String visitorName = getPara("visitorName");
		String cStatus = getPara("cStatus");
		List<Record> recordList = srv.findList(realName, visitorName, startTime, endTime, cStatus);
		List outputList = new ArrayList<>();
		if (recordList != null && recordList.size() > 0) {
			// 生成文件并返回
			for (int i = 0; i < recordList.size(); i++) {
				Record record = recordList.get(i);
				VisitorsBean sd = new VisitorsBean();
				sd.setCstatusName(record.getStr("cstatusName"));
				sd.setInTime(record.getStr("inTime"));
				sd.setOutTime(record.getStr("outTime"));
				sd.setVisitTimePeriod(record.getStr("visitTimePeriod"));
				sd.setVisitDateTime(record.getStr("visitDateTime"));
				sd.setVisitorName(record.getStr("visitorName"));
				sd.setUserName(record.getStr("userName"));
				outputList.add(sd);
			}
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(new Date());
		String exportName = date + "_访客报表.xls";
		String exportPath = Constant.BASE_DOWNLOAD_PATH;
		File exportFile = new File(exportPath + "/" + exportName);
		if(exportFile.exists()){
			exportFile.delete();
			try {
				exportFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String[] title = {  "来访人", "受访人", "申请时间","访问时段", "状态", "进入时间","离开时间"};
		byte[] data = ExcelUtil.export("访客报表", title, outputList);
		try {
			FileUtils.writeByteArrayToFile(exportFile, data, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		renderFile(exportFile);
	}

}
