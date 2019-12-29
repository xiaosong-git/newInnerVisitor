package com.xiaosong.validate.login;

import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.validate.Validator;

public class LoginValidator extends Validator {

    @Override
    protected void validate(Controller c) {
//        validateRequiredString("name", "nameMsg", "请输入用户名");
        setRet(Ret.fail("sign","fail"));
        validateRequiredString("phone", "desc", "请输入手机号码");
//        addError("msg", "昵称已被注册，请使用别的昵称！");
    }
    @Override
    protected void handleError(Controller c) {
        c.renderJson(getRet());
//        c.renderJson(Result.unDataResult("fail","缺少手机号"));
    }
}
