package com.xiaosong.common.web.access;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.model.TblAccess;
import com.xiaosong.model.base.BaseTblAccess;

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

    public List<String> getByAccessCodes(Long orgId) {
        List<Record>  list= Db.find("select access_code from tbl_access_org o left join tbl_access a on a.access_id=o.id where a.org_id=? and a.status=1 and o.status=1",orgId);
        return list.stream().map(n->n.getStr("access_code")).collect(Collectors.toList());
    }
//    public List<String> getByAccessCodes(Long orgId) {
//        List<Record>  list= Db.find("select access_code from tbl_access_org o left join tbl_access a on a.access_id=o.id where a.org_id=? and a.status=1 and o.status=1",orgId);
//        return list.stream().map(n->n.getStr("access_code")).collect(Collectors.toList());
//    }
}
