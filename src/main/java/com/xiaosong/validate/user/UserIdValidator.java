package com.xiaosong.validate.user;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.validate.Validator;
import com.xiaosong.compose.Result;

public class UserIdValidator extends Validator {

    @Override
    protected void validate(Controller c) {
        setRet(Ret.fail("sign","fail"));
        validateRequiredString("userId", "desc", "用户参数缺失");
    }
    @Override
    protected void handleError(Controller c) {
        Result result=new Result();
        result.setVerify(getRet());
        c.renderJson(result);
    }
}
