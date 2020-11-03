package com.xiaosong.task;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.web.sso.SSOService;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VDept;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.util.DESUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by CNL on 2020/9/15.
 */
public class SyncOrgTask extends  Thread {

    //定时任务发送数据到上位机
    @Override
    public void run()
    {
        System.out.println("开始同步部门数据");
        int i=0;
        List<Record> list = Db.find("select * from "+ TableList.DEPT + "  where  IFNULL(isSync,'F')!= 'T'");
        if(list!=null && list.size()>0) {
            for (Record record : list) {
                String code = record.getStr("code");
                String deptName = record.getStr("dept_name");
                Long id = record.getLong("id");
                if(StringUtils.isBlank(code) ||StringUtils.isBlank(deptName))
                {
                    continue;
                }
                boolean result = SSOService.me.deptSync(SSOService.me.getToken(), code, deptName);
                if (result) {
                    VDept dept = VDept.dao.findById(id);
                    dept.setIsSync("T");
                    dept.update();
                    i++;
                }
            }
        }
        System.out.println("结束同步部门数据，共同步"+i);
    }

}
