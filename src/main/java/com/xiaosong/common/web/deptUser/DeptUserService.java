package com.xiaosong.common.web.deptUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.util.DESUtil;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月14日 下午8:27:19 
* 类说明 
*/
public class DeptUserService {
	public static final	DeptUserService me = new DeptUserService();
	
	public Page<Record> findList(String realName,String dept , int currentPage, int pageSize){

		StringBuilder sql = new StringBuilder();
		List<Object> objects = new LinkedList<>();
		sql.append("from (select u.*,d.dept_name from v_dept_user u left join v_dept d on u.deptId=d.id where 1=1 ");

		if(realName!=null){
			sql.append(" and u.realName like CONCAT('%',?,'%') ");
			objects.add(realName);
		}
		if(dept!=null){
			sql.append(" and u.deptId = ? ");
			objects.add(dept);
		}
		sql.append(") as a ");
		return Db.paginate(currentPage, pageSize, "select *", sql.toString(),objects.toArray());
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
}
