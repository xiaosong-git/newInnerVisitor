package com.xiaosong.common.user;

import com.jfinal.core.Controller;
import com.xiaosong.common.base.BaseController;
import com.xiaosong.common.compose.Result;
import com.xiaosong.util.ConsantCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: innerVisitor
 * @description: 用户
 * @author: cwf
 * @create: 2019-12-27 16:32
 **/
public class UserController  extends Controller {
    public UserService userService = UserService.me;
    Logger logger = LoggerFactory.getLogger(UserController.class);
    public void login(){
        try {
            if (get("code")!=null) {
                renderJson(userService.loginByVerifyCode(get("userId"), get("phone"),get("code")));
            }
//            renderJson( userService.login(get("userId")));
        }catch (Exception e){
            e.printStackTrace();
            renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
        }
    }
    public void aa(){
        String json = getRawData();
        System.out.println(json);
        renderJson(json);
    }
}
