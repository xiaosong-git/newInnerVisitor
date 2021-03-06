package com.xiaosong.common.web.access;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.model.TblAccess;
import com.xiaosong.model.TblAccessDept;
import com.xiaosong.model.base.BaseTblAccess;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: newInnerVisitor
 * @description:
 * @author: cwf
 * @create: 2021-01-26 11:25
 **/
public class AccessDao {
    public static final AccessDao me = new AccessDao();

    TblAccess getByAccessCode(String accessCode, Long orgId){
        return  TblAccess.dao.findFirst("select * from tbl_access where status= 1 and access_code=? and org_id=?",accessCode,orgId);
    }

    public List<TblAccess> getAcessByDept(Long deptId) {
        return TblAccess.dao.find("select a.id,access_code,name from tbl_access_dept d left join tbl_access a on a.id=d.access_id where d.dept_id=? and  a.status=1",deptId);
    }

    /**
     * 批量查询门禁
     * @param accessIds
     * @return
     */
    public  List<TblAccess> getByAccessIds(Object[] accessIds) {
        String collect = Arrays.stream(accessIds).map(Object::toString).collect(Collectors.joining(","));
        List<TblAccess> accesses = TblAccess.dao.find("select access_code,name from tbl_access where id in ("+collect+") and status=1");
        return accesses ;

    }

    /**
     * 批量更新门禁
     * @param intersection
     * @param deptId
     * @param status
     * @return
     */
    public int updateAccessOrgStatus(String intersection, Long deptId,int status) {
        return  Db.update("update tbl_access_dept set status=? ,update_time=now() where access_id in(" + intersection + ") and dept_id=?",status, deptId);

    }

}
