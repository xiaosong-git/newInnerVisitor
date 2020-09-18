package com.xiaosong.task;

import com.xiaosong.common.api.websocket.WebSocketSyncData;

/**
 * Created by CNL on 2020/9/15.
 */
public class SyncDataTask extends  Thread {

    //定时任务发送数据到上位机
    @Override
    public void run()
    {
        System.out.println("开始下发数据");
        WebSocketSyncData.me.sendVisitorData();
        WebSocketSyncData.me.sendStaffData();
        System.out.println("数据下发完成");
    }

}
