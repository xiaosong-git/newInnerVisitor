package com.xiaosong.common.api.visitorRecord;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.xiaosong.compose.Result;
import com.xiaosong.constant.Constant;
import com.xiaosong.model.VAppUser;
import com.xiaosong.model.VOutVisitor;
import com.xiaosong.model.VVisitorRecord;
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
            renderText(JSON.toJSONString(VisitorRecordService.me.invite(getLong("userId"), pageNum, pageSize, Constant.INVITE, "userId")));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }
    //我的邀约
    @Before(UserIdValidator.class)
    @ActionKey("/visitor/visitorRecord/inviteRecord")
    public void myInvite(){
        Integer pageNum = getAttrForInt("pageNum");
        Integer pageSize = getAttrForInt("pageSize");
        try {
            renderText(JSON.toJSONString(VisitorRecordService.me.invite(getLong("userId"), pageNum, pageSize, Constant.INVITE, "visitorId")));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }
    //我的访问
    @Before(UserIdValidator.class)
    public void myVisit(){
        Integer pageNum = getAttrForInt("pageNum");
        Integer pageSize = getAttrForInt("pageSize");
        try {
            renderText(JSON.toJSONString(VisitorRecordService.me.invite(getLong("userId"), pageNum, pageSize, Constant.VISITOR, "userId")));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));

        }
    }
    //访问我的
    @Before(UserIdValidator.class)
    @ActionKey("/visitor/visitorRecord/visitRecord")
    public void visitMine(){
        Integer pageNum = getAttrForInt("pageNum");
        Integer pageSize = getAttrForInt("pageSize");
        try {
            renderText(JSON.toJSONString(VisitorRecordService.me.invite(getLong("userId"), pageNum, pageSize, Constant.VISITOR, "visitorId")));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }
    /**
     *
     *  非好友访问
     */
    @Before(Tx.class)
    public void visit() throws Exception {

        try {
            renderText(JSON.toJSONString(VisitorRecordService.me.visit(getLong("userId"), get("phone"), get("realName"), get("startDate"), get("endDate"), get("reason"))));

        }catch (Exception e){

            log.error("系统异常：",e.getMessage());
            if (e.getMessage().equals("1")){
                renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "获取云端信息错误")));
            }else {
                renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
            }
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
            renderText(JSON.toJSONString(VisitorRecordService.me.receiveOutVisit(visitorRecord, vOutVisitor)));

        }catch (Exception e){
            log.error("系统异常：",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));

        }
    }
    public void modifyCompanyFromId(){
        try {
            renderText(JSON.toJSONString(VisitorRecordService.me.modifyCompanyFromId(get("id"), getLong("userId"), getLong("companyId"), get("cstatus"), get("answerContent"))));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
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
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
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
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }
    public void findRecordFromId(){
        try {
            renderText(JSON.toJSONString(VisitorRecordService.me.findRecordFromId(get("id"))));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }

    /**
     *根据条件判断访问我的人，邀约我的人。。。。
     */
    public void visitorList(){
        try {
            renderText(JSON.toJSONString(VisitorRecordService.me.invite(getLong("userId"), getInt("pageNum"), getInt("pageSize"), getInt("recordType"),get("condition"))));
        }catch (Exception e){
            log.error("系统异常：",e);
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }
    /**
     * 非好友邀约
     */
}
