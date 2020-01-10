package com.xiaosong.common.api.deptUser;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.constant.TableList;
import java.util.List;

/**
 * @description: 原公司员工，现部门员工
 * @author: cwf
 * @create: 2020-01-10 17:39
 **/
public class DeptUserService {

    public Result findApplySuc(String userId) {
        String columnSql = "select cu.*,c.companyName,cs.sectionName";
        String fromSql = " from " + TableList.DEPT_USER + " cu " +
                " left join " + TableList.COMPANY + " c on cu.companyId=c.id" +
                " left join " + TableList.DEPT + " cs on cu.sectionId=cs.id" +
                 " left join"  + TableList.ORG +" og on c.orgid=og.id"+
//                " left join " + TableList.DICT_ITEM + " d on d.dict_code='companyUserRoleType' and d.item_code=cu.roleType " +
//                " left join " + TableList.DICT_ITEM + " i on i.dict_code='companyUserStatus' and i.item_code=cu.status " +
                " where cu.userId = '"+userId+"' and cu.status = 'applySuc' and cu.currentStatus='normal'";
        List<Record> records = Db.find((columnSql + fromSql));
        return records != null && !records.isEmpty()
                ? ResultData.dataResult("success","获取公司成功",records)
                : Result.unDataResult("success","暂无数据");
    }
}
