package com.xiaosong.common.api.user;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.xiaosong.compose.Result;
import com.xiaosong.model.VDeptUser;
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
    @Clear
    @Before(PhoneValidator.class)
    public void login(){
        VDeptUser deptUser=getBean(VDeptUser.class,"",true);
        try {
            if (get("code")!=null) {
                renderText(JSON.toJSONString(userService.loginByVerifyCode(deptUser, get("code"))));
                return;
            }else {
            renderText(JSON.toJSONString(userService.login(deptUser,get("sysPwd"),getInt("style"))) );
            }
        }catch (Exception e){
            log.error("登入异常",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }
    @Before(AuthValidator.class)
    public void verify(){
        VDeptUser appUser=getBean(VDeptUser.class,"",true);
        try {
            renderText(JSON.toJSONString(userService.verify(appUser, get("userId"))));
        }catch (Exception e){
            log.error(e.getMessage());
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }

    public void isVerify(){
        VDeptUser appUser=getBean(VDeptUser.class,"",true);
        try {
            if (userService.isVerify((get("userId")))) {
                renderText(JSON.toJSONString(Result.unDataResult("success", "已经实名验证")));
            } else {
                renderText(JSON.toJSONString(Result.unDataResult("fail", "还未实名验证")));
            }
        }catch (Exception e){
            log.error(e.getMessage());
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }

    /**
     * 忘记密码。通过短信修改密码
     */
    public void forget(){
        try {
            renderText(JSON.toJSONString(userService.forget(get("code"), get("phone"), get("sysPwd"))));
        }catch (Exception e){
            log.error(e.getMessage());
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }
    /**
     * 获取用户信息（通过UserId,Token）
     * @return jsonString
     */
    public void getUser(){
        try {
            renderText(JSON.toJSONString((userService.getUserByUserToken(get("userId"), get("token")))));
        }catch (Exception e){
            e.printStackTrace();
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }

    /**
     * 校验用户存在
     */
    public void checkPhone(){
        try {
            renderText(JSON.toJSONString((userService.checkPhone(get("phone")))));
        }catch (Exception e){
            e.printStackTrace();
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }
    public void index(){
        renderText(JSON.toJSONString((userService.getUserByUserToken(get("userId"), get("token")))));
    }
}
