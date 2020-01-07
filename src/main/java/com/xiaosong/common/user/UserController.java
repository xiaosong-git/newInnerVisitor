package com.xiaosong.common.user;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.xiaosong.compose.Result;
import com.xiaosong.model.VAppUser;
import com.xiaosong.util.ConsantCode;
import com.xiaosong.validate.user.AuthValidator;
import com.xiaosong.validate.user.PhoneValidator;


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
                renderJson(userService.loginByVerifyCode( appUser,get("code")));
                return;
            }else {
            renderJson( userService.login(appUser,get("sysPwd"),get("style")));
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

}
