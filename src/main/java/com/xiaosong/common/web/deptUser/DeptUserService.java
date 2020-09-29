package com.xiaosong.common.web.deptUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
		sql.append("from (select u.*,d.dept_name from v_dept_user u left join v_dept d on u.deptId=d.id where 1=1 and currentStatus!='deleted'");

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

	public Map findUserList(String phone,String name ,String dept_id, int currentPage, int pageSize){
		Map<String,Object> map = new HashMap<>();
		StringBuilder sql = new StringBuilder();
		List<Object> objects = new LinkedList<>();
		sql.append("select id,deptId as dept_id,realName as real_name,phone,dept_name,sex ");
		sql.append("from (select u.*,d.dept_name from v_dept_user u left join v_dept d on u.deptId=d.id where 1=1 and currentStatus!='deleted'");
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
		sql.append(") as a ");
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
		return Db.findFirst("select u.*,d.org_id from v_dept_user u left join v_dept d on d.id = u.deptId where u.id = ?",staffId);
	}


	public Record confireNameAndIdNO(String name ,String idNO){
		return Db.findFirst("select * from v_dept_user where realName = ? and idNO = ?",name,idNO);
	}

	public Record findByIdNOOrPhone(String idNO ,String phone){
		StringBuilder sql = new StringBuilder();
		List<Object> objects = new LinkedList<>();
		sql.append("select * from v_dept_user where  1 = 1");
		if(!idNO.isEmpty()){
			Record record = Db.findFirst("select * from v_user_key");
			String idNo = DESUtil.encode(record.getStr("workKey"), idNO);
			sql.append(" and idNO = ?");
			objects.add(idNo);
		}
		if(!phone.isEmpty()){
			sql.append(" and phone = ?");
			objects.add(phone);
		}
		return Db.findFirst(sql.toString(),objects.toArray());
	}
}
