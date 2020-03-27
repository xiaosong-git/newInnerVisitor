package com.xiaosong.common.api.deptUser;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.xiaosong.common.access.companyUser.CompanyUserService;
import com.xiaosong.compose.Result;
import com.xiaosong.validate.foreign.ForeginValidator;

/**
 * @description: 原公司员工，现部门员工
 * @author: cwf
 * @create: 2020-01-10 17:37
 **/
public class DeptUserController extends Controller {
        Log log =Log.getLog(DeptUserController.class);
    @Inject
    private DeptUserService deptUserService;

    /**
     * 全部初始化数据
     */
    public void findApplySuc(){
        try {
            renderText(JSON.toJSONString(deptUserService.findApplySuc(get("userId"))));
        }catch (Exception e){
            e.printStackTrace();
            renderText(JSON.toJSONString(Result.unDataResult("fail", "系统异常")));

        }
    }

    /**
     * 查询访客所拥有的公司
     */

    public void findVisitComSuc(){
        try {
             renderText(JSON.toJSONString(deptUserService.findApplySuc(get("visitorId"))));
        }catch (Exception e){
            e.printStackTrace();
             renderText(JSON.toJSONString(Result.unDataResult("fail", "系统异常")));
        }
    }



}
