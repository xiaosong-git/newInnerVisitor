package com.xiaosong.common.api.foreign;


import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.xiaosong.compose.Result;
import com.xiaosong.validate.foreign.ForeginValidator;

/**
 * @Author linyb
 * @Date 2017/4/3 15:50
 */

public class ForeignController extends Controller {

    @Inject
    private ForeignService foreignService;

    /**
     * 获取访问记录
     * @return
     */
    @Before(ForeginValidator.class)
    public void newFindOrgCode(){
        try {

            renderText(JSON.toJSONString(foreignService.findOrgCode(get("orgCode"), getInt("pageNum"), getInt("pageSize"))));

        }catch (Exception e){
            e.printStackTrace();
            renderText(JSON.toJSONString(Result.unDataResult("fail", "系统异常")));
        }
    }

    /**
     * 确认访问数据新接口
     * @param
     * @return
     */

    public void newFindOrgCodeConfirm(){

        try {
            renderText(JSON.toJSONString(foreignService.newFindOrgCodeConfirm(get("pospCode"), get("orgCode"), get("idStr"))));
        }catch (Exception e){
            e.printStackTrace();
            renderText(JSON.toJSONString(Result.unDataResult("fail", "系统异常")));
        }
    }



}
