package com.xiaosong.common.user;

import com.xiaosong.common.compose.Result;
import com.xiaosong.util.BaseUtil;

import java.util.Map;

/**
 * @program: innerVisitor
 * @description: 用户接口
 * @author: cwf
 * @create: 2019-12-27 16:33
 **/
public class UserService {
    public static final UserService me = new UserService();
    //验证码登入
    public Result loginByVerifyCode(Map<String, Object> paramMap) {
        String phone = BaseUtil.objToStr(paramMap.get("phone"), null);//登录账号
        String code = BaseUtil.objToStr(paramMap.get("code"), null);//短信验证码
        return null;
    }
    //密码登入
    public Result login(Map<String, Object> paramMap) {
        return null;
    }

    public Result loginByVerifyCode(String userId, String phone, String code) {
        return Result.unDataResult("success","成功");
    }
}
