package com.xiaosong.common.api.foreign;


import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.xiaosong.compose.Result;

/**
 * @Author linyb
 * @Date 2017/4/3 15:50
 */

public class ForeignController extends Controller {

    @Inject
    private ForeignService foreignService;

    /**
     * 访问我的人新接口
     * @return
     */
    public void newFindOrgCode(){
        try {
            renderJson(foreignService.findOrgCode(get("pospCode"), get("orgCode"), getInt(" pageNum"), getInt("pageSize")));
        }catch (Exception e){
            e.printStackTrace();
            renderJson(Result.unDataResult("fail", "系统异常"));
        }
    }

    /**
     * 确认访问数据新接口
     * @param
     * @return
     */

    public void newFindOrgCodeConfirm(){

        try {
            renderJson(foreignService.newFindOrgCodeConfirm(get("pospCode"), get("orgCode"), get("idStr")));
        }catch (Exception e){
            e.printStackTrace();
            renderJson(Result.unDataResult("fail", "系统异常"));
        }
    }
}
