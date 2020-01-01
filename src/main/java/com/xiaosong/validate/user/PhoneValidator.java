package com.xiaosong.validate.user;

import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.validate.Validator;

public class PhoneValidator extends Validator {

    @Override
    protected void validate(Controller c) {
        setRet(Ret.fail("sign","fail"));
        validateRequiredString("phone", "desc", "请输入手机号码");
    }
    @Override
    protected void handleError(Controller c) {
        c.renderJson(getRet());
    }
}
