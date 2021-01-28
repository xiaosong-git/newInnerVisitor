package com.xiaosong.common.api.deptUser;

import com.jfinal.aop.Inject;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.api.base.MyBaseService;
import com.xiaosong.common.api.foreign.ForeignService;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.constant.MyRecordPage;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VDeptUser;

import java.util.List;

/**
 * @description: 原公司员工，现部门员工
 * @author: cwf
 * @create: 2020-01-10 17:39
 **/
public class DeptUserService extends MyBaseService {

    Log log=Log.getLog(DeptUserService.class);
    public Result findApplySuc(String userId) {
        String columnSql = "select du.realName userName,currentStatus,status,roleType,du.createDate," +
                "du.deptId companyId,concat(IFNULL(floor,'1'),'层',dept_name) companyName, case when IFNULL(du.addr,'')='' then '省行政服务中心' else du.addr end as addr,d.dept_name sectionName ";
        String fromSql = " from " + TableList.DEPT_USER + " du " +
                " left join " + TableList.DEPT + " d on du.deptId=d.id" +
                 " left join"  + TableList.ORG +" og on d.org_id=og.id"+
                " where du.id = '"+userId+"' and du.status = 'applySuc' and du.currentStatus='normal'";
        List<Record> records = Db.find((columnSql + fromSql));
        log.info("查询公司信息{}",columnSql+fromSql);
        return records != null && !records.isEmpty()
                ? ResultData.dataResult("success","获取公司成功",apiList(records))
                : Result.unDataResult("success","暂无数据");
    }


    /**
     * 获取上级领导
     * @return
     */
    public List<VDeptUser> getSuperior(Long userId)
    {
        List<VDeptUser> list = VDeptUser.dao.find("select * from v_dept_user where deptId = (select deptId from v_dept_user where id = ?  ) and deptLeader =1",userId);
        return list;
    }


}
