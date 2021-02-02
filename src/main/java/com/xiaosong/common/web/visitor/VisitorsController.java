package com.xiaosong.common.web.visitor;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.bean.VisitorsBean;
import com.xiaosong.common.web.appMenu.AppMenuService;
import com.xiaosong.common.web.sysConfig.SysConfigController;
import com.xiaosong.constant.Constant;
import com.xiaosong.constant.ErrorCodeDef;
import com.xiaosong.util.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

	public void findList() {
		try {
			String userName = getPara("userName");
			String visitName = getPara("visitName");
			String startTime = getPara("startTime");
			String endTime = getPara("endTime");
			String visitDept = getPara("visitDept");

			int pageNum = getInt("pageNum");
			int pageSize = getInt("pageSize");
			Page<Record> pagelist = srv.findList(userName, visitName, startTime, endTime, visitDept,pageNum,pageSize);
			renderJson(RetUtil.okData(pagelist));
		} catch (Exception e) {
			log.error("错误信息：", e);
			renderJson(RetUtil.fail(e.getCause().getLocalizedMessage()));
		}
	}




	public void downReport() {

		OutputStream os = null;
		try {
			String userName = getPara("userName");
			String visitName = getPara("visitName");
			String startTime = getPara("startTime");
			String endTime = getPara("endTime");
			String visitDept = getPara("visitDept");
			//获取列表
			List<Record> downReportList = srv.downReport(userName, visitName, startTime, endTime, visitDept);

			if (downReportList != null && downReportList.size() > 0){

				String systemTimeFourteen = DateUtil.getSystemTimeFourteen();
				String[] fields = {"来访人姓名","受访人姓名","受访人所在部门","申请时间","访问时间","审核状态","审核人员","审核时间","进入园区时间","进入入口","离开时间","离开出口"};
				List<String> fieldsList = Arrays.asList(fields);

				HSSFWorkbook workbook = new HSSFWorkbook();
				HSSFSheet sheet = workbook.createSheet("访客大数据报表");

				//设置单元格行高，列宽
				sheet.setDefaultRowHeightInPoints(18);
				sheet.setDefaultColumnWidth(20);

				//标题
				HSSFRow rowTitle = sheet.createRow(0);
				HSSFCell cell = rowTitle.createCell(0);
				cell.setCellValue("访客大数据报表");
				sheet.addMergedRegion( new CellRangeAddress(0,0,0,fields.length -1));
				//设置表标题样式
				HSSFCellStyle cellStyle = ExcelUtil.createCellStyle(workbook, HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.ALIGN_CENTER, HSSFColor.SKY_BLUE.index, "新宋体", (short) 12, true);
				cell.setCellStyle(cellStyle);
				//创建字段栏目
				cellStyle = ExcelUtil.createCellStyle(workbook, HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.ALIGN_CENTER, HSSFColor.YELLOW.index, "新宋体", (short) 12, true);
				HSSFRow rowFiled = sheet.createRow(1);
				for (int i = 0; i < fieldsList.size(); i++){
					ExcelUtil.createCell(rowFiled,cellStyle,fieldsList.get(i),i);
				}

				HSSFRow row;
				int index = 2;
				cellStyle = ExcelUtil.createCellStyle(workbook, HSSFCellStyle.ALIGN_LEFT, HSSFCellStyle.ALIGN_CENTER, HSSFColor.WHITE.index, "新宋体", (short) 12, false);
				for (Record record : downReportList) {
					row = sheet.createRow(index);
					//来访人姓名
					ExcelUtil.createCell(row,cellStyle,record.get("userName"),0);
					//受访人姓名
					ExcelUtil.createCell(row,cellStyle,record.get("visitName"),1);
					//受访人所在部门
					ExcelUtil.createCell(row,cellStyle,record.get("visitDept"),2);
					//申请时间
					ExcelUtil.createCell(row,cellStyle,record.get("applyTime").toString(),3);
					//访问时间
					ExcelUtil.createCell(row,cellStyle,record.get("visitTime"),4);
					//审核状态
					ExcelUtil.createCell(row,cellStyle,record.get("cstatus"),5);
					//审核人员
					ExcelUtil.createCell(row,cellStyle,record.get("replyName"),6);
					//审核时间
					ExcelUtil.createCell(row,cellStyle,record.get("replyTime"),7);
					//进入园区时间
					ExcelUtil.createCell(row,cellStyle,record.get("inTime"),8);
					//进入入口
					ExcelUtil.createCell(row,cellStyle,record.get("inGate"),9);
					//离开时间
					ExcelUtil.createCell(row,cellStyle,record.get("outTime"),10);
					//离开出口
					ExcelUtil.createCell(row,cellStyle,record.get("outGate"),11);
					index++;
				}
				String fileName = String.format("访客大数据报表_%s.xls",systemTimeFourteen);
//				String fileNameUrl = Constant.BASE_DOWNLOAD_PATH;
                String fileNameUrl = "E:/newInnerVisitor/download/temp";
				File exportFile = new File(fileNameUrl);
				File file = new File(exportFile,fileName);
				if(!exportFile.exists()){
					exportFile.mkdirs();
					if (!file.exists()){
						file.createNewFile();
					}
				}else {
					if (!file.exists()){
						file.createNewFile();
					}
				}
				os = new FileOutputStream(file);
				workbook.write(os);
				os.flush();
				os.close();
				renderFile(file);
			}
		}catch (Exception e){
			log.error("错误信息：", e);
			renderJson(RetUtil.fail(e.getCause().getLocalizedMessage()));
		}finally {
			if (os != null){
				try {
					os.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}


	/**
	 * 实人认证接口
	 */
	public void checkAuth()
	{
		try {
			String idCard = get("idCard");
			String realName = get("realName");
			String imgBase64 = get("imgBase64");

			if(StringUtils.isBlank(idCard))
			{
				throw new Exception("证件号不能为空");
			}
			if(StringUtils.isBlank(realName))
			{
				throw new Exception("姓名不能为空");
			}
			if(StringUtils.isBlank(imgBase64))
			{
				throw new Exception("照片不能为空");
			}
			JSONObject photoResult = AuthUtil.auth(idCard, realName, imgBase64);
			if ("00000".equals(photoResult.getString("return_code"))) {  //实人认证
				renderJson(RetUtil.ok("实人认证通过"));
			}else{
				JSONObject errorData = photoResult.getJSONObject("data");
				throw new Exception(errorData.getString("resultMsg"));
			}
		}
		catch (Exception ex)
		{
			renderJson(RetUtil.fail("验证失败，"+ex.getMessage()));
		}
	}

	/**
	 * 访问
	 */
	public void interview()
	{
		try {
			String idCard = get("idCard");
			String realName = get("realName");
			String imgBase64 = get("imgBase64");

			if(StringUtils.isBlank(idCard))
			{
				throw new Exception("证件号不能为空");
			}
			if(StringUtils.isBlank(realName))
			{
				throw new Exception("姓名不能为空");
			}
			if(StringUtils.isBlank(imgBase64))
			{
				throw new Exception("照片不能为空");
			}
			JSONObject photoResult = AuthUtil.auth(idCard, realName, imgBase64);
			if ("00000".equals(photoResult.getString("return_code"))) {  //实人认证
				renderJson(RetUtil.ok("实人认证通过"));
			}else{
				JSONObject errorData = photoResult.getJSONObject("data");
				throw new Exception(errorData.getString("resultMsg"));
			}
		}
		catch (Exception ex)
		{
			renderJson(RetUtil.fail("验证失败，"+ex.getMessage()));
		}
	}




}
