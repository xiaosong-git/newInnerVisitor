package com.xiaosong.common.web.deptUser;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.json.Json;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.xiaosong.MainConfig;
import com.xiaosong.common.api.websocket.WebSocketMonitor;
import com.xiaosong.common.api.websocket.WebSocketSyncData;
import com.xiaosong.compose.Result;
import com.xiaosong.model.VDept;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月14日 下午8:27:07 
* 类说明 
*/
public class DeptUsersController extends Controller{
	private Log log = Log.getLog(DeptUsersController.class);
	public DeptUserService srv = DeptUserService.me;
	private String imgServerUrl = MainConfig.p.get("imgServerUrl");//图片服务地址

	public void findList() {
		String realName = getPara("realName");
		String dept = getPara("dept");
		int currentPage = getInt("currentPage");
		int pageSize = getInt("pageSize");
		Page<Record> pagelist = srv.findList(realName,dept,currentPage,pageSize);
		List<Record> recordList = pagelist.getList();
		Record user_key = Db.findFirst("select * from v_user_key");
		for(Record record : recordList)
		{
			String idNo =record.get("idNO");
			idNo = DESUtil.decode(user_key.getStr("workKey"), idNo);
			record.set("idNO",idNo);
		}
		renderJson(pagelist);
	}
	
	public void addDeptUser() throws Exception {
		String realName = getPara("realName");
		String userNo = getPara("userNo");
		String sex = getPara("sex");
		Long deptId = getLong("deptId");
		String idNO = getPara("idNO");
		String phone = getPara("phone");
		String intime = getPara("intime");
		String addr = getPara("addr");
		String remark = getPara("remark");
		String imgName = getPara("idHandleImgUrl");
		String carNo = getPara("carNo");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createtime = df.format(new Date());
		VDeptUser deptUser = getModel(VDeptUser.class);
		deptUser.setRealName(realName);
		deptUser.setCreateDate(createtime);
		deptUser.setSex(sex);
		deptUser.setPhone(phone);
		deptUser.setUserNo(userNo);
		deptUser.setDeptId(deptId);
		deptUser.setIdNO(idNO);
		deptUser.setIntime(intime);
		deptUser.setAddr(addr);
		deptUser.setRemark(remark);
		deptUser.setIdHandleImgUrl(imgName);
		deptUser.setStatus("applySuc");
		deptUser.setCurrentStatus("normal");
		deptUser.setCardNO(carNo);
		if(imgName!=null && !"cache".equals(imgName)) {
			String photoPath = File.separator + "user" + File.separator + "cache" + File.separator + imgName;
			String cahceImgUrl = imgServerUrl + photoPath;
			deptUser.setIdHandleImgUrl(photoPath);
			String photo = com.xiaosong.util.Base64.encode(FilesUtils.compressUnderSize(FilesUtils.getImageFromNetByUrl(cahceImgUrl), 40960L));
			JSONObject photoResult = AuthUtil.auth(idNO, realName, photo);
			//实人认证
			if ("00000".equals(photoResult.getString("return_code"))) {
				deptUser.setIsAuth("T");
			}
		}
		boolean bool = srv.addDeptUser(deptUser);
		if (bool) {
			//websocket通知前端获取用户数量
			WebSocketMonitor.me.getPersonNum();
			WebSocketSyncData.me.sendStaffData();
			renderJson(RetUtil.ok());
		} else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void editDeptUser() throws Exception{
		long id = getLong("id");
		String realName = getPara("realName");
		String userNo = getPara("userNo");
		String sex = getPara("sex");
		Long deptId = getLong("deptId");
		String idNO = getPara("idNO");
		String phone = getPara("phone");
		String intime = getPara("intime");
		String addr = getPara("addr");
		String remark = getPara("remark");
		String imgName = getPara("idHandleImgUrl");
		String carNo = getPara("carNo");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createtime = df.format(new Date());
		VDeptUser deptUser = VDeptUser.dao.findById(id);
		deptUser.setRealName(realName);
		deptUser.setCreateDate(createtime);
		deptUser.setSex(sex);
		deptUser.setPhone(phone);
		deptUser.setUserNo(userNo);
		deptUser.setDeptId(deptId);
		deptUser.setIdNO(idNO);
		deptUser.setIntime(intime);
		deptUser.setAddr(addr);
		deptUser.setRemark(remark);
		deptUser.setId(id);
		deptUser.setStatus("applySuc");
		deptUser.setCurrentStatus("normal");
		deptUser.setCardNO(carNo);

		if(imgName!=null && !"cache".equals(imgName))
		{
			String photoPath = File.separator+"user"+File.separator+"cache"+File.separator+imgName;
			String cahceImgUrl = imgServerUrl+photoPath;
			deptUser.setIdHandleImgUrl(photoPath);
			String photo = com.xiaosong.util.Base64.encode(FilesUtils.compressUnderSize(FilesUtils.getImageFromNetByUrl(cahceImgUrl), 40960L));
			JSONObject photoResult = AuthUtil.auth(idNO,realName,photo);
			if ("00000".equals(photoResult.getString("return_code"))) {  //实人认证
				deptUser.setIsAuth("T");
				deptUser.setIsReceive("F");
			}
		}
		boolean bool = srv.editDeptUser(deptUser);
		if (bool) {
			//websocket通知前端获取用户数量
			WebSocketMonitor.me.getPersonNum();
			WebSocketSyncData.me.sendStaffData();
			renderJson(RetUtil.ok());
		} else {
			renderJson(RetUtil.fail());
		}

	}
	
	public void delDeptUser() {
		Long id = getLong("id");
		VDeptUser user = VDeptUser.dao.findById(id);
        user.setCurrentStatus("deleted");
		user.setIsReceive("F");
		boolean bool =user.update();
		if(bool) {
			//websocket通知前端获取用户数量
			WebSocketMonitor.me.getPersonNum();
			WebSocketSyncData.me.sendStaffData();
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void uploadUser() {
		boolean bool = srv.uploadDeptUser(getFile("file"));
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}


	/**
	 * 批量导入
	 *
	 * @throws Exception
	 */
	public void imports() throws Exception {
			List errList = new ArrayList();
			UploadFile uploadFile = getFile();
			File file = uploadFile.getFile();
			int total_count = 0;
			int succ_count = 0;
			int err_count = 0;
			Map resultMap = new HashMap<>();
			if (!file.exists()) {
				renderJson(RetUtil.fail("文件接收失败"));
			} else {
				Workbook book = null;
				if (file.getName().endsWith(".xls")) {
					book = new HSSFWorkbook(new FileInputStream(file));
				} else if (file.getName().endsWith(".xlsx")) {
					book = new XSSFWorkbook(new FileInputStream(file));
				}
				Sheet sheet = book.getSheetAt(0);
				// 里面有多少行
				int rows = sheet.getPhysicalNumberOfRows();
				total_count = rows - 1;
				for (int i = 1; i <= rows - 1; i++) {// 从1开始是去除首行-头
					Map errMap = new HashMap<>();
					try {
						Row row = sheet.getRow(i);// 行数
						StringBuffer errRow = new StringBuffer();
						String realName = getCellValue(row, 0);
						String deptName =  getCellValue(row, 1);
						String userNo = getCellValue(row, 2);
						String idNo = getCellValue(row, 3);
						String sex = getCellValue(row, 4);
						String inTime = getCellValue(row, 5);
						String phone = getCellValue(row, 6);
						String addr = getCellValue(row, 7);
						String remark = getCellValue(row, 8);
						Long deptId = null;

						// 姓名、身份证号、工号不能为空
						if (realName.isEmpty() || idNo.isEmpty() || userNo.isEmpty()) {
							throw new Exception("姓名、身份证号或者工号存在空值");
						} else {
							//如果填写了手机号，判断手机号是否已存在
							if(!phone.isEmpty())
							{
								VDeptUser user = VDeptUser.dao.findFirst("select * from v_dept_user where phone = ?",phone);
								if(user!=null)
								{
									throw new Exception(phone+"该手机号码已存在");
								}
							}
							if(!deptName.isEmpty())
							{
								VDept dept = VDept.dao.findFirst("select * from v_dept where dept_name =?",deptName);
								if(dept!=null)
								{
									deptId = dept.getId();
								}
							}

							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String createtime = df.format(new Date());
							VDeptUser deptUser = getModel(VDeptUser.class);
							deptUser.setRealName(realName);
							deptUser.setCreateDate(createtime);
							deptUser.setSex("男".equals(sex)?"1":"2");
							deptUser.setPhone(phone);
							deptUser.setUserNo(userNo);
							deptUser.setDeptId(deptId);
							deptUser.setIdNO(idNo);
							deptUser.setIntime(inTime);
							deptUser.setAddr(addr);
							deptUser.setRemark(remark);
							deptUser.setStatus("applySuc");
							deptUser.setCurrentStatus("normal");
							boolean bool = srv.addDeptUser(deptUser);
							if (bool) {
								succ_count++;
							} else {
								throw new Exception("保存失败");
							}
						}
					}
					catch (Exception e)
					{
						err_count++;
						errMap.put("errNum", i);
						errMap.put("errormsg", e.getMessage());
						errList.add(errMap);
					}
				}
				resultMap.put("total_count", total_count);
				resultMap.put("succ_count", succ_count);
				resultMap.put("err_count", err_count);
				resultMap.put("errmgs", errList);

				//websocket通知前端获取用户数量
				WebSocketMonitor.me.getPersonNum();
				WebSocketSyncData.me.sendStaffData();
				renderJson(resultMap);
			}
	}


	private String getCellValue(Row row,int index)
	{
		String result ="";
		if (row.getCell(index) != null) {
			Cell cell = row.getCell(index);
			if(cell != null){
				cell.setCellType(Cell.CELL_TYPE_STRING);
			}
			result = cell.getStringCellValue();
		}
		return result;
	}

}
