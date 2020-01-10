package com.xiaosong.common.api.visitorRecord;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.xiaosong.compose.Result;
import com.xiaosong.constant.Constant;
import com.xiaosong.util.ConsantCode;
import com.xiaosong.validate.user.UserIdValidator;


/**
 * @program: xiaosong
 * @description: 访客记录
 * @author: cwf
 * @create: 2020-01-03 15:42
 **/
public class VisitorRecordController extends Controller {
    private Log log = Log.getLog(VisitorRecordController.class);
    //邀约我的
    @Before(UserIdValidator.class)
    public void inviteMine(){
        Integer pageNum = getAttrForInt("pageNum");
        Integer pageSize = getAttrForInt("pageSize");
        try {
          renderJson(VisitorRecordService.me.invite(getLong("userId"),pageNum,pageSize, Constant.INVITE,"userId"));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
        }
    }
    //我的邀约
    @Before(UserIdValidator.class)
    @ActionKey("/visitor/visitorRecord/inviteRecord")
    public void myInvite(){
        Integer pageNum = getAttrForInt("pageNum");
        Integer pageSize = getAttrForInt("pageSize");
        try {
            renderJson(VisitorRecordService.me.invite(getLong("userId"),pageNum,pageSize, Constant.INVITE,"visitorId"));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
        }
    }
    //我的访问
    @Before(UserIdValidator.class)
    public void myVisit(){
        Integer pageNum = getAttrForInt("pageNum");
        Integer pageSize = getAttrForInt("pageSize");
        try {
            renderJson(VisitorRecordService.me.invite(getLong("userId"),pageNum,pageSize, Constant.VISITOR,"userId"));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
        }
    }
    //访问我的
    @Before(UserIdValidator.class)
    @ActionKey("/visitor/visitorRecord/visitRecord")
    public void visitMine(){
        Integer pageNum = getAttrForInt("pageNum");
        Integer pageSize = getAttrForInt("pageSize");
        try {
            renderJson(VisitorRecordService.me.invite(getLong("userId"),pageNum,pageSize, Constant.VISITOR,"visitorId"));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
        }
    }

    public void visit(){

        try {
            renderJson(VisitorRecordService.me.visit(getLong("userId"),get("phone"),get("realName"),get("startDate"),get("endDate"),get("reason")));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
        }
    }
    public void modifyCompanyFromId(){
        try {
            renderJson(VisitorRecordService.me.modifyCompanyFromId(get("id"),getLong("userId"),getLong("companyId"),get("cstatus"),get("answerContent")));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
        }
    }
    //访问我的人与visitmine一样
    @Before(UserIdValidator.class)
    public void visitMyPeople(){
        Integer pageNum = getAttrForInt("pageNum");
        Integer pageSize = getAttrForInt("pageSize");
        try {
            renderText(JSON.toJSONString(VisitorRecordService.me.invite(getLong("userId"), pageNum, pageSize, Constant.VISITOR, "visitorId")));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
        }
    }
    @Before(UserIdValidator.class)
    public void visitMyCompany(){
        Integer pageNum = getAttrForInt("pageNum");
        Integer pageSize = getAttrForInt("pageSize");
        try {
            renderText(JSON.toJSONString(VisitorRecordService.me.visitMyCompany(get("userId"), pageNum, pageSize)));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
        }
    }
}
