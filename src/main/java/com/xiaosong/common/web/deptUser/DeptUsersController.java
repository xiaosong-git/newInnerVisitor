package com.xiaosong.common.web.deptUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.json.Json;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.xiaosong.MainConfig;
import com.xiaosong.bean.DeptUserBean;
import com.xiaosong.bean.PeopleCheckBean;
import com.xiaosong.common.api.userPost.UserPostService;
import com.xiaosong.common.api.websocket.WebSocketMonitor;
import com.xiaosong.common.api.websocket.WebSocketSyncData;
import com.xiaosong.common.web.sysn.SysnService;
import com.xiaosong.model.VDept;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.model.VUserPost;
import com.xiaosong.util.*;
import com.xiaosong.util.DateUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.xiaosong.constant.Constant;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月14日 下午8:27:07 
* 类说明 
*/
public class DeptUsersController extends Controller{
	private Log log = Log.getLog(DeptUsersController.class);
	public DeptUserService srv = DeptUserService.me;
	private String imgServerUrl = MainConfig.p.get("imgServerUrl")+"imgserver/";//图片服务地址
    private SysnService sysnService = SysnService.me;
	public void findList() {
		String realName = getPara("realName");
		String dept = getPara("dept");
		String idHandleImgUrl = getPara("idHandleImgUrl");
        String phone = getPara("phone");
        String cardNo = getPara("cardNo");
        String idCard = getPara("idCard");

		int currentPage = getInt("currentPage");
		int pageSize = getInt("pageSize");
		Page<Record> pagelist = srv.findList(realName,dept,idHandleImgUrl,phone,cardNo,idCard,currentPage,pageSize);
		List<Record> recordList = pagelist.getList();
		Record user_key = Db.findFirst("select * from v_user_key");
		for(Record record : recordList)
		{
			String idNo =record.get("idNO");
			idNo = DESUtil.decode(user_key.getStr("workKey"), idNo);

			List<VUserPost> list = VUserPost.dao.find("select * from v_user_post where userId = ?",record.getLong("id"));
			Long[] userPosts = null;
			if(list!=null && list.size()>0) {
				userPosts = new Long[list.size()];
				for(int i = 0; i< list.size();i++)
				{
					userPosts[i] = list.get(i).getPostId();
				}
			}
			record.set("userPost",userPosts);
			record.set("idNO",idNo);
		}
		renderJson(pagelist);
	}
	
	public void addDeptUser() throws Exception {
		//todo 获取accessCodes 插入code
		String accessCodes = getPara("accessCodes");
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
		String cardNO = getPara("cardNO");
		String strActiveDate =getPara("activeDate");
		String strExpiryDate =getPara("expiryDate");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createtime = df.format(new Date());
		String [] postIds = getParaValues("userPost[]");
		VDeptUser deptUser = getModel(VDeptUser.class);
		deptUser.setRealName(realName);
		deptUser.setAccessCodes(accessCodes);
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
		deptUser.setCardNO(cardNO);
		deptUser.setUserType("staff");
		Date activeDate = DateUtil.changeDate(strActiveDate);
		Date expiryDate= DateUtil.changeDate(strExpiryDate);
		deptUser.setActiveDate(activeDate);
		deptUser.setExpiryDate(expiryDate);

		if(imgName!=null && !"cache".equals(imgName)) {
			String photoPath = File.separator + "user" + File.separator + "cache" + File.separator + imgName;
			String cahceImgUrl = imgServerUrl + photoPath;

			try {
				byte[] data = FilesUtils.compressUnderSize(FilesUtils.getImageFromNetByUrl(cahceImgUrl), 40960L);
				String photo = com.xiaosong.util.Base64.encode(data);
				JSONObject photoResult = AuthUtil.auth(idNO, realName, photo);
				//实人认证
				if ("00000".equals(photoResult.getString("return_code"))) {
					deptUser.setIsAuth("T");
				} else {
					JSONObject errorData = photoResult.getJSONObject("data");
					System.out.println(photoResult.toJSONString());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			deptUser.setIdHandleImgUrl(photoPath);
//			String photo = com.xiaosong.util.Base64.encode(FilesUtils.compressUnderSize(FilesUtils.getImageFromNetByUrl(cahceImgUrl), 40960L));
//			JSONObject photoResult = AuthUtil.auth(idNO, realName, photo);
//			//实人认证
//			if ("00000".equals(photoResult.getString("return_code"))) {
//				deptUser.setIsAuth("T");
//			}else{
//				JSONObject errorData = photoResult.getJSONObject("data");
//				System.out.println(errorData.getString("resultMsg"));
//			}
		}
		boolean bool = Db.tx(()->{
			srv.addDeptUser(deptUser);
			UserPostService.me.addPostUser(deptUser.getId(),postIds);
			return true;
		});
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
		String accessCodes = getPara("accessCodes");
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
		String cardNO = getPara("cardNO");
		String strActiveDate =getPara("activeDate");
		String strExpiryDate =getPara("expiryDate");
		String [] postIds = getParaValues("userPost[]");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createtime = df.format(new Date());
		VDeptUser deptUser = VDeptUser.dao.findById(id);
		deptUser.setAccessCodes(accessCodes);
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
		deptUser.setCardNO(cardNO);
		deptUser.setUserType("staff");

		Date activeDate = DateUtil.changeDate(strActiveDate);
		Date expiryDate= DateUtil.changeDate(strExpiryDate);
		deptUser.setActiveDate(activeDate);
		deptUser.setExpiryDate(expiryDate);

		if(imgName!=null && !"cache".equals(imgName)) {
			String photoPath = File.separator + "user" + File.separator + "cache" + File.separator + imgName;
			String cahceImgUrl = imgServerUrl + photoPath;

			try {
				byte[] data = FilesUtils.compressUnderSize(FilesUtils.getImageFromNetByUrl(cahceImgUrl), 40960L);
				String photo = com.xiaosong.util.Base64.encode(data);
				JSONObject photoResult = AuthUtil.auth(idNO, realName, photo);
				if ("00000".equals(photoResult.getString("return_code"))) {  //实人认证
					deptUser.setIsAuth("T");
				} else {
					JSONObject errorData = photoResult.getJSONObject("data");
					System.out.println(photoResult.toJSONString());
				}
				deptUser.setIdHandleImgUrl(photoPath);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		boolean bool = Db.tx(()->{
			srv.editDeptUser(deptUser);
			UserPostService.me.addPostUser(deptUser.getId(),postIds);
			return true;
		});
		if (bool) {
			sysnService.setStaffIsReceiveF(deptUser.getId());
			deptUser.setIsReceive("F");
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
        sysnService.setStaffIsReceiveF(id);
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
		List<UploadFile> uploadFiles = getFiles();
		if(uploadFiles.size() == 0){
			renderJson(RetUtil.fail("文件接收失败"));
		}else if(uploadFiles.size() == 1 && (uploadFiles.get(0).getFile().getName().endsWith(".xls")||uploadFiles.get(0).getFile().getName().endsWith(".xlsx"))){
			File file = uploadFiles.get(0).getFile();
			Map xmlMap = parseXlsFile(file);
			WebSocketMonitor.me.getPersonNum();
			renderJson(xmlMap);
		}else if(uploadFiles.size() == 1 && (uploadFiles.get(0).getFile().getName().endsWith(".zip")||uploadFiles.get(0).getFile().getName().endsWith(".rar"))){
			File file = uploadFiles.get(0).getFile();
			Map zipMap = parseZipFile(new HashMap<>(),file);
			WebSocketMonitor.me.getPersonNum();
			renderJson(zipMap);
		}else{
			File xmlFile = null;
			File zipFile = null;
			for(UploadFile uploadFile:uploadFiles){
				if(uploadFile.getFile().getName().endsWith(".xls")||uploadFile.getFile().getName().endsWith(".xlsx")){
					xmlFile = uploadFile.getFile();
				}
				if(uploadFile.getFile().getName().endsWith(".zip")||uploadFile.getFile().getName().endsWith(".rar")){
					zipFile = uploadFile.getFile();
				}
			}
			Map xmlMap = parseXlsFile(xmlFile);
			Map resultMap = parseZipFile(xmlMap,zipFile);

			List ids = (List) resultMap.get("pic_success");
			//WebSocketMonitor.me.getPersonNum();
			WebSocketSyncData.me.sendStaffData();
			renderJson(resultMap);
		}
	}

	/**
	 * 	excel表格解析
	 *
	 * @param xlsFile
	 * @throws IOException
	 */
	private Map<String,Object> parseXlsFile(File xlsFile) throws IOException {
		Map<String, Object> resultMap = new HashMap<>();
		int total_count = 0;
		int succ_count = 0;
		int err_count = 0;
		List errList = new ArrayList();
		List successList = new ArrayList();
		Workbook book = null;
		if (xlsFile.getName().endsWith(".xls")) {
			book = new HSSFWorkbook(new FileInputStream(xlsFile));
		} else if (xlsFile.getName().endsWith(".xlsx")) {
			book = new XSSFWorkbook(new FileInputStream(xlsFile));
		}
		Sheet sheet = book.getSheetAt(0);
		int rows = sheet.getPhysicalNumberOfRows();
		total_count = rows - 1;
		for (int i = 1; i <= total_count; i++) {// 从1开始是去除首行-头
			Map errMap = new HashMap<>();
			Map successMap = new HashMap<>();
			try {
				Row row = sheet.getRow(i);// 行数
				boolean hasValue = false;
				for (int j = 0; j < 12; j++) {
					String value = getCellValue(row, j);
					if (!StringUtils.isBlank(value)) {
						hasValue = true;
						break;
					}
				}
				if (!hasValue) {
					total_count--;
					continue;
				}
				StringBuffer errRow = new StringBuffer();
				String realName = getCellValue(row, 0);
				String deptName = getCellValue(row, 1);
				String cardNO = getCellValue(row, 2);
				String userNo = getCellValue(row, 3);
				String idNo = getCellValue(row, 4);
				String sex = getCellValue(row, 5);
				String inTime = getCellValue(row, 6);
				String phone = getCellValue(row, 7);
				String addr = getCellValue(row, 8);
				String remark = getCellValue(row, 9);
				String strActiveDate = getCellValue(row, 10);
				String strExpiryDate = getCellValue(row, 11);
				Date activeDate = null;
				Date expiryDate = null;
				activeDate = DateUtil.changeDate(strActiveDate);
				expiryDate = DateUtil.changeDate(strExpiryDate);
				Long deptId = null;

				if (StringUtils.isBlank(realName) || StringUtils.isBlank(idNo)) {
					throw new Exception("姓名或者身份证号存在空值");
				} else {
//					if(!phone.isEmpty()) {
//						VDeptUser user = VDeptUser.dao.findFirst("select * from v_dept_user where currentStatus='normal' and userType='staff' and  phone = ?",phone);
//						if(user!=null) {
//							throw new Exception(phone+"该手机号码已存在");
//						}
//					}
					if (!deptName.isEmpty()) {
						VDept dept = VDept.dao.findFirst("select * from v_dept where dept_name =?", deptName);
						if (dept == null) {
							dept = new VDept();
							dept.setOrgId(1L);
							dept.setDeptName(deptName);
							dept.save();
						}
						deptId = dept.getId();
						//deptId = Long.parseLong(deptName);
					}
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String createtime = df.format(new Date());
					VDeptUser deptUser = null;
					boolean isNewRecord = false;

					Record record = Db.findFirst("select * from v_user_key");
					idNo = DESUtil.encode(record.getStr("workKey"), idNo);
					if (StringUtils.isNotBlank(realName) && StringUtils.isNotBlank(idNo)) {
						deptUser = VDeptUser.dao.findFirst("select * from v_dept_user where currentStatus='normal' and userType='staff' and  idNO = ? and realName=?", idNo, realName);
					}
					if (deptUser == null) {
						deptUser = getModel(VDeptUser.class);
						isNewRecord = true;
					}
					deptUser.setRealName(realName);
					deptUser.setCreateDate(createtime);
					deptUser.setSex("男".equals(sex) ? "1" : "2");
					deptUser.setPhone(phone);
					deptUser.setUserNo(userNo);
					deptUser.setDeptId(deptId);
					deptUser.setIdNO(idNo);
					deptUser.setIntime(inTime);
					deptUser.setAddr(addr);
					deptUser.setRemark(remark);
					deptUser.setStatus("applySuc");
					deptUser.setCurrentStatus("normal");
					deptUser.setUserType("staff");
					deptUser.setActiveDate(activeDate);
					deptUser.setExpiryDate(expiryDate);
					if (!cardNO.isEmpty()) {
						deptUser.setCardNO(cardNO);
					}
					boolean bool = isNewRecord ? deptUser.save() : deptUser.update();
					if (bool) {
						succ_count++;
						successMap.put("userName", realName);
						successMap.put("phone", phone);
						successList.add(successMap);
					} else {
						throw new Exception("保存失败");
					}
				}
			} catch (Exception e) {
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
		resultMap.put("sucmgs", successList);
		return resultMap;
	}

	/**
	 * 	解析照片压缩包
	 *
	 * @param map		表格解析的返回值
	 * @param zipFile
	 * @return
	 */
	private Map parseZipFile(Map<String,Object> map,File zipFile) throws Exception {
		int err_picnum = 0;	//照片失败的数量
		int suc_picnum = 0;	//成功数量
		List errList = new ArrayList();
		List sucList = new ArrayList();
		String parentFile = zipFile.getParent();
		String path = ZipUtil.unzip(zipFile.getAbsolutePath(), parentFile);
		File file = new File(path);
		Record record = Db.findFirst("select * from v_user_key");

		if(!file.exists()){
			return map;
		}
		File[] pictures  = file.listFiles();
		for(File picture :pictures){
			Map errMap = new HashMap<>();
			if(picture.getName().toLowerCase().endsWith(".jpg")){
				String pictureName =picture.getName().substring(0,picture.getName().lastIndexOf(".")) ;
				//if(pictureName.split("_").length == 2){
					//String userName = pictureName.split("_")[0];
					String phone = pictureName;
					VDeptUser user = null;
					if(map.size()==0){
						user = VDeptUser.dao.findFirst("select * from v_dept_user where userType='staff' and currentStatus='normal'  and phone = ?",phone);
					}else{
						List<Map> xmlSucList = (List) map.get("sucmgs");
						for(int i=0;i<xmlSucList.size();i++){
							if(phone.equals(xmlSucList.get(i).get("phone"))){
								user = VDeptUser.dao.findFirst("select * from v_dept_user where userType='staff' and currentStatus='normal' and phone = ?",phone);
								break;
							}
						}
					}
					if(user != null){
						user.setIdHandleImgUrl(pictureName);
						/*if(user.getIdNO().isEmpty()){
							errMap.put(picture.getName(),"该成员身份证为空");
							err_picnum++;
						}*/
/*						//测试跳过三要素认证
						user.setIsAuth("T");
						user.update();
						sysnService.setStaffIsReceiveF(user.getId());
                        sucList.add(user.getId());
						suc_picnum++;*/
						String idNo = DESUtil.decode(record.getStr("workKey"), user.getIdNO());
						String photo = com.xiaosong.util.Base64.encode(FilesUtils.compressUnderSize(FilesUtils.getPhoto(picture.getAbsolutePath()), 40960L));
						JSONObject photoResult = AuthUtil.auth(idNo,user.getRealName(),photo);

						if ("00000".equals(photoResult.getString("return_code"))) {  //实人认证
							String idHandleImgUrl = srv.uploadUserImg(picture.getAbsolutePath(),""+user.getId());
							user.setIdHandleImgUrl(idHandleImgUrl);
							user.setIsAuth("T");
							user.update();
							suc_picnum++;
							sucList.add(user.getId());
							sysnService.setStaffIsReceiveF(user.getId());
						}else{
							errMap.put(picture.getName(),"照片与本人不匹配");
							err_picnum++;
						}

						//HttpPostUploadUtil.formUpload(,,)
						picture.delete();

					}else{
						errMap.put(picture.getName(),"对应人员数据错误");
						err_picnum++;
					}
/*				}else{
					errMap.put(picture.getName(),"照片命名格式错误");
					err_picnum++;
				}*/
			}else{
				errMap.put(picture.getName(),"文件类型错误");
				err_picnum++;
			}
			if(errMap != null){
				errList.add(errMap);
			}
		}
		map.put("suc_picnum",suc_picnum);
		map.put("pic_success",sucList);
		map.put("pic_error",errList);
		map.put("err_picnum",err_picnum);

		return map;
	}



	/*private void getImportFiles() throws IOException {
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

*/
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
		return replaceBlank(result);
	}



	public static String replaceBlank(String str) {
		String dest = "";
		if (str!=null) {

			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}


	public void download() {
		String realName = getPara("realName");
		String dept = getPara("dept");
		String idHandleImgUrl = getPara("idHandleImgUrl");
        String phone = getPara("phone");
        String cardNo = getPara("cardNo");
        String idCard = getPara("idCard");

		List<Record> recordList = srv.findRecordList(realName,dept,idHandleImgUrl,phone,cardNo,idCard);
		Record user_key = Db.findFirst("select * from v_user_key");
		for(Record record : recordList)
		{
			String idNo =record.get("idNO");
			idNo = DESUtil.decode(user_key.getStr("workKey"), idNo);
			record.set("idNO",idNo);
		}
		List outputList = new ArrayList<>();
		if (recordList != null && recordList.size() > 0) {
			// 生成文件并返回
			for (int i = 0; i < recordList.size(); i++) {
				Record record = recordList.get(i);
				DeptUserBean sd = new DeptUserBean();
				sd.setName(record.getStr("realName"));
				sd.setActiveDate(record.getStr("activeDate"));
				sd.setAddress(record.getStr("address"));
				sd.setCardNO(record.getStr("cardNO"));
				sd.setDeptName(record.getStr("dept_name"));
				sd.setExpiryDate(record.getStr("expiryDate"));
				sd.setIntime(record.getStr("intime"));
				sd.setPhone(record.getStr("phone"));
				sd.setSex("1".equals(record.getStr("sex")) ? "男" : "女");
				sd.setRemark(record.getStr("remark"));
				sd.setIdNO(record.getStr("idNO"));
				sd.setUserNO(record.getStr("userNo"));
				outputList.add(sd);
			}
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(new Date());
		String exportName = date + "_员工报表.xls";
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
		String[] title = {  "姓名", "部门", "卡号","工号","身份证","性别","入职时间","联系电话","住址","备注","卡激活时间","卡过期时间"};
		byte[] data = ExcelUtil.export("人员报表", title, outputList,false);
		try {
			FileUtils.writeByteArrayToFile(exportFile, data, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		renderFile(exportFile);
	}


	public void batchUpdateTime() {

		String strActiveDate =getPara("activeDate");
		String strExpiryDate =getPara("expiryDate");
		String[] userids = this.getParaValues("userIds[]");
		String [] postIds = getParaValues("right[]");
		String strUserids = "";
		if(userids==null)
		{
			renderJson(RetUtil.fail("人员不能为空"));
		}
		for(String userId : userids)
		{
			strUserids+=userId+",";
		}

		if(strUserids.length()>0)
		{
			strUserids = strUserids.substring(0,strUserids.length()-1);
		}

		final String finalUserids = strUserids;
	    boolean result =	Db.tx(()->{
	    	if(userids==null || userids.length==0)
			{
				return false;
			}
	    	if(StringUtils.isNotBlank(strActiveDate)|| StringUtils.isNotBlank(strExpiryDate)) {
				Db.update("update v_dept_user set activeDate =? , expiryDate =? where id in (" + finalUserids + ")", strActiveDate, strExpiryDate);
			}
			if(postIds!=null && postIds.length>0) {
				for(String userId : userids)
				{
					UserPostService.me.addPostUser(Long.parseLong(userId),postIds);
				}
			}

			Db.delete("delete from v_sync where type='staff' and relationId  in ("+finalUserids+")");
			return true;
		});

		if(result) {
			renderJson(RetUtil.ok());
		}
		else
		{
			renderJson(RetUtil.fail());
		}
	}


	public void findListByDeptId() {

		String dept = getPara("dept");
		List<Record> recordList = srv.findRecordList(null,dept,null,null,null,null);
		renderJson(recordList);
	}



	public void batchApportionPost() {

		String [] postIds = getParaValues("right[]");
		String[] userids = this.getParaValues("userIds[]");

		boolean result =	Db.tx(()->{

			for(String userId : userids)
			{
				UserPostService.me.addPostUser(Long.parseLong(userId),postIds);
			}
			return true;
		});

		if(result) {
			renderJson(RetUtil.ok());
		}
		else
		{
			renderJson(RetUtil.fail());
		}
	}

    /**
     * CTID认证及重点人员核查列表
     */
    public void getPeopleCheckList() {
        try {
            int currentPage = getInt("currentPage");
            int pageSize = getInt("pageSize");
            Page<PeopleCheckBean> list = srv.getPeopleCheckList(currentPage, pageSize, getPara("name"), getPara("idNO"));
            renderJson(RetUtil.okData(list));
        } catch (Exception e) {
            log.error("错误信息：", e);
            renderJson(RetUtil.fail(e.getCause().getLocalizedMessage()));
        }
    }

    /**
     * 人员核查
     */
    public void checkPeople() {
        try {
			if (StringUtils.isEmpty(getPara("realName")) || StringUtils.isEmpty(getPara("idNO")) || StringUtils.isEmpty(getPara("imgName"))) {
				renderJson(RetUtil.fail("参数缺失！"));
				return;
			}
            String realName = getPara("realName");
            String idNO = getPara("idNO");
            String imgName = getPara("imgName");
            String photoPath = File.separator + "user" + File.separator + "cache" + File.separator + imgName;
            String cahceImgUrl = imgServerUrl + photoPath;
            byte[] data = FilesUtils.compressUnderSize(FilesUtils.getImageFromNetByUrl(cahceImgUrl), 40960L);
            String photo = com.xiaosong.util.Base64.encode(data);
            JSONObject photoResult = AuthUtil.auth(idNO, realName, photo);
            //实人认证结果
            if (!"00000".equals(photoResult.getString("return_code"))) {
                JSONObject errorData = photoResult.getJSONObject("data");
                System.out.println(photoResult.toJSONString());
                renderJson(RetUtil.ok("核验失败，错误原因：" + errorData));
            }
            renderJson(RetUtil.ok("核验无误！"));
        } catch (Exception e) {
            log.error("错误信息：", e);
            renderJson(RetUtil.fail(e.getCause().getLocalizedMessage()));
        }
    }

}
