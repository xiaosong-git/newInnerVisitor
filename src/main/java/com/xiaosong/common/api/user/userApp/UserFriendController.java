package com.xiaosong.common.api.user.userApp;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.xiaosong.compose.Result;
import com.xiaosong.model.VUserFriend;
import com.xiaosong.util.ConsantCode;
import com.xiaosong.validate.user.FriendIdValidator;
import com.xiaosong.validate.user.PhoneValidator;
import com.xiaosong.validate.user.RealNameValidator;
import com.xiaosong.validate.user.UserIdValidator;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @program: xiaosong
 * @description:
 * @author: cwf
 * @create: 2019-12-31 11:38
 **/
public class UserFriendController extends Controller {
    private Log log = Log.getLog(UserFriendController.class);
    @Inject
    UserFriendService userFriendService;

    /**
     * 获取app菜单
     */
    @ActionKey("/visitor/userAppRole/getRoleMenu")
    @Before(UserIdValidator.class)
    public void getRoleMenu(){
        try {
            renderText(JSON.toJSONString(userFriendService.getRoleMenu(getLong("userId"))));
            return;
        }catch (Exception e){
            log.error(e.getMessage());
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }

    /**
     * 退出app
     */
    @ActionKey("/visitor/app/quit")
    @Before(UserIdValidator.class)
    public void appQuit(){
        try {
            renderText(JSON.toJSONString(userFriendService.appQuit(getLong("userId"))));
        }catch (Exception e){
            log.error(e.getMessage());
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }

    @Before(UserIdValidator.class)
    public void findUserFriend(){
        try {
            renderText(JSON.toJSONString(userFriendService.findUserFriend(getLong("userId"))));
        }catch (Exception e){
            log.error(e.getMessage());
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }
    @Before({PhoneValidator.class,UserIdValidator.class, RealNameValidator.class})
    public void addFriendByPhoneAndUser(){
        try {
           renderText(JSON.toJSONString(userFriendService.addFriendByPhoneAndUser(get("userId"),get("phone"),get("realName"),get("remark"))));
        }catch (Exception e){
            log.error(e.getMessage());
           renderText(JSON.toJSONString( Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }

    /**
     * 同意好友
     */
    public void agreeFriend(){
        VUserFriend userFriend=getBean(VUserFriend.class,"",true);
        try {
           renderText(JSON.toJSONString(userFriendService.agreeFriend(userFriend)));
        }catch (Exception e){
            log.error(e.getMessage());
           renderText(JSON.toJSONString( Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }
    //新的朋友
    public void newFriend(){
        try {
            renderText(JSON.toJSONString(userFriendService.newFriend(getLong("userId"), get("phoneStr"))));
        }catch (Exception e){
            log.error(e.getMessage());
           renderText(JSON.toJSONString( Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }
    @Before(FriendIdValidator.class)
    public void deleteUserFriend(){
        try {
            renderText(JSON.toJSONString(userFriendService.deleteUserFriend(getLong("userId"), getLong("friendId"))));
        }catch (Exception e){
            log.error(e.getMessage());
           renderText(JSON.toJSONString( Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }

    public void findFriendApplyMe(){
        try {
            renderText(JSON.toJSONString(userFriendService.findFriendApplyMe(getLong("userId"))));
        }catch (Exception e){
            log.error(e.getMessage());
            renderText(JSON.toJSONString(Result.unDataResult("fail", "系统异常")));

        }
    }
}
