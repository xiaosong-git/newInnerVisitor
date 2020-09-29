package com.xiaosong.common.visitDevice;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.MainConfig;
import com.xiaosong.common.api.visitorRecord.VisitorRecordService;
import com.xiaosong.common.web.dept.DeptService;
import com.xiaosong.common.web.deptUser.DeptUserService;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.model.VVisitorRecord;
import com.xiaosong.util.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *  访客机对接接口
 *
 */
public class visitDeviceController  extends Controller {

    private DeptService deptService = DeptService.me;

    private DeptUserService deptUserService = DeptUserService.me;

    private VisitorRecordService visitorRecordService = VisitorRecordService.me;

    NonceData nonceData = NonceData.getInstance();
    /**
     *  获取部门列表
     *
     */
    public void getAllDept(){

        CommonResult result;
        String data = getPara("data");
        if(!isValued(data)){
            return;
        }
        try{
            List<Record> list = deptService.findDeptList();
            if(list.size()>0){
                result = new CommonResult(0,"操作成功",list);
            }else{
                result = new CommonResult(3,"无相关记录");
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

      try {
          String data = HttpKit.readData(getRequest());
          if(!isValued(data)){
              return;
          }
          JSONObject jsonObject = JSONObject.parseObject(data);
          String idCard = jsonObject.getString("card_no");
          String name = jsonObject.getString("name");
          String photo = jsonObject.getString("photo");
          CommonResult result;
          if(idCard.isEmpty() || name.isEmpty() ||photo.isEmpty()){
              result = new CommonResult(3,"参数不合法");
              renderJson(result);
              return;
          }
           JSONObject photoResult = AuthUtil.auth(idCard,name,photo);
           if ("00000".equals(photoResult.getString("return_code"))) {  //实人认证
               renderJson(new CommonResult(0,"操作成功"));
           }else{
               renderJson(new CommonResult(3,"无相关记录"));
           }
        } catch (Exception e) {
           renderJson(new CommonResult(444,"服务异常"));
           e.printStackTrace();
        }
    }

    public void getAllDeptUser(){
        try{
            String data = HttpKit.readData(getRequest());
            System.out.println(data);
            if(!isValued(data)){
                return;
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            String phone = jsonObject.getString("phone");
            String name = jsonObject.getString("name");
            String dept_id = jsonObject.getString("dept_id");
            Integer page_size = jsonObject.getInteger("page_size");
            Integer page_number = jsonObject.getInteger("page_number");
            if(page_size ==null ||page_number == null){
                renderJson(new CommonResult(1,"参数不完整"));
                return;
            }
            Map list =  deptUserService.findUserList(phone,name,dept_id,page_number,page_size);
            if(list.get("page_size").equals(0)){
                list.put("code",3);
                list.put("message","无相关记录");
            }else{
                list.put("code",0);
                list.put("message","操作成功");
            }
            renderJson(list);
        }catch (Exception e){
            renderJson(new CommonResult(444,"服务异常"));
            e.printStackTrace();
        }

    }

    public void requestVisit() throws ParseException {
        try {
            String data = HttpKit.readData(getRequest());
            if(!isValued(data)){
                return;
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            String staff_id = jsonObject.getString("staff_id");//员工ID
            String visitor_name = jsonObject.getString("visitor_name");
            String visitor_phone = jsonObject.getString("visitor_phone");
            String visitor_plate = jsonObject.getString("visitor_plate");  //访客车牌
            String visitor_card_no = jsonObject.getString("visitor_card_no");
            String visitor_sex = jsonObject.getString("visitor_sex");
            String appoint_time = jsonObject.getString("appoint_time"); //开始访问时间
            String visit_hours = jsonObject.getString("visit_hours"); //访问时长,单位小时
            String visit_reason = jsonObject.getString("visit_reason");
            String card_photo = jsonObject.getString("card_photo");
            String scene_photo = jsonObject.getString("scene_photo");
            String apply_type = jsonObject.getString("apply_type"); //申请方式0：手机申请；1：二代证自助预约；2：无证自助预约；3：二代证人工预约；4：无证人工预约
            String retinues = jsonObject.getString("retinues");  //随行人员

            String fileName ="";

            //判断被访者是否存在
            Record isStaff = deptUserService.confireNameAndIdNO(visitor_name,visitor_card_no);
            if(isStaff == null){

            }
            if(scene_photo == null){
                renderJson(new CommonResult<>(1,"参数不完整"));
                return;
            }
            byte[] photoKey =  Base64.decode(scene_photo);
            fileName = visitor_name+".jpg";
            FilesUtils.getFileFromBytes(photoKey,"D:\\sts-space\\innervisitor\\", fileName);

            Record record = Db.findFirst("select * from v_user_key");
            String idNo = DESUtil.encode(record.getStr("workKey"), visitor_card_no);

            //判断访客是否存在人员信息表
            VDeptUser vDeptUser = new VDeptUser();
            Record user = deptUserService.confireNameAndIdNO(visitor_name,idNo);
            Long userId;
            if(user == null){
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
                userId = vDeptUser.getId();
            }else{
                userId = user.getLong("id");
            }
            Record staff = deptUserService.findByStaffId(staff_id);
            if(staff == null){
                renderJson(new CommonResult<>(3,"该员工无相关记录"));
                return;
            }
            VVisitorRecord visitorRecord = new VVisitorRecord();
            visitorRecord.setUserId(userId)
                    .setVisitDate(getDate())
                    .setVisitTime(getTime())
                    .setVisitorId(Long.valueOf(staff_id))
                    .setReason(visit_reason)
                    .setStartDate(appoint_time)
                    .setCstatus("applying")
                    .setRecordType(1)
                    .setIsReceive("F")
                    .setVitype(apply_type)
                    .setCompanyId(staff.getLong("deptId"))
                    .setOrgCode(staff.getStr("org_id"));
            if(!visitor_plate.isEmpty()){
                visitorRecord.setPlate(visitor_plate);
            }
            String endDate = endDateTime(appoint_time,Float.valueOf(visit_hours));
            visitorRecord.setEndDate(endDate);
            visitorRecord.save();
            if(retinues != null){
                List<RetinueEntity>  retinueEntities = JSON.parseArray(retinues,RetinueEntity.class);
                for(RetinueEntity retinueEntity : retinueEntities){
                    try {
                        byte[] photoBase =   Base64.decode(retinueEntity.getScene_photo());
                        fileName = retinueEntity.getVisitor_name()+".jpg";
                        FilesUtils.getFileFromBytes(photoBase,"D:\\sts-space\\innervisitor\\", fileName);
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
            renderJson(new CommonResult(0,"操作成功"));

        } catch (Exception e) {
            renderJson(new CommonResult(444,"服务异常"));
            e.printStackTrace();
            return;
        }
    }

    public void getVisitList(){
        String data = HttpKit.readData(getRequest());
        if(!isValued(data)){
            return;
        }
        JSONObject jsonObject = JSONObject.parseObject(data);
        String card_no = jsonObject.getString("card_no");
        String phone = jsonObject.getString("phone");
        try {

            Record userR = deptUserService.findByIdNOOrPhone(card_no,phone);

            if(userR == null){
                renderJson(new CommonResult(444,"服务异常,访客未记录"));
                return;
            }
            Long userId = userR.getLong("id");


            List<Record> list = visitorRecordService.findValidList(userId,getDateTime());

            if(list.size()>0){
                renderJson(new CommonResult(0,"获取成功",list));
            }else{
                renderJson(new CommonResult(3,"无相关记录"));
            }
        }catch (Exception e){
            renderJson(new CommonResult(444,"服务异常"));
            e.printStackTrace();
        }
    }

    public void testService(){
        renderJson(new CommonResult(0,"请求成功"));
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

    /**
     *   签名核验
     *
     *  timestamp    时间戳（秒）
     *  nonce     随机数
     *  sign     发送过来的签名
     *  mySign    我解密的签名
     * @return
     */
    private boolean isValued(String data){

        String sign = getRequest().getHeader("sign");
        String nonce = getRequest().getHeader("nonce");
        if(sign == null ||nonce ==null ||getRequest().getHeader("timestamp") ==null){
            renderJson(new CommonResult(1,"参数不完整"));
            return false;
        }
        long timestamp = Long.valueOf(getRequest().getHeader("timestamp"));
        sign = sign.toUpperCase();

        String key = MainConfig.p.get("visitMachineKey");
        String mySign = SignUtils.getSign(timestamp,key,nonce,data);

        if(!mySign.equals(sign)){
            renderJson(new CommonResult(4,"请求不合法"));
            return false;
        }
        for(String str:nonceData.nonceList){
            if(nonce.equals(str)){
                renderJson(new CommonResult(7,"请求重复"));
                return false;
            }
        }
        nonceData.nonceList.add(nonce);
        long dif = System.currentTimeMillis()/1000 - timestamp;
        long difminutes = dif/60;
        if( difminutes > 5 ){
            renderJson(new CommonResult(6,"请求已过期"));
            return false;
        }
        return true;
    }
}
