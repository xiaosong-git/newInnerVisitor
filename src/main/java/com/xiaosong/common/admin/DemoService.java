package com.xiaosong.common.admin;

import com.xiaosong.common.compose.Result;
import com.xiaosong.common.compose.ResultData;
import com.xiaosong.model.VDictionaries;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoService {
	
	public static final	DemoService me = new DemoService();
	public static final VDictionaries dao = VDictionaries.dao;
	
	/*public TblNews findTblNews(){
		//获取jxt.sql 文件已经在插件加载时加载完毕 
		//Sqlpara sql = Db.getSqlPara(key, data); key为jxt文件中的名称，data为传入的参数（Map）
		//Sqlpara sql = dao.getSqlPara(key, data)
		return dao.findById(1);
	}*/
	//Sqlpara sql = dao.getSqlPara("sys_account.admin-paginate", Model<VDictionaries> s);
	public List<Record> findDictionaty() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("dictionariesKey", "1");
		SqlPara para = Db.getSqlPara("demo.find", map);
		return Db.find(para);
	}
	public Result demo1(){
		return ResultData.dataResult("success","成功",Db.findFirst("select * from v_app_user"));
	}
}
