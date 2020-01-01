package com.xiaosong.validate.user;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class UserIdValidator extends Validator {

    @Override
    protected void validate(Controller c) {
//        validateRequiredString("name", "nameMsg", "请输入用户名");
        validateRequiredString("userId", "desc", "用户参数缺失");
    }
    @Override
    protected void handleError(Controller c) {
        c.renderJson(getRet());
//        c.renderJson(Result.unDataResult("fail","缺少手机号"));
    }
}
