package com.xiaosong.common.visitDevice;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.json.Json;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.api.visitorRecord.VisitorRecordService;
import com.xiaosong.common.web.dept.DeptService;
import com.xiaosong.common.web.deptUser.DeptUserService;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.model.VVisitorRecord;
import com.xiaosong.util.AuthUtil;
import com.xiaosong.util.Base64;
import com.xiaosong.util.DESUtil;
import com.xiaosong.util.FilesUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *  访客机对接接口
 *
 */
public class visitDeviceController  extends Controller {

    private DeptService deptService = DeptService.me;

    private DeptUserService deptUserService = DeptUserService.me;

    private VisitorRecordService visitorRecordService = VisitorRecordService.me;
    /**
     *  获取部门列表
     *
     */
    public void getAllDept(){

        String timestamp = getPara("timestamp");


        CommonResult result;
        try{
            List<Record> list = deptService.findDeptList();
            if(list.size()>0){
                result = new CommonResult(1,"获取部门列表成功",list);
            }else{
                result = new CommonResult(5,"获取列表失败，管理端未配置部门");
            }
            renderJson(result);
        }catch (Exception e){
            e.printStackTrace();
            result = new CommonResult(444,"服务器异常");
            renderJson(result);

        }
    }

    /**
     *  三要素身份验证
     *
     */
    public void verifyByThrEle(){
        String idCard = getPara("card_no");
        String name = getPara("name");
        String photo = getPara("photo");
        CommonResult result;
        if(idCard==null || name==null ||photo==null){
            result = new CommonResult(3,"参数不合法");
            renderJson(result);
            return;
        }
      try {
           JSONObject photoResult = AuthUtil.auth(idCard,name,photo);
           if ("00000".equals(photoResult.getString("return_code"))) {  //实人认证
             renderJson(new CommonResult(1,"身份核验成功"));
           }else{
               renderJson(new CommonResult(2,"身份核验失败"));
           }
        } catch (Exception e) {
           renderJson(new CommonResult(444,"服务器异常"));
           e.printStackTrace();
        }
    }

    public void getAllDeptUser(){
        String phone = getPara("phone");
        String name = getPara("name");
        String dept_id = getPara("dept_id");
        Integer page_size = getInt("page_size");
        Integer page_num = getInt("page_num");
        if(page_size ==null ||page_num == null){

        }
        Page<Record> list =  deptUserService.findUserList(phone,name,dept_id,page_num,page_size);
        renderJson(list);
    }

    public void requestVisit() throws ParseException {
        String staff_id = getPara("staff_id");  //员工ID
        String visitor_name = getPara("visitor_name");
        String visitor_phone = getPara("visitor_phone");
        String visitor_plate = getPara("visitor_plate");    //访客车牌
        String visitor_card_no = getPara("visitor_card_no");
        String visitor_sex = getPara("visitor_sex");
        String appoint_time = getPara("appoint_time");  //开始访问时间
        String visit_hours = getPara("visit_hours");       //访问时长,单位小时
        String visit_reason = getPara("visit_reason");
        String card_photo = getPara("card_photo");
        String scene_photo = getPara("scene_photo");
        String apply_type = getPara("apply_type");  //申请方式0：手机申请；1：二代证自助预约；2：无证自助预约；3：二代证人工预约；4：无证人工预约
        String retinues = getPara("retinues");  //随行人员
        String fileName ="";
        try {
            byte[] photoKey =   Base64.decode(scene_photo);
            fileName = visitor_name+".jpg";
            FilesUtils.getFileFromBytes(photoKey,"D:\\sts-space\\innervisitor\\", fileName);
        } catch (Exception e) {
            renderJson(new CommonResult(444,"系统错误"));
            e.printStackTrace();
            return;
        }
        Record record = Db.findFirst("select * from v_user_key");
        String idNo = DESUtil.encode(record.getStr("workKey"), visitor_card_no);
        VDeptUser vDeptUser = new VDeptUser();
        vDeptUser.setRealName(visitor_name)
                .setPhone(visitor_phone)
                .setCreateDate(getDateTime())
                .setAuthDate(getDate())
                .setIdNO(idNo)
                .setIdHandleImgUrl(fileName)
                .setSex(visitor_sex)
                .setStatus("applySuc")
                .setCurrentStatus("normal")
                .setUserType("visitor");
        vDeptUser.save();
        Record staff = deptUserService.findByStaffId(staff_id);
        VVisitorRecord visitorRecord = new VVisitorRecord();
        visitorRecord.setUserId(vDeptUser.getId())
                .setVisitDate(getDate())
                .setVisitTime(getTime())
                .setVisitorId(Long.valueOf(staff_id))
                .setReason(visit_reason)
                .setStartDate(appoint_time)
                .setEndDate(endDateTime(appoint_time,Float.valueOf(visit_hours)))
                .setCstatus("applying")
                .setRecordType(1)
                .setIsReceive("F")
                .setPlate(visitor_plate)
                .setVitype(apply_type)
                .setCompanyId(staff.getLong("deptId"))
                .setOrgCode(staff.getStr("org_id"));
        visitorRecord.save();
        if(retinues != null){
            List<RetinueEntity>  retinueEntities = JSON.parseArray(retinues,RetinueEntity.class);
            for(RetinueEntity retinueEntity : retinueEntities){
                try {
                    byte[] photoKey =   Base64.decode(retinueEntity.getScene_photo());
                    fileName = retinueEntity.getVisitor_name()+".jpg";
                    FilesUtils.getFileFromBytes(photoKey,"D:\\sts-space\\innervisitor\\", fileName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                VDeptUser retinueUser = new VDeptUser();
                retinueUser.setRealName(retinueEntity.getVisitor_name())
                        .setPhone(retinueEntity.getVisitor_phone())
                        .setCreateDate(getDateTime())
                        .setAuthDate(getDate())
                        .setIdNO(DESUtil.encode(record.getStr("workKey"), retinueEntity.getVisitor_card_no()))
                        .setIdHandleImgUrl(fileName)
                        .setSex(retinueEntity.getVisitor_sex())
                        .setStatus("applySuc")
                        .setCurrentStatus("normal")
                        .setUserType("visitor");
                vDeptUser.save();
            }
        }
        renderJson(new CommonResult(1,"访问申请发送成功"));
    }

    public void getVisitList(){

        String card_no = getPara("card_no");
        String phone = getPara("phone");
        List<Record> list = visitorRecordService.findValidList(card_no,phone,getDateTime());
        renderJson(new CommonResult(1,"获取成功",list));
    }




    private String getDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        String date = df.format(new Date()); // new Date()为获取当前系统时间
        return date;
    }

    private String getTime() {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");// 设置日期格式
        String date = df.format(new Date()); // new Date()为获取当前系统时间
        return date;
    }

    private String getDateTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");// 设置日期格式
        String date = df.format(new Date()); // new Date()为获取当前系统时间
        return date;
    }

    private String endDateTime(String dateTime,float pretime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date dt = sdf.parse(dateTime);
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);
        rightNow.add(Calendar.MINUTE, (int)(pretime * 60));
        return sdf.format(rightNow.getTime());
    }
}
