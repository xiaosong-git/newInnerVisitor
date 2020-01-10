package com.xiaosong.common.api.user;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.model.VAppUser;
import com.xiaosong.util.ConsantCode;
import com.xiaosong.validate.user.AuthValidator;
import com.xiaosong.validate.user.PhoneValidator;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * @program: innerVisitor
 * @description: 用户
 * @author: cwf
 * @create: 2019-12-27 16:32
 **/
public class UserController  extends Controller {
    private Log log = Log.getLog(UserController.class);
    @Inject
    UserService userService;
    @Before(PhoneValidator.class)
    public void login(){
        VAppUser appUser=getBean(VAppUser.class,"",true);
        try {
            if (get("code")!=null) {
                renderJson(JSON.toJSONString(userService.loginByVerifyCode(appUser, get("code"))));
                return;
            }else {
            renderText(JSON.toJSONString(userService.login(appUser,get("sysPwd"),getInt("style"))) );
            }
        }catch (Exception e){
            log.error(e.getMessage());
            renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
        }
    }
    @Before(AuthValidator.class)
    public void verify(){
        VAppUser appUser=getBean(VAppUser.class,"",true);
        try {
                renderJson(userService.verify(appUser,get("userId")));
        }catch (Exception e){
            log.error(e.getMessage());
            renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
        }
    }

    /**
     * 忘记密码。通过短信修改密码
     */
    public void forget(){
        try {
            renderJson(userService.forget(get("code"),get("phone"),get("sysPwd")));
        }catch (Exception e){
            log.error(e.getMessage());
            renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
        }
    }
    /**
     * 获取用户信息（通过UserId,Token）
     * @return jsonString
     */

    public void getUser(){
        try {
            renderText(JSON.toJSONString(userService.getUserByUserToken(get("userId"), get("token"))));
        }catch (Exception e){
            e.printStackTrace();
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }

}
