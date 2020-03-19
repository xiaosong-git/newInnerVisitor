package com.xiaosong.validate.user;

import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.validate.Validator;
import com.xiaosong.compose.Result;

/**
 * @program: XiaoSong
 * @description: auth
 * @author: cwf
 * @create: 2020-01-06 14:02
 **/
public class AuthValidator extends Validator {
    @Override
    protected void validate(Controller c) {
        setRet(Ret.fail("sign","fail"));
        validateRequiredString("userId", "desc", "用户参数缺失");
        validateRequiredString("idNO", "desc", "身份证不能为空");
        validateRequiredString("realName", "desc", "真实姓名不能为空");
        validateRequiredString("idHandleImgUrl", "desc", "图片上传失败，请稍后再试!");
    }
    @Override
    protected void handleError(Controller c) {
       Result result=new Result();
        result.setVerify(getRet());
        c.renderJson(result);
    }
}
