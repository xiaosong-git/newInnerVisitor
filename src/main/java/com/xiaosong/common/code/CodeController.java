package com.xiaosong.common.code;

import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: jfinal_demo_for_maven
 * @description: 验证码
 * @author: cwf
 * @create: 2019-12-29 17:00
 **/
public class CodeController extends Controller {
    @Inject
    CodeService codeService ;
    Logger logger = LoggerFactory.getLogger(CodeController.class);
    //测试接口
    public void index(){
        codeService.verifyCode(get("phone"),get("code"),getInt("type"));
        renderText("haha");
    }
    public  void sendCode(){
        renderJson(codeService.sendMsg(get("phone"),getInt("type"),null,null,null,null));
    }
}
