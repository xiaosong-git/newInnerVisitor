package com.xiaosong.common.api.visitorRecord;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.xiaosong.common.api.user.UserService;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.constant.Constant;
import com.xiaosong.interceptor.apiInterceptor.AuthCheckAnnotation;
import com.xiaosong.model.VOutVisitor;
import com.xiaosong.model.VVisitorRecord;
import com.xiaosong.util.ConsantCode;
import com.xiaosong.validate.user.UserIdValidator;

import java.util.Map;


/**
 * @program: xiaosong
 * @description: 访客记录
 * @author: cwf
 * @create: 2020-01-03 15:42
 **/
public class VisitorRecordController extends Controller {
    private Log log = Log.getLog(VisitorRecordController.class);
    @Inject
    VisitorRecordService visitorRecordService;
    /**
     *  非好友访问
     */
    @AuthCheckAnnotation(checkLogin = true,checkVerify = false, checkRequestLegal = true)
    public void visit() throws Exception {

        try {
            renderText(JSON.toJSONString(visitorRecordService.visit(getLong("userId"), get("phone"), get("realName"), get("startDate"), get("endDate"), get("reason"),get("carNumber"),get("entourages"))));

        }catch (Exception e){

            log.error("系统异常：",e.getMessage());
                renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, e.getMessage())));
            throw e;
        }
    }

    /**
     * 接收外部访问
     */

    public void receiveOutVisit(){
        VVisitorRecord visitorRecord=getBean(VVisitorRecord.class,"",true);
        VOutVisitor vOutVisitor=getBean(VOutVisitor.class,"",true);
        try {
            renderText(JSON.toJSONString(visitorRecordService.receiveOutVisit(visitorRecord, vOutVisitor)));

        }catch (Exception e){
            log.error("系统异常：",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));

        }
    }
    @AuthCheckAnnotation(checkLogin = true,checkVerify = false, checkRequestLegal = true)
    public void modifyCompanyFromId(){
        try {
            renderText(JSON.toJSONString(visitorRecordService.modifyCompanyFromId(get("id"), getLong("userId"), getLong("companyId"), get("cstatus"), get("answerContent"))));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }
    //访问我的人与visitmine一样
    @AuthCheckAnnotation(checkLogin = true,checkVerify = false, checkRequestLegal = true)
    @Before(UserIdValidator.class)
    public void visitMyPeople(){
        Integer pageNum = getAttrForInt("pageNum");
        Integer pageSize = getAttrForInt("pageSize");
        try {
            renderText(JSON.toJSONString(visitorRecordService.invite(getLong("userId"), pageNum, pageSize, Constant.VISITOR, "visitorId")));

        }catch (Exception e){
            log.error("系统异常：",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }
    @AuthCheckAnnotation(checkLogin = true,checkVerify = false, checkRequestLegal = true)
    @Before(UserIdValidator.class)
    public void visitMyCompany(){
        Integer pageNum = getAttrForInt("pageNum");
        Integer pageSize = getAttrForInt("pageSize");
        try {
            renderText(JSON.toJSONString(visitorRecordService.visitMyCompany(get("userId"), pageNum, pageSize)));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }

    /**
     * 非好友访问
     */
    @AuthCheckAnnotation(checkLogin = true,checkVerify = false, checkRequestLegal = true)
    @Before(UserIdValidator.class)
    public void inviteStranger(){

        try {
            renderText(JSON.toJSONString(visitorRecordService.inviteStranger(getInt("userId"), get("phone"),get("realName"),get("startDate"),get("endDate"),get("reason"),getInt("companyId"),get("carNumber"),get("entourages"))));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }
    @AuthCheckAnnotation(checkLogin = true,checkVerify = false, checkRequestLegal = true)
    public void findRecordFromId(){
        try {
            renderText(JSON.toJSONString(visitorRecordService.findRecordFromId(get("id"))));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }

    /**
     *根据条件判断访问我的人，邀约我的人。。。。
     */
    @AuthCheckAnnotation(checkLogin = true,checkVerify = false, checkRequestLegal = true)
    public void visitorList(){
        try {
            renderText(JSON.toJSONString(visitorRecordService.invite(getLong("userId"), getInt("pageNum"), getInt("pageSize"), getInt("recordType"),get("condition"))));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }

    /**
     * 接收外部访问 需要内外网拦截器
     */
    public void innerVisitReceive(){
        VVisitorRecord visitorRecord=getBean(VVisitorRecord.class,"",true);
        try {
            renderText(JSON.toJSONString(visitorRecordService.innerVisitReceive(visitorRecord)));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }
    //@AuthCheckAnnotation(checkLogin = true,checkVerify = false, checkRequestLegal = true)
    public void visitReply(){
        VVisitorRecord visitorRecord=getBean(VVisitorRecord.class,"",true);
        try {
            renderText(JSON.toJSONString(visitorRecordService.visitReply(visitorRecord)));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }
    @AuthCheckAnnotation(checkLogin = true,checkVerify = false, checkRequestLegal = true)
    public void findRecordUser(){
        try {
            renderText(JSON.toJSONString(visitorRecordService.findRecordUser(getLong("userId"),getInt("pageNum"),getInt("pageSize"))));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }
    @AuthCheckAnnotation(checkLogin = true,checkVerify = false, checkRequestLegal = true)
    public void findRecordUserDetail(){
        try {
            renderText(JSON.toJSONString(visitorRecordService.findRecordUserDetail(getLong("userId"),getLong("visitorId"),getInt("pageNum"),getInt("pageSize"),getInt("recordType"))));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }
    @AuthCheckAnnotation(checkLogin = true,checkVerify = false, checkRequestLegal = true)
    public void visitorSucList(){
        try {
            renderText(JSON.toJSONString(visitorRecordService.visitorSucList(getInt("pageNum"),getInt("pageSize"),get("userId"))));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }



    @AuthCheckAnnotation(checkLogin = true,checkVerify = false, checkRequestLegal = true)
    @Before(UserIdValidator.class)
    public void findCarList(){
        Integer pageNum = getAttrForInt("pageNum");
        Integer pageSize = getAttrForInt("pageSize");
        try {
            renderText(JSON.toJSONString(visitorRecordService.findCarList(getLong("userId"), pageNum, pageSize)));

        }catch (Exception e){
            log.error("系统异常：",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }


    @AuthCheckAnnotation(checkLogin = true,checkVerify = false, checkRequestLegal = true)
    public void findCarFromId(){
        try {
            renderText(JSON.toJSONString(visitorRecordService.findCarFromId(get("id"))));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }


    @AuthCheckAnnotation(checkLogin = true,checkVerify = false, checkRequestLegal = true)
    @Before(UserIdValidator.class)
    public void approvalCar(){
        try {
            renderText(JSON.toJSONString(visitorRecordService.approvalCar(getLong("userId"),getLong("carId"),get("status"))));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }


}
