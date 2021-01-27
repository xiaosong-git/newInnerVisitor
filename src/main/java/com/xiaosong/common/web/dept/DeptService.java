package com.xiaosong.common.web.dept;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.date.DateUtil;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.web.access.AccessDao;
import com.xiaosong.model.TblAccessDept;
import com.xiaosong.model.VDept;
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
		String sql = "SELECT d.*,o.org_name,du.realName from v_dept d left join v_org o on d.org_id=o.id, "+
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
		boolean save = dept.save();
		if (accessIds != null && accessIds.length>0) {
			String currentDateTime = DateUtil.now();
//			List<TblAccessOrg> tblAccessOrgs=new ArrayList<>();
//			//数据库中的集合
//			List<String> accessList = accessDao.getByAccessCodes(dept.getOrgId());
//			//交集批量修改状态为1
//			List<String> intersection = Arrays.stream(accessCodes).filter(accessList::contains).collect(Collectors.toList());

//			//批量更新
//			log.info("批量更新机构门禁1：{}",intersection);
//			if (intersection.size() > 0) {
//				accessDao.updateAccessOrgStatus(intersection, orgDto.getId(), 1, currentDateTime);
//			}
//			//差集批量更新门禁状态为禁用
//			List<Long> accDifference = accessList.stream().filter(item -> !orgDto.getAccessIds().contains(item)).collect(Collectors.toList());
			//批量修改
//			log.info("批量更新机构门禁状态2：{}",intersection);
//			if (accDifference.size() > 0) {
//				tblAccessOrgDao.updateAccessOrgStatus(accDifference, orgDto.getId(), 2, currentDateTime);
//				//todo 在这修改状态
////                    memberDao.updatePushMemberAcess(org.getId(),accDifference);
//			}
			List<TblAccessDept> tblAccessOrgs = new ArrayList<>();
			for (Long accessId : accessIds) {
				tblAccessOrgs.add(new TblAccessDept()
						.setStatus(1)
						.setDeptId(dept.getId())
						.setCreateTime(currentDateTime)
						.setUpdateTime(currentDateTime)
						.setAccessId(accessId));
			}
			int[] batch = Db.batch("insert into tbl_access_dept(access_id,dept_id,create_time,update_time,status) values(?,?,?,?,?)", "access_id,dept_id,create_time,update_time,status", tblAccessOrgs, 500);
			if (batch[0]>0&&save){
				return true;
			}
		}
		return false;
	}
	
	public boolean editDept(VDept config) {
		return config.update();
	}
	
	public boolean deleteDept(Long id) {
		return VDept.dao.deleteById(id);
	}

	public List<Record> findDeptList(){
		return Db.find("select id as dept_id,dept_name from v_dept");
	}
}
