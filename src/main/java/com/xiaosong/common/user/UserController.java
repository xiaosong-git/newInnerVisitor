package com.xiaosong.common.user;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.xiaosong.common.compose.Result;
import com.xiaosong.model.VAppUser;
import com.xiaosong.util.ConsantCode;
import com.xiaosong.validate.user.PhoneValidator;
import org.apache.log4j.Logger;


/**
 * @program: innerVisitor
 * @description: 用户
 * @author: cwf
 * @create: 2019-12-27 16:32
 **/
public class UserController  extends Controller {
    Logger logger = Logger.getLogger(UserController.class);
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
            logger.error(e.getMessage());
            renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
        }
    }

}
