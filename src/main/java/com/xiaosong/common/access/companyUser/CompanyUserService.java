package com.xiaosong.common.access.companyUser;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.xiaosong.common.api.base.MyBaseService;
import com.xiaosong.common.api.foreign.ForeignService;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.constant.MyRecordPage;
import com.xiaosong.constant.TableList;
import com.xiaosong.param.ParamService;
import com.xiaosong.util.Base64;
import com.xiaosong.util.ConsantCode;
import com.xiaosong.util.DateUtil;
import com.xiaosong.util.FilesUtils;

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
	                String imageServerUrl = ParamService.me.findValueByName("imageServerUrl");
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

	public Result newFindApplyAllSucOrg(String orgCode, Integer pageNum, Integer pageSize,String type) {
		pageNum=pageNum==null?1:pageNum;
		pageSize=pageSize==null?10:pageSize;
		String and ="";
		if (type!=null) {
			 and = " and  (DATE_FORMAT(cu.createDate, '%Y-%m-%d') = '" + DateUtil.getCurDate()+"' or DATE_FORMAT(cu.authDate, '%Y-%m-%d') = '" + DateUtil.getCurDate()+"' ) ";
		}
		String columnSql = "select cu.id,cu.deptId companyId,'' sectionId,cu.id userId,cu.realName userName,DATE_FORMAT(cu.createDate, '%Y-%m-%d ') createDate,DATE_FORMAT(cu.createDate, '%H:%i:%s') createTime ," +
				"                cu.roleType,cu.status,cu.currentStatus,cu.postId,cu.idHandleImgUrl ,'01' idType, " +
				"                cu.idNO,c.floor companyFloor,cu.phone ";
		String fromSql = " from " + TableList.DEPT_USER + " cu " +
				" left join " + TableList.DEPT + " c on cu.deptId=c.id" +
				" join" + TableList.ORG + " og on c.org_id=og.id" +
				" where og.org_code = '" + orgCode + "' and cu.status = 'applySuc' " + " and cu.isAuth = 'T' and cu.currentStatus='normal' "+and;
		String count = "select count(*) " + fromSql;
		Page<Record> recordPage = Db.paginateByFullSql(pageNum, pageSize, count, columnSql + fromSql);
		List<Record> rows = recordPage.getList();
		MyRecordPage myPage = new MyRecordPage(apiList(rows), pageNum, pageSize, recordPage.getTotalPage(), recordPage.getTotalRow());
		return ForeignService.me.insertUserPhoto(myPage);
	}

}
