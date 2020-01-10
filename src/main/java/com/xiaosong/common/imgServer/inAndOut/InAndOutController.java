package com.xiaosong.common.imgServer.inAndOut;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.xiaosong.compose.Result;
import com.xiaosong.util.ConsantCode;

/**
 * @program: jfinal_demo_for_maven
 * @description: 进出日志上传
 * @author: cwf
 * @create: 2020-01-08 16:56
 **/
public class InAndOutController extends Controller {
    Log log =Log.getLog(InAndOutController.class);

    //开始使用
    public void index(){

        renderJson(true);
    }
    public void save() {
        try {
            renderJson(InAndOutService.me.save(getFile(),get("orgCode"),get("pospCode"),get("sign")));
        }catch (Exception e){
            log.error(e.getMessage());
            renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
        }
    }
    public void test(){
        try {
            renderJson(InAndOutService.me.test(get("path")));
        }catch (Exception e){
            log.error(e.getMessage());
            renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
        }
    }
}
