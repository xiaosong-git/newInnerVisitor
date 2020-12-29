package com.xiaosong.common.visitDevice;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xiaosong.MainConfig;
import com.xiaosong.common.api.car.CarService;
import com.xiaosong.common.api.code.CodeService;
import com.xiaosong.common.api.utils.ApiDataUtils;
import com.xiaosong.common.api.visitorRecord.VisitorRecordService;
import com.xiaosong.common.api.websocket.WebSocketMonitor;
import com.xiaosong.common.api.websocket.WebSocketSyncData;
import com.xiaosong.common.web.blackUser.BlackUserService;
import com.xiaosong.common.web.dept.DeptService;
import com.xiaosong.common.web.deptUser.DeptUserService;
import com.xiaosong.common.web.login.LoginService;
import com.xiaosong.common.web.machine.MachineService;
import com.xiaosong.common.web.sysUser.SysUserService;
import com.xiaosong.constant.*;
import com.xiaosong.model.*;
import com.xiaosong.util.*;
import com.xiaosong.util.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 *  访客机对接接口
 *
 */
public class visitDeviceController  extends Controller {
    private Log log = Log.getLog(visitDeviceController.class);
    private DeptService deptService = DeptService.me;

    private DeptUserService deptUserService = DeptUserService.me;

    private VisitorRecordService visitorRecordService = VisitorRecordService.me;

    private BlackUserService blackUserService = BlackUserService.me;

    private String imageSaveDir = MainConfig.p.get("imageSaveDir");//图片保存路径

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
            result = new CommonResult(0,"操作成功",list);
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
           System.out.println("开始实人认证-----------------------------------------");
           JSONObject photoResult = AuthUtil.auth(idCard,name,photo);
           System.out.println("实人认证结果："+photoResult);
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



    /**
     *  邀约确认
     *
     */
    public void invitationConfirm(){
        try {
            String data = HttpKit.readData(getRequest());
            if(!isValued(data)){
                return;
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            String idCard = jsonObject.getString("card_no");
            String name = jsonObject.getString("name");
            String photo = jsonObject.getString("photo");
            Long userId = jsonObject.getLong("user_id");
            Long recordId = jsonObject.getLong("record_id");
            CommonResult result = null;
            List<String> QRCodeList = new ArrayList<>();

            if(StringUtils.isNotEmpty(idCard) && StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(photo) && userId != null) {

                Record record = Db.findFirst("select * from v_user_key");
                String workKey = record.getStr("workKey");
                byte[] photoKey = Base64.decode(photo);
                String fileName = name + ".jpg";
                File file = FilesUtils.getFileFromBytes(photoKey, imageSaveDir, fileName);
                String idNo = DESUtil.encode(workKey, idCard);
                //判断访客是否存在人员信息表
                VDeptUser vDeptUser = deptUserService.confireNameAndIdNO(name, idNo);

                if (vDeptUser!=null && "T".equals(vDeptUser.getIsAuth())) {
                    result = new CommonResult(3, "该身份证号已经实人认证");
                    renderJson(result);
                    return;
                }
                vDeptUser = VDeptUser.dao.findById(userId);

                if (vDeptUser == null) {
                    result = new CommonResult(3, "未找到该用户");
                    renderJson(result);
                    return;
                } else {
                    fileName = deptUserService.uploadUserImg(file.getAbsolutePath(), "" + userId);
                    vDeptUser.setIdHandleImgUrl(fileName);
                    vDeptUser.setIsAuth("T");
                    vDeptUser.setAuthDate(DateUtil.getCurDate());
                    vDeptUser.setRealName(name);
                    vDeptUser.setIdNO(idNo);
                    boolean r = vDeptUser.update();
                    if (r) {
                        file.delete();
                        String sqlWhere = " where recordType =2  and cstatus = 'applyConfirm' and userId =? and endDate>?";
                        Db.update("update v_visitor_record set cstatus ='applySuccess'"+ sqlWhere, userId, DateUtil.getSystemTime());
                        List<VVisitorRecord> vVisitorRecords = VVisitorRecord.dao.find("select * from v_visitor_record "+ sqlWhere, userId, DateUtil.getSystemTime());
                        if(vVisitorRecords!=null && vVisitorRecords.size()>0) {
                            vVisitorRecords.stream().forEach(x->{
                                addQRCode(QRCodeList, name, x);
                            });
                        }
                    }
                    result = new CommonResult(0,"操作成功",QRCodeList);
                    WebSocketSyncData.me.sendVisitorData();
                }

            }

            //传入记录ID，那么执行邀约确认否知执行实人认证
            if(recordId!=null)
            {
                VVisitorRecord vVisitorRecord = VVisitorRecord.dao.findById(recordId);
                if(vVisitorRecord == null)
                {
                    result = new CommonResult(3,"参数不合法");
                }
                else {
                    //访问类型只返回二维码内容，邀约类型并且未确认的修改未审核通过，并打印二维码
                    if(vVisitorRecord.getRecordType()!=1 && "applyConfirm".equals(vVisitorRecord.getCstatus()))
                    {
                        vVisitorRecord.setCstatus("applySuccess");
                        vVisitorRecord.update();
                        WebSocketSyncData.me.sendVisitorData();
                    }
                    VDeptUser vDeptUser =  VDeptUser.dao.findById(vVisitorRecord.getUserId());
                    if(vDeptUser!=null) {
                        addQRCode(QRCodeList, vDeptUser.getRealName(), vVisitorRecord);
                        result = new CommonResult(0, "操作成功", QRCodeList);
                    }else{
                        result = new CommonResult(4444, "服务异常");
                    }
                }
            }
            renderJson(result);

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

            list.put("code",0);
            list.put("message","操作成功");

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
            String visitor_cmp = jsonObject.getString("visitor_cmp");
            String visit_address = jsonObject.getString("visit_address");
            String card_photo = jsonObject.getString("card_photo");
            String scene_photo = jsonObject.getString("scene_photo");
            String apply_type = jsonObject.getString("apply_type"); //申请方式0：手机申请；1：二代证自助预约；2：无证自助预约；3：二代证人工预约；4：无证人工预约
            String retinues = jsonObject.getString("retinues");  //随行人员
            BigInteger createUserId = jsonObject.getBigInteger("create_user_id");
            String machineCode = jsonObject.getString("machine_code");


            if(scene_photo.isEmpty() || visitor_name.isEmpty() || visitor_phone.isEmpty() ||visitor_card_no.isEmpty()||appoint_time.isEmpty()){
                renderJson(new CommonResult<>(1,"参数不完整"));
                return;
            }
            Record record = Db.findFirst("select * from v_user_key");
            String workKey = record.getStr("workKey");

            VDeptUser staff = VDeptUser.dao.findById(staff_id);
            if(staff == null){
                renderJson(new CommonResult<>(3,"该员工无相关记录"));
                return;
            }
            List<HashMap<String,Object>> visitorList = new ArrayList<>();
            List<String> QRCodeList = new ArrayList<>();
            String staffName = staff.getRealName();
            String staffPhone = staff.getPhone();
            boolean result =  Db.tx(()->{
                staff.setAddr(visit_address);
                staff.update();
                VVisitorRecord vVisitorRecord =  addVisitor(visitorList,workKey,visitor_name,visitor_card_no,visitor_phone,visitor_sex,scene_photo,visit_reason,appoint_time,apply_type,visitor_plate,visit_hours,null,visitor_cmp,staff,createUserId,machineCode);
                addQRCode(QRCodeList,visitor_name,vVisitorRecord);

                if(vVisitorRecord!= null && retinues != null){
                    List<RetinueEntity>  retinueEntities = JSON.parseArray(retinues,RetinueEntity.class);
                    for(RetinueEntity retinueEntity : retinueEntities){
                        String retinuePhoto = retinueEntity.getScene_photo();
                        String retinueVisitorName = retinueEntity.getVisitor_name();
                        String retinueVisitorCardNo = retinueEntity.getVisitor_card_no();
                        String retinueVisitorPhone= retinueEntity.getVisitor_phone();
                        String retinueVisitorSex= retinueEntity.getVisitor_sex();
                        VVisitorRecord retinueRecord = addVisitor(visitorList,workKey,retinueVisitorName,retinueVisitorCardNo,retinueVisitorPhone,retinueVisitorSex,retinuePhoto,visit_reason,appoint_time,apply_type,visitor_plate,visit_hours,vVisitorRecord.getId(),visitor_cmp,staff,createUserId,machineCode);
                        addQRCode(QRCodeList,retinueVisitorName,retinueRecord);
                    }
                }
                CarService.me.addCarInfo(vVisitorRecord);
                return  true ;
            });

            if(result) {
                if (!"T".equals(Params.getAutoApproval()) && ("1".equals(apply_type) || "2".equals(apply_type))) //自动审核不发送请求审核短信
                {
                    CodeService.me.pushMsg(staff, YunPainSmsUtil.MSG_TYPE_VERIFY, null, null, appoint_time, visitor_name);
                }
                else{
                    for(HashMap<String,Object> map : visitorList)
                    {
                        String visitorResult = map.get("visitorResult").toString();
                        if("applySuccess".equals(visitorResult))
                        {
                            visitorResult ="审核通过";
                        }else{
                            visitorResult="审核不通过";
                        }
                        CodeService.me.pushMsg((VDeptUser)map.get("visitor"), YunPainSmsUtil.MSG_TYPE_VISITORBY_QRCODE, visitorResult, staffName, map.get("visitorDateTime").toString(), null);
                    }
                }
                WebSocketSyncData.me.sendVisitorData();
                WebSocketMonitor.me.getVisitorData();
                renderJson(new CommonResult(0,"操作成功", QRCodeList));
            }else{
                throw new Exception("添加失败");
            }

        } catch (Exception e) {
            renderJson(new CommonResult(444,"服务异常"));
            e.printStackTrace();
            return;
        }
    }





    public void getVisitList() {
        String data = HttpKit.readData(getRequest());
        if (!isValued(data)) {
            return;
        }
        JSONObject jsonObject = JSONObject.parseObject(data);
        String card_no = jsonObject.getString("card_no");
        String phone = jsonObject.getString("phone");
        String query_type = jsonObject.getString("query_type");
        Long createUserId = jsonObject.getLong("create_user_id");
        String machineCode = jsonObject.getString("machine_code");
        Integer recordType = jsonObject.getInteger("record_type");

        try {

            List<Record> list = null;
            Long userId =null;

            if(!"2".equals(query_type)) {
                Record userR = deptUserService.findByIdNOOrPhone(card_no, phone);
                if (userR == null) {
                    renderJson(new CommonResult(3, "无相关记录"));
                    return;
                }
                userId = userR.getLong("id");
            }
            if ("1".equals(query_type)) {
                list = new ArrayList<>();
                List<Long> visitorIds = new ArrayList<>();
                List<Record> findList = visitorRecordService.findValidList(userId, null,null,null,"desc");
                for (Record record : findList) {
                    Long visitorId = record.getLong("staff_id");
                    if (list.size() >= 9) {
                        break;
                    }
                    if (!visitorIds.contains(visitorId)) {
                        list.add(record);
                        visitorIds.add(visitorId);
                    }
                }

            }else if("2".equals(query_type)) {
                Integer page_size = jsonObject.getInteger("page_size");
                Integer page_number = jsonObject.getInteger("page_number");
                if(page_size ==null ||page_number == null){
                    renderJson(new CommonResult(1,"参数不完整"));
                    return;
                }
                Page<Record> recordPage = visitorRecordService.findValidListPage(page_number,page_size,userId,phone,createUserId,machineCode,recordType,"desc");
                if(recordPage!=null) {
                    HashMap map = new HashMap();
                    map.put("total", recordPage.getTotalRow());
                    map.put("page_number", recordPage.getPageNumber());
                    map.put("page_size", recordPage.getPageSize());
                    map.put("data", ApiDataUtils.apiList(recordPage.getList()));
                    map.put("code",0);
                    map.put("message","获取成功");
                    renderJson( map);
                }
                else
                {
                    renderJson(new CommonResult(3, "无相关记录"));
                }
                return;
            }
            else {
                list = visitorRecordService.findValidList(userId, getDateTime(),createUserId,machineCode,"asc");
            }
            renderJson(new CommonResult(0, "获取成功", list));
        } catch (Exception e) {
            renderJson(new CommonResult(444, "服务异常"));
            e.printStackTrace();
        }
    }



    public void login() {
        String data = HttpKit.readData(getRequest());
        if (!isValued(data)) {
            return;
        }
        try {
            JSONObject jsonObject = JSONObject.parseObject(data);
            Map<String, Object> map = new HashMap<String, Object>();
            String username = jsonObject.getString("user_name");
            String password = jsonObject.getString("password");
            password = MD5Util.MD5(password);
            VSysUser user = LoginService.me.checkLoginUser(username, password);
            if(user!=null) {
                map.put("user_name", username);
                map.put("real_name", user.getTrueName());
                map.put("role_id", user.getRoleId());
                map.put("user_id", user.getId().toString());
                renderJson(new CommonResult(0,"登录成功",map));
            }else {
                renderJson(new CommonResult(3, "无相关记录"));
            }
        }
        catch (Exception ex)
        {
            renderJson(new CommonResult(444, "服务异常"));
        }

    }



    public void editPwd() throws Exception {
        String data = HttpKit.readData(getRequest());
        if (!isValued(data)) {
            return;
        }
        JSONObject jsonObject = JSONObject.parseObject(data);
        Long id = jsonObject.getLong("user_id");
        String oldPwd =jsonObject.getString("old_pwd");
        String newPwd = jsonObject.getString("new_pwd");
        oldPwd = MD5Util.MD5(oldPwd);
        newPwd = MD5Util.MD5(newPwd);
        List<VSysUser> sysuser = LoginService.me.checkPwd(id, oldPwd);
        if(sysuser!=null&&sysuser.size()>0) {
            if(LoginService.me.editPwd(id, newPwd)) {
                renderJson(new CommonResult(0,"操作成功"));
            }else {
                renderJson(new CommonResult(3,"修改失败"));
            }
        }else {
            renderJson(new CommonResult(3,"密码错误"));
        }

    }


    public void getMachineList(){
        String data = HttpKit.readData(getRequest());
        if (!isValued(data)) {
            return;
        }
        List<VMachine> machineList = MachineService.me.getMachineList();
        renderJson(new CommonResult(0,"操作成功",machineList));
    }


    public void statVisitorRecord() {

        String data = HttpKit.readData(getRequest());
        if (!isValued(data)) {
            return;
        }
         List<Record> list =  VisitorRecordService.me.statVisitorRecordToday();
         int visitCount = 0;
         int inviteCount=0;
         HashMap<String,Object> resultMap = new HashMap<>();
         if(list!=null && list.size()>0)
         {
             List<HashMap<String,Object>> detailList = new ArrayList<>();
             for(Record record : list){
                 HashMap<String,Object> map = new HashMap<>();
                 int num = record.getInt("num");
                 int recordType = record.getInt("recordType");
                 String orgName = record.getStr("org_name");
                 if(recordType == 1) {
                     visitCount += num;
                     map.put("org_name",orgName);
                     map.put("num",num);
                     detailList.add(map);
                 }
                 else if(recordType == 2)
                 {
                     inviteCount +=num;
                 }

             }
             resultMap.put("detail_list",detailList);
         }
        resultMap.put("visit_count",visitCount);
        resultMap.put("invite_count",inviteCount);
        renderJson(new CommonResult(0,"操作成功",resultMap));
    }



    private VVisitorRecord addVisitor(List<HashMap<String,Object>> visitorList, String workKey, String visitor_name, String visitor_card_no, String visitor_phone, String visitor_sex, String scene_photo, String visit_reason, String appoint_time, String apply_type, String visitor_plate, String visit_hours, BigInteger pid, String visitor_cmp, VDeptUser staff, BigInteger createUserId, String machineCode) {

        VVisitorRecord visitorRecord = null;
        try {
            Long staff_id = staff.getLong("id");
            byte[] photoKey = Base64.decode(scene_photo);
            String fileName = visitor_name + ".jpg";
            File file = FilesUtils.getFileFromBytes(photoKey, imageSaveDir, fileName);
            String idNo = DESUtil.encode(workKey, visitor_card_no);
            //判断访客是否存在人员信息表
            VDeptUser vDeptUser = new VDeptUser();
            VDeptUser user = deptUserService.confireNameAndIdNO(visitor_name, idNo);
            Long userId;
            if (user == null) {
                vDeptUser.setRealName(visitor_name)
                        .setPhone(visitor_phone)
                        .setCreateDate(getDateTime())
                        .setAuthDate(getDate())
                        .setIdNO(idNo)
                        .setIdHandleImgUrl(fileName)
                        .setSex(visitor_sex)
                        .setStatus("applySuc")
                        .setIsAuth("T")
                        .setCurrentStatus("normal")
                        .setAddr(visitor_cmp)
                        .setUserType("visitor");
                vDeptUser.save();
                userId = vDeptUser.getId();
                fileName = deptUserService.uploadUserImg(file.getAbsolutePath(), "" + userId);
                vDeptUser.setIdHandleImgUrl(fileName).update();
                file.delete();
            } else {
                userId = user.getLong("id");
                vDeptUser = VDeptUser.dao.findById(userId);
                if(StringUtils.isNotEmpty(visitor_phone)) {
                    vDeptUser.setPhone(visitor_phone);
                }
                if(StringUtils.isNotEmpty(visitor_sex)) {
                    vDeptUser.setSex(visitor_sex);
                }
                if ("visitor".equals(vDeptUser.getUserType()) || StringUtils.isBlank(vDeptUser.getIdHandleImgUrl()))
                {
                    fileName = deptUserService.uploadUserImg(file.getAbsolutePath(), "" + userId);
                    vDeptUser.setIdHandleImgUrl(fileName);
                }
                if(StringUtils.isNotEmpty(visitor_cmp)) {
                    vDeptUser.setAddr(visitor_cmp);
                }
                vDeptUser.update();
                file.delete();
            }
            visitorRecord =  new VVisitorRecord();
            visitorRecord.setUserId(userId)
                    .setVisitDate(getDate())
                    .setVisitTime(getTime())
                    .setVisitorId(staff_id)
                    .setReason(visit_reason)
                    .setStartDate(appoint_time)
                    .setRecordType(1)
                    .setIsReceive("F")
                    .setVitype(apply_type)
                    .setCompanyId(staff.getLong("deptId"))
                    .setPid(pid!=null?pid.longValue():null)
                    .setMachineCode(machineCode)
                    .setCreateUser(createUserId!=null?createUserId.longValue():null)
                    .setOrgCode(staff.getStr("org_id"));

            if (StringUtils.isNotBlank(visitor_plate)) {
                visitorRecord.setPlate(visitor_plate);
            }
            String endDate = endDateTime(appoint_time, Float.valueOf(visit_hours));
            visitorRecord.setEndDate(endDate);
            //判断是否访问黑名单人员
            VBlackUser blackUser = blackUserService.findBalckUser(visitor_name, idNo);
            if (blackUser != null) {
                visitorRecord.setCstatus("applyFail");
                visitorRecord.setReplyDate(getDate());
                visitorRecord.setReplyTime(getTime());
                visitorRecord.setReplyUserId(0L);
            } else {
                visitorRecord.setCstatus("applyConfirm");
                if ("T".equals(Params.getAutoApproval())) //自动审核通过
                {
                    visitorRecord.setCstatus("applySuccess");
                    visitorRecord.setReplyDate(getDate());
                    visitorRecord.setReplyTime(getTime());
                    visitorRecord.setReplyUserId(0L);
                }
            }
            visitorRecord.save();
            addVisitorMap(visitorList,vDeptUser,visitor_phone,visitor_name,visitorRecord);

        } catch (Exception ex) {
            ex.fillInStackTrace();
            return  null;
        }
        return visitorRecord;
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
        //System.out.println("data:"+data);
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
            System.out.println("签名:"+mySign+"  -------" + sign);
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


    /**
     * 添加二维码信息
     * @param QRCodeList
     * @param visitor_name
     * @param vVisitorRecord
     */
    private void addQRCode(List<String> QRCodeList,String visitor_name,VVisitorRecord vVisitorRecord )
    {
        String visitorCont = "["+visitor_name+"]"
                +"["+vVisitorRecord.getId()+"]"
                +"["+vVisitorRecord.getStartDate()+"]"
                +"["+vVisitorRecord.getEndDate()+"]";
        StringBuilder strQRCodeCon = new StringBuilder("abc&2&1&1&");
        strQRCodeCon.append(System.currentTimeMillis());
        strQRCodeCon.append("|");
        strQRCodeCon.append(Base64.encode(visitorCont.getBytes()));
        QRCodeList.add(strQRCodeCon.toString());
    }

    /**
     *添加访客信息
     * @param visitorList
     * @param phone
     * @param visitorName
     */
    private void addVisitorMap(List<HashMap<String,Object>> visitorList,VDeptUser visitor,String phone,String visitorName,VVisitorRecord vVisitorRecord)
    {
        if(StringUtils.isNotBlank(phone)) {
            HashMap<String, Object> mapVisitor = new HashMap();
            mapVisitor.put("visitor",  visitor);
            mapVisitor.put("name",visitorName);
            mapVisitor.put("phone",  phone);
            mapVisitor.put("visitorResult",vVisitorRecord.getCstatus());
            mapVisitor.put("visitorDateTime",vVisitorRecord.getStartDate()+"~"+vVisitorRecord.getEndDate());
            visitorList.add(mapVisitor);
        }
    }





}
