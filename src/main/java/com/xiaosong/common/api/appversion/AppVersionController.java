package com.xiaosong.common.api.appversion;

import com.alibaba.fastjson.JSON;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.xiaosong.compose.Result;

/**
 * Created by Administrator on 2017/5/21.
 */

public class AppVersionController extends Controller {
    Log log=Log.getLog(AppVersionController.class);
    /**
     * 安卓更新接口
     * @Author linyb
     * @Date 2017/5/4 21:33
     */

    public void updateAndroid(){
        try {
            String channel = getAttrForStr("channel");
            String versionNum = getAttrForStr("versionNum");
            renderText(JSON.toJSONString(appVersionService.me.updateAndroid("android", channel, new Integer(versionNum))));
        }catch (Exception e){
            log.error("更新接口错误",e);
            renderText(JSON.toJSONString(Result.unDataResult("fail", "更新系统错误")));
        }
    }

    /**
     * IOS更新接口
     * @Author linyb
     * @Date 2017/5/4 21:33
     */

    public void updateIOS(){
        try {
            renderText(JSON.toJSONString(appVersionService.me.updateIos("ios", get("channel"))));
        }catch (Exception e){
            log.error("更新接口错误",e);
            renderText(JSON.toJSONString(Result.unDataResult("fail", "更新系统错误")));
        }
    }

}
