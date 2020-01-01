package com.xiaosong.validate.user;

import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.validate.Validator;

public class RealNameValidator extends Validator {

    @Override
    protected void validate(Controller c) {
        setRet(Ret.fail("sign","fail"));
        validateRequiredString("realName", "desc", "请输入真实姓名");
    }
    @Override
    protected void handleError(Controller c) {
        c.renderJson(getRet());
    }
}
