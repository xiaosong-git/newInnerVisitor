package com.xiaosong.validate.code;

import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.validate.Validator;

/**
 * @program: xiaosong
 * @description: 判断验证码
 * @author: cwf
 * @create: 2019-12-28 10:48
 **/
public class codeValidator extends Validator {
    @Override
    protected void validate(Controller c) {
        setRet(Ret.fail("sign","fail"));
        validateRequiredString("code", "desc", "请输入验证码");
    }
    @Override
    protected void handleError(Controller c) {
        c.renderJson(getRet());
//        c.renderJson(Result.unDataResult("fail","缺少手机号"));
    }
}
