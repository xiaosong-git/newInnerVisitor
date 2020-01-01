package com.xiaosong.common.user.userApp;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.xiaosong.common.compose.Result;
import com.xiaosong.util.ConsantCode;
import com.xiaosong.validate.user.PhoneValidator;
import com.xiaosong.validate.user.RealNameValidator;
import com.xiaosong.validate.user.UserIdValidator;
import org.apache.log4j.Logger;

/**
 * @program: jfinal_demo_for_maven
 * @description:
 * @author: cwf
 * @create: 2019-12-31 11:38
 **/
public class UserFriendController extends Controller {
    Logger logger = Logger.getLogger(UserFriendController.class);
    @Inject
    UserFriendService userFriendService;
    @ActionKey("/visitor/userAppRole/getRoleMenu")
    @Before(UserIdValidator.class)
    public void getRoleMenu(){
        try {
            renderJson(userFriendService.getRoleMenu(getLong("userId")));
            return;
        }catch (Exception e){
            logger.error(e.getMessage());
            renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
        }
    }
    @ActionKey("/visitor/app/quit")
    @Before(UserIdValidator.class)
    public void appQuit(){
        try {
            renderJson(userFriendService.appQuit(getLong("userId")));
        }catch (Exception e){
            logger.error(e.getMessage());
            renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
        }
    }

    @Before(UserIdValidator.class)
    public void findUserFriend(){
        try {
            renderJson(userFriendService.findUserFriend(getLong("userId")));
        }catch (Exception e){
            logger.error(e.getMessage());
            renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
        }
    }
    @Before({PhoneValidator.class,UserIdValidator.class, RealNameValidator.class})
    public void addFriendByPhoneAndUser(){
        try {
            renderJson(userFriendService.addFriendByPhoneAndUser(get("userId"),get("phone"),get("realName"),get("remark")));
        }catch (Exception e){
            logger.error(e.getMessage());
            renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
        }
    }
}
