package com.xiaosong.common.api.work;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.xiaosong.common.api.visitorRecord.VisitorRecordController;
import com.xiaosong.compose.Result;
import com.xiaosong.interceptor.apiInterceptor.AuthCheckAnnotation;
import com.xiaosong.util.ConsantCode;

/**
 * @program: newInnerVisitor
 * @description: 考勤controller
 * @author: cwf
 * @create: 2020-03-02 16:54
 **/
public class WorkController extends Controller {
    private Log log = Log.getLog(VisitorRecordController.class);
    @Inject
    FlowService flowService;
    @Inject
    CheckInWorkService CheckInWorkService;
//    @AuthCheckAnnotation(checkLogin = false,checkVerify = false, checkRequestLegal = true)
//    public void saveGroup(){
//        try {
//            String s = HttpKit.readData(getRequest());
//            JSONObject jsonObject=JSON.parseObject(s);
//            renderText(JSON.toJSONString(CheckInWorkService.saveGroup(jsonObject)));
//        }catch (Exception e){
//            log.error("系统异常：",e);
//            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
//        }
//    }

    //    @AuthCheckAnnotation(checkLogin = false,checkVerify = false, checkRequestLegal = true)
//    public void gainWork(){
//        try {
//
//            renderText(JSON.toJSONString(CheckInWorkService.gainWork(getLong("userId"),getLong("companyId"),get("date"))));
//        }catch (Exception e){
//            log.error("系统异常：",e);
//            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
//        }
//    }
//    @AuthCheckAnnotation(checkLogin = false,checkVerify = false, checkRequestLegal = true)
//    public void gainMonthStatistics(){
//        try {
//
//            renderText(JSON.toJSONString(CheckInWorkService.gainMonthStatistics(get("date"),getLong("groupId"))));
//        }catch (Exception e){
//            log.error("系统异常：",e);
//            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
//        }
//    }
//    @AuthCheckAnnotation(checkLogin = false,checkVerify = false, checkRequestLegal = true)
//    public void gaindayStatistics(){
//        try {
//
//            renderText(JSON.toJSONString(CheckInWorkService.gaindayStatistics(get("dateTime"),getLong("groupId"))));
//        }catch (Exception e){
//            log.error("系统异常：",e);
//            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
//        }
//    }
//    @AuthCheckAnnotation(checkLogin = false, checkVerify = false, checkRequestLegal = true)
//    public void saveWork() {
//        try {
//            WkRecord wkRecord = getBean(WkRecord.class, "", true);
//
//            renderText(JSON.toJSONString(CheckInWorkService.saveWork(wkRecord)));
//        } catch (Exception e) {
//            log.error("系统异常：", e);
//            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
//        }
//    }
//
//    /**
//     * 统计
//     *
//     * @Author cwf
//     * @Date 2019/11/4 21:33
//     */
//    @AuthCheckAnnotation(checkLogin = false)
//
//    public void gainDay() {
//        try {
//            renderText(JSON.toJSONString(CheckInWorkService.gainDay(getLong("userId"), get("date"), getLong("companyId"))));
//        } catch (Exception e) {
//            e.printStackTrace();
//            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
//        }
//
//    }
}
