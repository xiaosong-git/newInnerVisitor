package com.xiaosong.task;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.api.websocket.WebSocketSyncData;
import com.xiaosong.common.web.sso.SSOService;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VSysUser;

import java.util.List;

/**
 * Created by CNL on 2020/9/15.
 */
public class SyncUserInfoTask extends  Thread {

    //定时任务发送数据到上位机
    @Override
    public void run()
    {
        List<VSysUser> list = VSysUser.dao.find("select * from "+ TableList.SYS_USER + " where isSync != 'T'");
        String token = SSOService.me.getToken();
        for(VSysUser record : list)
        {
            String username = record.getUsername();
            String password= "000000";
            String name= record.getTrueName();
            String phone= record.getTel();
            String organCode= null;
            boolean result = SSOService.me.userSync(token,username,password,name,phone,organCode);
            if(result)
            {
                record.update();
            }
        }
    }

}
