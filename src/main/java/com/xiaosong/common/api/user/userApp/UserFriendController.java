package com.xiaosong.common.api.user.userApp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.xiaosong.compose.Result;
import com.xiaosong.interceptor.apiInterceptor.AuthCheckAnnotation;
import com.xiaosong.model.VUserFriend;
import com.xiaosong.util.ConsantCode;
import com.xiaosong.validate.user.FriendIdValidator;
import com.xiaosong.validate.user.PhoneValidator;
import com.xiaosong.validate.user.RealNameValidator;
import com.xiaosong.validate.user.UserIdValidator;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
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
            renderText(JSON.toJSONString(userFriendService.getRoleMenu(getLong("userId")),SerializerFeature.WriteNullStringAsEmpty));
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
            renderText(JSON.toJSONString(userFriendService.appQuit(getLong("userId")),SerializerFeature.WriteNullStringAsEmpty));
        }catch (Exception e){
            log.error(e.getMessage());
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }
    @AuthCheckAnnotation(checkLogin = true,checkVerify = true, checkRequestLegal = true)
    @Before(UserIdValidator.class)
    public void findUserFriend(){
        try {
            renderText(JSON.toJSONString(userFriendService.findUserFriend(getLong("userId")),SerializerFeature.WriteNullStringAsEmpty));
        }catch (Exception e){
            log.error(e.getMessage());
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }





    @AuthCheckAnnotation(checkLogin = true,checkVerify = true, checkRequestLegal = true)
    @Before({PhoneValidator.class,UserIdValidator.class, RealNameValidator.class})
    public void addFriendByPhoneAndUser(){
        try {
           renderText(JSON.toJSONString(userFriendService.addFriendByPhoneAndUser(get("userId"),get("phone"),get("realName"),get("remark")),SerializerFeature.WriteNullStringAsEmpty));
        }catch (Exception e){
            log.error(e.getMessage());
           renderText(JSON.toJSONString( Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }

    /**
     * 同意好友
     */
    @AuthCheckAnnotation(checkLogin = true,checkVerify = true, checkRequestLegal = true)
    public void agreeFriend(){
        VUserFriend userFriend=getBean(VUserFriend.class,"",true);
        try {
           renderText(JSON.toJSONString(userFriendService.agreeFriend(userFriend)));
        }catch (Exception e){
            log.error(e.getMessage());
           renderText(JSON.toJSONString( Result.unDataResult(ConsantCode.FAIL, "系统异常"),SerializerFeature.WriteNullStringAsEmpty));
        }
    }
    //新的朋友
    @AuthCheckAnnotation(checkLogin = true,checkVerify = false, checkRequestLegal = true)
    public void newFriend(){
        try {
            renderText(JSON.toJSONString(userFriendService.newFriend(getLong("userId"), get("phoneStr")),SerializerFeature.WriteNullStringAsEmpty));
        }catch (Exception e){
            log.error(e.getMessage());
           renderText(JSON.toJSONString( Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }
    @AuthCheckAnnotation(checkLogin = true,checkVerify = true, checkRequestLegal = true)
    @Before(FriendIdValidator.class)
    public void deleteUserFriend(){
        try {
            renderText(JSON.toJSONString(userFriendService.deleteUserFriend(getLong("userId"), getLong("friendId")),SerializerFeature.WriteNullStringAsEmpty));
        }catch (Exception e){
            log.error(e.getMessage());
           renderText(JSON.toJSONString( Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }
    @AuthCheckAnnotation(checkLogin = true,checkVerify = true, checkRequestLegal = true)
    public void findFriendApplyMe(){
        try {
            //WriteNullStringAsEmpty值只能判断字段（key）为空值，不能判断value为空值
            renderText(JSON.toJSONString(userFriendService.findFriendApplyMe(getLong("userId")),SerializerFeature.WriteNullStringAsEmpty) );
        }catch (Exception e){
            log.error(e.getMessage());
            renderText(JSON.toJSONString(Result.unDataResult("fail", "系统异常")));

        }
    }

    /**
     *
     *
     */
    public Map<String, Object> getParamsToMap(HttpServletRequest request) {

        Map<String,Object>  res = new HashMap<String,Object>();
        Map<String,String[]>  parameter = request.getParameterMap();
        Iterator<String> it = parameter.keySet().iterator();
        StringBuffer str=new StringBuffer();
        while(it.hasNext()){
            String key = it.next();
            String[]  val = parameter.get(key);
            if(val!=null&&val.length>0){
                if(val[0]!=null&&!"".equals(val[0])){
                    res.put(key, val[0].trim());
                    str.append(key+"="+ val[0].trim()+"\n");
                }
            }
        }
        log.info(str.toString());
        return res;
    }

    @AuthCheckAnnotation(checkLogin = true,checkVerify = true, checkRequestLegal = true)
    public Result findIsUserByPhone(HttpServletRequest request){
        try {
            Map<String,Object> paramMap = getParamsToMap(request);
            return userFriendService.findIsUserByPhone(paramMap);
        }catch (Exception e){
            e.printStackTrace();
            return Result.unDataResult("fail", "系统异常");
        }
    }
}
