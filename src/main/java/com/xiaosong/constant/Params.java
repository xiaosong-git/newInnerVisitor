package com.xiaosong.constant;

import com.xiaosong.param.ParamService;

public class Params {


     public static String getAutoApproval()
     {
         String value = ParamService.me.findValueByName("autoApproval");
         return value;
     }

    public static String getStopAuthVerify()
    {
        String value = ParamService.me.findValueByName("stopAuthVerify");
        return value;
    }
    public static String getMaintenancePhone()
    {
        String value = ParamService.me.findValueByName("repairCall");
        return value;
    }


}
