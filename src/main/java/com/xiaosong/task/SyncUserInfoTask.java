package com.xiaosong.task;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.api.websocket.WebSocketSyncData;
import com.xiaosong.common.web.sso.SSOService;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.model.VSysUser;
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
        List<VDeptUser> list = VDeptUser.dao.find("select * from "+ TableList.DEPT_USER + " where currentStatus='normal' and IFNULL(isSync,'F')!= 'T'");
        if(list!=null && list.size()>0) {
            String token = SSOService.me.getToken();
            for (VDeptUser record : list) {

                String username = record.getPhone();
                String password = "000000";
                String name = record.getRealName();
                String phone = record.getPhone();
                String organCode = null;

                if(StringUtils.isBlank(username))
                {
                    continue;
                }

                boolean result = SSOService.me.userSync(token, username, password, name, phone, organCode);
                if (result) {
                    record.setIsSync("T");
                    record.update();
                    i++;
                }
            }
        }
        System.out.println("结束同步人员数据，共同步"+i);
    }

}
