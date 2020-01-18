package com.xiaosong.common.api.deptUser;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.api.base.MyBaseService;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.constant.TableList;
import java.util.List;

/**
 * @description: 原公司员工，现部门员工
 * @author: cwf
 * @create: 2020-01-10 17:39
 **/
public class DeptUserService extends MyBaseService {
    Log log=Log.getLog(DeptUserService.class);
    public Result findApplySuc(String userId) {
        String columnSql = "select du.realName userName,du.createDate,du.deptId companyId,og.org_name companyName, d.dept_name sectionName ";
        String fromSql = " from " + TableList.DEPT_USER + " du " +
                " left join " + TableList.DEPT + " d on du.deptId=d.id" +
                 " left join"  + TableList.ORG +" og on d.org_id=og.id"+
//                " left join " + TableList.DICT_ITEM + " d on d.dict_code='companyUserRoleType' and d.item_code=du.roleType " +
//                " left join " + TableList.DICT_ITEM + " i on i.dict_code='companyUserStatus' and i.item_code=du.status " +
                " where du.id = '"+userId+"' and du.status = 'applySuc' and du.currentStatus='normal'";
        List<Record> records = Db.find((columnSql + fromSql));
        log.info("查询公司信息{}",columnSql+fromSql);
        return records != null && !records.isEmpty()
                ? ResultData.dataResult("success","获取公司成功",apiList(records))
                : Result.unDataResult("success","暂无数据");
    }
}
