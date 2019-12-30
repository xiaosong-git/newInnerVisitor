package com.xiaosong.common.user;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.xiaosong.common.compose.Result;
import com.xiaosong.model.VAppUser;
import com.xiaosong.util.ConsantCode;
import com.xiaosong.validate.login.LoginValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: innerVisitor
 * @description: 用户
 * @author: cwf
 * @create: 2019-12-27 16:32
 **/
public class UserController  extends Controller {
    @Inject
    UserService userService;
    @Inject
    UserAppRoleService userAppRoleService;
    Logger logger = LoggerFactory.getLogger(UserController.class);
    @Before(LoginValidator.class)
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
            e.printStackTrace();
            renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
        }
    }
    public void getRoleMenu(){

        try {
                renderJson(userAppRoleService.getRoleMenu(getLong("userId")));
                return;
        }catch (Exception e){
            e.printStackTrace();
            renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
        }
    }

}
