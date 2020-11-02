package com.xiaosong.common.web.deptUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.upload.UploadFile;
import com.xiaosong.MainConfig;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.util.DESUtil;
import com.xiaosong.util.HttpPostUploadUtil;
import org.apache.commons.lang3.StringUtils;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月14日 下午8:27:19 
* 类说明 
*/
public class DeptUserService {

	public static final	DeptUserService me = new DeptUserService();
	private String imgServerUrl = MainConfig.p.get("imgServerUrl");//图片服务地址

	public Page<Record> findList(String realName,String dept,String idHandleImgUrl, int currentPage, int pageSize){
	   SqlPara sqlPara =	findList(realName,dept,idHandleImgUrl);
	   return Db.paginate(currentPage, pageSize, sqlPara);
	}


	public List<Record> findRecordList(String realName,String dept,String idHandleImgUrl){
		SqlPara sqlPara =findList(realName,dept,idHandleImgUrl);
		return Db.find(sqlPara);
	}


	private SqlPara findList(String realName,String dept,String idHandleImgUrl){

		StringBuilder sql = new StringBuilder();
		List<Object> objects = new LinkedList<>();
		sql.append("select * from (select u.*,d.dept_name from v_dept_user u left join v_dept d on u.deptId=d.id where 1=1 and currentStatus!='deleted' and  IFNULL(userType,'')!='visitor'");

		if(realName!=null){
			sql.append(" and u.realName like CONCAT('%',?,'%') ");
			objects.add(realName);
		}
		if(dept!=null){
			sql.append(" and u.deptId = ? ");
			objects.add(dept);
		}

		if(idHandleImgUrl!=null){
			if("0".equals(idHandleImgUrl)) {
				sql.append(" and u.idHandleImgUrl is null ");
			}
			else if("1".equals(idHandleImgUrl))
			{
				sql.append(" and u.idHandleImgUrl is not null ");
			}
		}

		sql.append(" order by u.id desc) as a ");

		SqlPara sqlPara = new SqlPara();
		sqlPara.setSql(sql.toString());
		for(Object object : objects) {
			sqlPara.addPara(object);
		}

		return sqlPara;
	}

	
	public boolean addDeptUser(VDeptUser config) {
		Record record = Db.findFirst("select * from v_user_key");
		String idNo = DESUtil.encode(record.getStr("workKey"), config.getIdNO());
		config.setIdNO(idNo);
		return config.save();
	}
	
	public boolean editDeptUser(VDeptUser config) {
		Record record = Db.findFirst("select * from v_user_key");
		String idNo = DESUtil.encode(record.getStr("workKey"), config.getIdNO());
		config.setIdNO(idNo);
		return config.update();
	}
	
	public boolean deleteDeptUser(Long id) {
		return VDeptUser.dao.deleteById(id);
	}
	
	public boolean uploadDeptUser(UploadFile uploadfile) {
		File file =  uploadfile.getFile();
		try {
			FileInputStream in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return false;
	}

	public Map findUserList(String phone,String name ,String dept_id, int currentPage, int pageSize){
		Map<String,Object> map = new HashMap<>();
		StringBuilder sql = new StringBuilder();
		List<Object> objects = new LinkedList<>();
		sql.append("select id,deptId as dept_id,realName as real_name,phone,dept_name,sex,CASE WHEN (addr is null  or addr ='')THEN deptAddr ELSE addr END AS address  ");
		sql.append("from (select u.*,d.dept_name,d.addr as deptAddr from v_dept_user u left join v_dept d on u.deptId=d.id where 1=1 and IFNULL(userType,'')!='visitor' and currentStatus!='deleted'");
		if( !phone.isEmpty()){
			sql.append(" and phone like CONCAT('%',?,'%')");
			objects.add(phone);
		}
		if( !name.isEmpty()){
			sql.append(" and realName like CONCAT('%',?,'%') ");
			objects.add(name);
		}
		if( !dept_id.isEmpty()){
			sql.append(" and deptId = ? ");
			objects.add(dept_id);
		}
		sql.append(") as a  order by id desc ");
		int total = Db.find(sql.toString(),objects.toArray()).size();
		map.put("total",total);
		map.put("page_number",currentPage);
		currentPage = (currentPage - 1)*pageSize;
		sql.append("limit ?,?");
		objects.add(currentPage);
		objects.add(pageSize);
		System.out.println(sql.toString());
		System.out.println(objects.toArray());
		List<Record> list = Db.find(sql.toString(),objects.toArray());
		map.put("page_size",list.size());
		map.put("data",list);
		return map;
	}

	public Record findByStaffId(String staffId){
		return Db.findFirst("select u.*,d.org_id,org_code from v_dept_user u left join v_dept d on d.id = u.deptId left join v_org o on o.id = d.org_id where u.id = ?",staffId);
	}


	public Record confireNameAndIdNO(String name ,String idNO){
		return Db.findFirst("select * from v_dept_user where realName = ? and idNO = ?",name,idNO);
	}

	public Record findByIdNOOrPhone(String idNO ,String phone){
		StringBuilder sql = new StringBuilder();
		List<Object> objects = new LinkedList<>();
		sql.append("select * from v_dept_user where  1 = 1");
		if(StringUtils.isNotBlank(idNO)){
			Record record = Db.findFirst("select * from v_user_key");
			String idNo = DESUtil.encode(record.getStr("workKey"), idNO);
			sql.append(" and idNO = ?");
			objects.add(idNo);
		}
		if(StringUtils.isNotBlank(phone)){
			sql.append(" and phone = ?");
			objects.add(phone);
		}
		return Db.findFirst(sql.toString(),objects.toArray());
	}



	public String uploadUserImg(String filepath,String userId){
		try {
			String urlStr = imgServerUrl+"goldccm-imgServer/goldccm/image/gainData";
			Map<String, String> textMap = new HashMap<String, String>();
			textMap.put("ad", null);
			textMap.put("type", "3");
			textMap.put("userId", userId);
			Map<String, String> fileMap = new HashMap<String, String>();
			fileMap.put("file", filepath);
			String ret = HttpPostUploadUtil.formUpload(urlStr, textMap, fileMap);
			JSONObject json = JSON.parseObject(ret);
			JSONObject jsonVerify = json.getJSONObject("verify");
			JSONObject jsonData = json.getJSONObject("data");
			if (jsonVerify != null && jsonVerify.containsKey("sign") && "success".equals(jsonVerify.get("sign"))) {
				return jsonData.getString("imageFileName");
			}
		}
		catch (Exception ex)
		{
			ex.fillInStackTrace();
		}

		return null;
	}




}
