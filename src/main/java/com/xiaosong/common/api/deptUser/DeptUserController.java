package com.xiaosong.common.api.deptUser;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.xiaosong.compose.Result;

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
     * 未确认记录
     * @param
     * @return renderText(JSON.toJSONString(
     */
//    public void findApplying(){
//        try {
//            Map<String,Object> paramMap = getParamsToMap(request);
//             deptUserService.findApplying(paramMap);
//        }catch (Exception e){
//            e.printStackTrace();
//             Result.unDataResult("fail", "系统异常");
//        }
//    }

    /**
     * 修改状态
     * @param 
     * @return renderText(JSON.toJSONString(
     */
    
//    public void updateStatus(){
//        try {
//
//            renderText(JSON.toJSONString(deptUserService.updateStatus(paramMap)));
//        }catch (Exception e){
//            e.printStackTrace();
//            renderText(JSON.toJSONString(Result.unDataResult("fail", "系统异常")));
//        }
//    }

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

//    /**
//     * 确认记录
//     * @param request
//     * @return renderText(JSON.toJSONString(
//     */
//
//    public void findApplySucByOrg(){
//        try {
//            Map<String,Object> paramMap = getParamsToMap(request);
//            return renderText(JSON.toJSONString( deptUserService.findApplySucByOrg(paramMap);
//        }catch (Exception e){
//            e.printStackTrace();
//            return renderText(JSON.toJSONString( Result.unDataResult("fail", "系统异常");
//        }
//    }
//    /**
//     * 确认大楼全部记录
//     * @param request
//     * @return renderText(JSON.toJSONString(
//     */
//
//    public Result findApplyAllSucByOrg(){
//        try {
//            Map<String,Object> paramMap = getParamsToMap(request);
//            return renderText(JSON.toJSONString( deptUserService.findApplyAllSucByOrg(paramMap);
//        }catch (Exception e){
//            e.printStackTrace();
//            return renderText(JSON.toJSONString( Result.unDataResult("fail", "系统异常");
//        }
//    }
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
