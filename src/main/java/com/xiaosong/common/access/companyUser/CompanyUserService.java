package com.xiaosong.common.access.companyUser;

import com.xiaosong.common.api.base.MyBaseService;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.util.Base64;
import com.xiaosong.util.BaseUtil;
import com.xiaosong.util.ConsantCode;
import com.xiaosong.util.FilesUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;


import java.text.SimpleDateFormat;
import java.util.*;


/**
* @author xiaojf
* @version 创建时间：2019年12月4日 上午11:17:34
* 类说明
*/
public class CompanyUserService extends MyBaseService {
	public static final	CompanyUserService me = new CompanyUserService();
	
	 public Result findApplySucByOrg(String org_code) throws Exception {
		 Map<String, Object> paramlist = new HashMap<>();
	     String create_date =new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	     paramlist.put("org_code", org_code);
	     paramlist.put("create_date", create_date);
	     if(org_code==null){
	         return  Result.unDataResult(ConsantCode.FAIL,"缺少大楼参数!");
	     }
	     SqlPara para = Db.getSqlPara("companyUser.findApplySucByOrg", paramlist);
	     List<Record> list = Db.find(para);
	     List<Map<String, Object>> li = new ArrayList<Map<String, Object>>();
	     for(int i=0;i<list.size();i++) {
	        	Map<String,Object> map=list.get(i).getColumns();
	        	String idHandleImgUrl=(String) map.get("idHandleImgUrl");
	        	if(idHandleImgUrl!=null&&idHandleImgUrl.length()!=0) {
	        	    //生产图片地址
	                String imageServerUrl = findValueByName("imageServerUrl");
	        	 String photo= Base64.encode(FilesUtils.getImageFromNetByUrl(imageServerUrl+idHandleImgUrl));
//	           测试图片地址
//	        	 String photo=Base64.encode(FilesUtils.getPhoto(idHandleImgUrl));
	        	 list.get(i).set("photo", photo);
	        	 li.add(list.get(i).getColumns());
	        	}
	     }
	     return list.isEmpty()
	                ? ResultData.dataResult("success","获取大楼员工信息成功",list)
	                : Result.unDataResult("success","暂无数据");
	 }
	 
	 public Result findApplyAllSucByOrg(Map<String, Object> paramMap) throws Exception {
		 String org_code = BaseUtil.objToStr(paramMap.get("org_code"), null);
		 Map<String, Object> paramlist = new HashMap<String, Object>();
		 paramlist.put("org_code", org_code);

	        if(org_code==null){
	            return  Result.unDataResult(ConsantCode.FAIL,"缺少大楼参数!");
	        }
	        SqlPara para = Db.getSqlPara("companyUser.findApplyAllSucByOrg", paramlist);
		    List<Record> list = Db.find(para);
	       

	        for(int i=0;i<list.size();i++) {
	        	Map<String,Object> map=list.get(i).getColumns();
	        	String idHandleImgUrl=(String) map.get("idHandleImgUrl");
	        	if(idHandleImgUrl!=null&&idHandleImgUrl.length()!=0) {
//	             //生产图片地址
	                String imageServerUrl = findValueByName("imageServerUrl");

	        	 String photo=Base64.encode(FilesUtils.getImageFromNetByUrl(imageServerUrl+idHandleImgUrl));
//	           测试图片地址
//	        	 String photo=Base64.encode(FilesUtils.getPhoto(idHandleImgUrl));
	        	 list.get(i).set("photo", photo);
	        	}
	        }
	        return list != null && !list.isEmpty()
	                ? ResultData.dataResult("success","获取大楼员工信息成功",list)
	                : Result.unDataResult("success","暂无数据");
	 }
	 protected String findValueByName(String paramName) {
		 Map<String, Object> paramlist = new HashMap<String, Object>();
		 paramlist.put("imageServerUrl", paramName);
		 Cache redis = Redis.use("REDIS");
		 String value = null;
		 value = redis.get(paramName);
		 if (value == null){
			 SqlPara para = Db.getSqlPara("companyUser.findValueByNameFromDB", paramlist);
			 List<Record> list = Db.find(para);
			 value = list.get(0).getColumns().get("paramText").toString();
			 if(value!=null) {
				 redis.set("params_" + paramName, value);
			 }
		 }
		 return value;
	 }
}
