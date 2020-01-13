package com.xiaosong.interceptor.apiInterceptor;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.xiaosong.compose.Result;
import com.xiaosong.model.VAppUser;
import com.xiaosong.param.ParamService;
import com.xiaosong.util.RedisUtil;
import com.xiaosong.util.TokenUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @description: 登入验证，校验token
 * @author: cwf
 * @create: 2020-01-13 11:17
 **/
public class AuthCheckInteceptor implements Interceptor {
    Log log = Log.getLog(AuthCheckInteceptor.class);
    public void intercept(Invocation inv) {
        Controller con = inv.getController();
        String token = con.get("token");
        String userId = con.get("userId");
        AuthCheckAnnotation authCheck = inv.getMethod().getAnnotation(AuthCheckAnnotation.class);
        //检测token 与userId
        if (authCheck.checkLogin()) {
            if (StringUtils.isBlank(token) || StringUtils.isBlank(userId)) {
                con.renderText(JSON.toJSONString(Result.unDataResult("tokenFail", "请重新登录。")));
                return;
            }
            Integer apiAuthCheckRedisDbIndex = Integer.valueOf(ParamService.me.findValueByName("apiAuthCheckRedisDbIndex"));//缓存中的位置
            String userToken = null;
            //redis修改
            VAppUser appUser;
            userToken = RedisUtil.getStrVal(userId + "_token", apiAuthCheckRedisDbIndex);
            if (StringUtils.isBlank(userToken)) {
                //缓存中不存在Token，就从数据库中查询
                appUser = VAppUser.dao.findById(userId);
                userToken = appUser.getToken();
            }
            log.info("是否token正确：" + token.equals(userToken));
            log.info("userId：" + userId);
            log.info("token：" + token + ", userToken" + userToken);
            if (!token.equals(userToken)) {
                con.renderText(JSON.toJSONString(Result.unDataResult("tokenFail", "您的帐号在另一台设备登录，请重新登录。")));
                return;
            }
            /**
             * 实名验证
             */
            if (authCheck.checkVerify()) {
                String verifyKey = userId + "_isAuth";
                String isAuth = RedisUtil.getStrVal(verifyKey, apiAuthCheckRedisDbIndex);
                if (StringUtils.isBlank(isAuth)) {
                    //缓存中不存在，就从数据库中查询
                    appUser = new VAppUser();
                    isAuth = appUser.getIsAuth();
                }
                if (!"T".equalsIgnoreCase(isAuth)) {
                    con.renderText(JSON.toJSONString(Result.unDataResult("tokenFail", "您还未进行实名验证")));
                    return;
                }
            }
            /**
             * 验证请求合法性
             */
            if (authCheck.checkRequestLegal()) {
                String threshold = con.get("threshold");//客户端计算的Key
                String factor = con.get("factor");//客户端上传的时间，例如20170831143600
                if (StringUtils.isBlank(threshold) || StringUtils.isBlank(factor)) {
                    con.renderText(JSON.toJSONString(Result.unDataResult("tokenFail", "当前版本过低，请先到应用市场下载最新的版本")));
                    return;
                }
                try {
                    if (!TokenUtil.checkRequestLegal(userId, factor, token, threshold)) {
                        con.renderText(JSON.toJSONString(Result.unDataResult("userFail", "请求非法!")));
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        inv.invoke();
    }
}
