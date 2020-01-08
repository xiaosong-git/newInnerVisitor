package com.xiaosong.common.api.param;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.param.ParamService;
import org.apache.commons.lang3.StringUtils;

/**
 * @program: jfinal_demo_for_maven
 * @description: 获取参数
 * @author: cwf
 * @create: 2020-01-08 10:29
 **/
public class ParamController extends Controller {
    @ActionKey("/visitor/param")
    public void getParamByName(){
        String paramName = getAttrForStr("paramName");
        renderJson(StringUtils.isNotBlank(paramName)
                ? ResultData.dataResult("success","获取成功",paramName)
                : Result.unDataResult("fail","参数名不存在"));
    }
}
