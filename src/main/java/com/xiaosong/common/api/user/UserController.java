package com.xiaosong.common.api.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Inject;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xiaosong.common.web.sso.SSOService;
import com.xiaosong.compose.Result;
import com.xiaosong.constant.Constant;
import com.xiaosong.interceptor.apiInterceptor.AuthCheckAnnotation;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.model.VSysUser;
import com.xiaosong.util.ConsantCode;
import com.xiaosong.validate.user.AuthValidator;
import com.xiaosong.validate.user.PhoneValidator;

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
    private Log log = Log.getLog(UserController.class);
    @Inject
    UserService userService;

    public SSOService srv = SSOService.me;

    @Clear
   // @Before(PhoneValidator.class)
    public void login(){
        VDeptUser deptUser=getBean(VDeptUser.class,"",true);
        try {
            String token = get("token");
            if (get("code")!=null) {
                renderText(JSON.toJSONString(userService.loginByVerifyCode(deptUser, get("code"))));
                return;
            }else if(token!=null) {

                //renderText(JSON.toJSONString(userService.loginByVerifyCode(token)));
                //根据token获取用信息
                String userInfo =srv.getUserInfoSync(token);
                JSONObject userJSON = JSONObject.parseObject(userInfo);
                if (userJSON != null) {
                    Result result =  userService.loginByToken(deptUser, userJSON);
                    renderText(JSON.toJSONString(result));
                } else {
                   throw new Exception("无效的token:"+token);
                }
            }
            else {
                renderText(JSON.toJSONString(userService.login(deptUser,get("sysPwd"),getInt("style"))) );
            }
        }catch (Exception e){
            log.error("登入异常",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, e.getMessage())));
        }
    }
    @AuthCheckAnnotation(checkLogin = true,checkRequestLegal = true)
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
    @AuthCheckAnnotation(checkLogin = true,checkVerify = false, checkRequestLegal = true)
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
             log.error(e.getMessage());
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
             log.error(e.getMessage());
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }

    /**
     * 修改密码
     */
    @AuthCheckAnnotation(checkLogin = true,checkVerify = false, checkRequestLegal = true)
    @ActionKey("/visitor/user/update/sysPwd")
    public void updatePassword(){

        try {

            renderText(JSON.toJSONString((userService.updatePassword(get("userId"),get("oldPassword"),get("newPassword")))));
        }catch (Exception e){
             log.error(e.getMessage());
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }

    }

    /**
     * 修改手机号
     */
    @AuthCheckAnnotation(checkLogin = true,checkRequestLegal = true)
    public void updatePhone(){
        try {
            renderText(JSON.toJSONString((userService.updatePhone(get("userId"),get("code"),get("phone")))));
        }catch (Exception e){
             log.error(e.getMessage());
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }

    public void index(){
        renderText(JSON.toJSONString((userService.getUserByUserToken(get("userId"), get("token")))));
    }

    public void nick(){
        try {
            VDeptUser appUser=getBean(VDeptUser.class,"",true);
            renderText(JSON.toJSONString((userService.nick(getLong("userId"),appUser))));
        }catch (Exception e){
             log.error(e.getMessage());
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }
}
