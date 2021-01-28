package com.xiaosong.common.web.dept;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cn.hutool.core.date.DateUtil;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.web.access.AccessDao;
import com.xiaosong.model.TblAccess;
import com.xiaosong.model.TblAccessDept;
import com.xiaosong.model.VDept;
import com.xiaosong.model.base.BaseTblAccess;
import org.apache.commons.lang3.StringUtils;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月14日 下午8:26:26 
* 类说明 
*/
public class DeptService {
	private Log log= Log.getLog(DeptService.class);
	public static final	DeptService me = new DeptService();
	private AccessDao accessDao = AccessDao.me;
	public Page<Record> findList(int currentPage,int pageSize,String deptName){
		List<Object> params = new ArrayList<>();
		String sql = "SELECT d.*,o.org_name,du.realName from v_dept d left join v_org o on d.org_id=o.id "+
					 "left join v_dept_user du on d.manage_user_id=du.id where 1=1";
		if(StringUtils.isNotBlank(deptName))
		{
			sql += " and dept_name like ?";
			params.add("%"+deptName+"%");
		}
		return Db.paginate(currentPage, pageSize, "select *", "from ("+sql+") as a",params.toArray());
	}
	
	public List<VDept> findByOption(){
		return VDept.dao.find("select * from v_dept");
	}
	
	public List<Record> findByOrgOption(){
		return Db.find("select * from v_org");
	}
	
	public boolean addDept(VDept dept,Long[] accessIds) {
		String accessCode = accessDao.getByAccessIds(accessIds);
		dept.setAccessCodes(accessCode);
		boolean save = dept.save();
		if (accessIds != null && accessIds.length>0) {
			String currentDateTime = DateUtil.now();

			List<TblAccessDept> tblAccessOrgs = new ArrayList<>();
			for (Long accessId : accessIds) {
				tblAccessOrgs.add(new TblAccessDept()
						.setStatus(1)
						.setDeptId(dept.getId())
						.setCreateTime(currentDateTime)
						.setAccessId(accessId));
			}
			int[] batch = Db.batch("insert into tbl_access_dept(access_id,dept_id,create_time,status) values(?,?,?,?)", "access_id,dept_id,create_time,status", tblAccessOrgs, 500);
			if (batch[0]>0&&save){
				return true;
			}
		}
		return false;
	}
	
	public boolean editDept(VDept dept, Long[] accessIds) {

		List<Long> collect = Arrays.stream(accessIds).collect(Collectors.toList());
		//数据库中的集合
		List<TblAccess> tblAccesses = accessDao.getAcessByDept(dept.getId());

		if (tblAccesses.size()>0) {

			//交集
			List<TblAccess> tblaccessCollect = tblAccesses.stream().filter(n -> collect.contains(n.getId())).collect(Collectors.toList());
			if (tblaccessCollect.size() > 0) {
				String intersection = tblaccessCollect.stream().map(n -> n.getId().toString()).collect(Collectors.joining(","));
				accessDao.updateAccessOrgStatus(intersection, dept.getId(), 1);
			}
			//差集
			String accDifference = tblAccesses.stream().filter(item -> !collect.contains(item)).map(n->n.getId().toString()).collect(Collectors.joining(","));
			if (StringUtils.isNotBlank(accDifference)) {
				//批量禁用门禁
				accessDao.updateAccessOrgStatus(accDifference, dept.getId(), 2);
				//批量修改该部门下员工的门禁
			}
		}
		List<Long> tblAccessesLong = tblAccesses.stream().map(BaseTblAccess::getId).collect(Collectors.toList());
		List<TblAccessDept> tblAccessOrgs = collect.stream().filter(item -> !tblAccessesLong.contains(item)).map(n -> new TblAccessDept()
				.setStatus(1)
				.setDeptId(dept.getId())
				.setCreateTime(DateUtil.now())
				.setAccessId(n))
				.collect(Collectors.toList());
		log.info("批量插入机构门禁{}",tblAccessOrgs);
		//批量插入
		if (tblAccessOrgs.size() > 0) {
			Db.batch("insert into tbl_access_dept(access_id,dept_id,create_time,status) values(?,?,?,?)", "access_id,dept_id,create_time,status", tblAccessOrgs, 500);
		}
		return dept.update();
	}
	
	public boolean deleteDept(Long id) {
		return VDept.dao.deleteById(id);
	}

	public List<Record> findDeptList(){
		return Db.find("select id as dept_id,dept_name from v_dept");
	}
}
