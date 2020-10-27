package com.xiaosong.task;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.api.websocket.WebSocketSyncData;
import com.xiaosong.common.web.sso.SSOService;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.model.VSysUser;
import com.xiaosong.util.DESUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by CNL on 2020/9/15.
 */
public class SyncUserInfoTask extends  Thread {

    //定时任务发送数据到上位机
    @Override
    public void run()
    {
        System.out.println("开始同步人员数据");
        int i=0;
        List<Record> list = Db.find("select a.*,d.code from "+ TableList.DEPT_USER + " a  left join v_dept d on deptId = d.id where currentStatus='normal' and IFNULL(isSync,'F')!= 'T'");
        if(list!=null && list.size()>0) {
            String token = SSOService.me.getToken();
            Record work = Db.findFirst("select * from v_user_key");
            String workKey = work.getStr("workKey");

            for (Record record : list) {

                String username = record.getStr("phone");
                Long id =record.getLong("id");
                String password = "000000";
                String name = record.getStr("realName");
                String phone = record.getStr("phone");
                String organCode = record.getStr("code");
                String idNo = record.getStr("idNO");
                idNo = DESUtil.decode(workKey,idNo);

                if(StringUtils.isBlank(username))
                {
                    continue;
                }

                boolean result = SSOService.me.userSync(token, username, password, name, phone, idNo,organCode);
                if (result) {
                    VDeptUser deptUser = VDeptUser.dao.findById(id);
                    deptUser.setIsSync("T");
                    deptUser.update();
                    i++;
                }
            }
        }
        System.out.println("结束同步人员数据，共同步"+i);
    }

}
