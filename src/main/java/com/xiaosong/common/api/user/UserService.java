package com.xiaosong.common.api.user;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.xiaosong.MainConfig;
import com.xiaosong.common.api.userkey.UserKeyService;
import com.xiaosong.common.api.code.CodeService;
import com.xiaosong.common.api.websocket.WebSocketMonitor;
import com.xiaosong.common.api.websocket.WebSocketSyncData;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.constant.Constant;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.util.*;
import com.xiaosong.util.Base64;
import org.apache.commons.lang3.StringUtils;

import java.net.URLDecoder;
import java.util.*;

/**
 * @program: innerVisitor
 * @description: 用户接口
 * @author: cwf
 * @create: 2019-12-27 16:33
 **/
public class UserService {
    public static final UserService me = new UserService();
    Log log = Log.getLog(UserService.class);

    //密码登入
    public Result login(VDeptUser deptUser, String sysPwd, Integer style) throws Exception {
        SqlPara para = Db.getSqlPara("deptUser.findByPhone", deptUser.getPhone());//根据手机查找用户
        VDeptUser user = VDeptUser.dao.findFirst(para);
        if (user == null) {
            return Result.unDataResult("fail", "用户不存在");
        }
        //jfinalredis问题暂时关闭
        //判断密码输入次数是否超出限制，超出无法登录
//        if(PasswordService.me.isErrInputOutOfLimit(user.getId(),Status.PWD_TYPE_SYS)){
//            String limitTime = ParamService.me.findValueByName("errorInputSyspwdWaitTime");
//            CodeService.me.sendMsg(user.getLoginName(), 2,null,null,null,null);
//            return  Result.unDataResult("fail","由于您多次输入错误密码，为保证您的账户与资金安全，"+limitTime+"分钟内无法登录");
//        }
        //style为空默认选择：密码登录
        if (sysPwd.equals(user.getSysPwd())) {
            //重置允许用户输入错误密码次数
//            PasswordService.me.resetPwdInputNum(user.getId(), Status.PWD_TYPE_SYS);
            return UserUtil.me.loginSave(user, deptUser);
        } else {
//            Long leftInputNum =PasswordService.me.addErrInputNum(user.getId(),Status.PWD_TYPE_SYS);
//            return  Result.unDataResult("fail","密码错误:剩余" + leftInputNum + "次输入机会");
            return Result.unDataResult("fail", "密码错误");
        }
    }

    //验证码登入
    public Result loginByVerifyCode(VDeptUser deptUser, String code) throws Exception {
        SqlPara para = Db.getSqlPara("deptUser.findByPhone", deptUser.getPhone());//根据手机查找用户
        VDeptUser user = VDeptUser.dao.findFirst(para);
        if (user == null) {
            return Result.unDataResult(ConsantCode.FAIL, "用户不存在");
        }
        //用户状态为normal
        /**
         * 2,验证短信验证码
         */
        if (CodeService.me.verifyCode(deptUser.getPhone(), code, 1, Constant.SYS_CODE)) {
            return UserUtil.me.loginSave(user, deptUser);
        } else {
            return Result.unDataResult(ConsantCode.FAIL, "验证码输入错误，请重新输入!");
        }
    }


    //验证码登入
    public Result loginByVerifyCode(String phone) throws Exception {
        VDeptUser deptUser  = new VDeptUser();
        deptUser.setPhone(phone);
        deptUser.setToken(UUID.randomUUID().toString());
        SqlPara para = Db.getSqlPara("deptUser.findByPhone", phone);//根据手机查找用户
        VDeptUser user = VDeptUser.dao.findFirst(para);
        if (user == null) {
            return Result.unDataResult(ConsantCode.FAIL, "用户不存在");
        }

        return UserUtil.me.loginSave(user, deptUser);
    }


    //验证码登入
    public Result loginByToken(VDeptUser deptUser, JSONObject userJSON) throws Exception {

        String phone = userJSON.getString("phone");
        if(StringUtils.isBlank(phone))
        {
            phone = userJSON.getString("username");
        }
        VDeptUser user = null;
        if(StringUtils.isNotBlank(phone)) {
            SqlPara para = Db.getSqlPara("deptUser.findByPhone", phone);//根据手机查找用户
            user = VDeptUser.dao.findFirst(para);
        }else
        {
            return Result.fail();
        }
        System.out.println("解析到sso数据" + userJSON.toJSONString());

        String appType = userJSON.getString("appType");
        String registrationId = userJSON.getString("registrationId");

        if (user == null) {
            String username = userJSON.getString("username");
            String idCard = userJSON.getString("idCard");
            String sex = userJSON.containsKey("sex")?(userJSON.getInteger("sex")==0?"1":"2"):"0";
            String name = userJSON.getString("name");

            Record record = Db.findFirst("select * from v_user_key");
            String idNo = DESUtil.encode(record.getStr("workKey"), idCard);
            user = new VDeptUser();
            user.setCurrentStatus("normal");
            user.setIsAuth("F");
            user.setPhone(StringUtils.isBlank(phone)?username:phone);
            user.setRealName(name);
            user.setIdNO(idNo);
            user.setSex(sex);
            user.setIsSync("T");
            user.setCreateDate(DateUtil.getSystemTime());
            user.setStatus("applySuc");
            user.setUserType("visitor");
            user.setRegistrationId(registrationId);
            user.setAppType(appType);
            user.save();
        }
        else
        {
            user.setRegistrationId(registrationId);
            user.setAppType(appType);
            user.update();
        }
        return UserUtil.me.loginSave(user, deptUser);
    }


    //是否实名
    public boolean isVerify(Object userId) {
        /**
         * 企业版去除redis
         */
//        Integer apiAuthCheckRedisDbIndex = Integer.valueOf(ParamService.me.findValueByName("apiAuthCheckRedisDbIndex"));//存储在缓存中的位置
//        String key = userId + "_isAuth";
//        //redis修改
//        String isAuth = RedisUtil.getStrVal(key, apiAuthCheckRedisDbIndex);
//        if (StringUtils.isBlank(isAuth)) {
            //缓存中不存在，从数据库查询
           String isAuth = Db.queryStr("select isAuth from " + TableList.DEPT_USER + " where id=?", userId);
            if (isAuth == null) return false;
            //redis修改
//            RedisUtil.setStr(apiAuthCheckRedisDbIndex, key, isAuth, null);
//        }
        return "T".equalsIgnoreCase(isAuth);
    }

    public Result verify(VDeptUser deptUser, String userId) {
        try {
            if (isVerify(userId)) {
                log.info("已经实名认证过");
                return Result.unDataResult("fail", "已经实名认证过");
            }
            String idNO = deptUser.getIdNO();
            String realName = URLDecoder.decode(deptUser.getRealName(), "UTF-8");
            String workKey = UserKeyService.me.findKeyByStatus("normal");
            // update by cwf  2019/10/15 10:36 Reason:暂时修改为后端加密
//            String idNoMW = DESUtil.encode(workKey,idNO);
            //原先为前端加密后端解密
            String idNoMW = DESUtil.decode(workKey, idNO);
            if (idNoMW.equals(idNO)) {
                return Result.unDataResult("fail", "传输过程中身份信息错误");
            }
//            String idNoMW = idNO;
            //储存在本地图片地址
            //String idHandleImgUrl = MainConfig.p.get("imageSaveDir")+deptUser.getIdHandleImgUrl();
            //存储在图片服务器的图片
            String idHandleImgUrl = deptUser.getIdHandleImgUrl();
            String photo = Base64.encode(FilesUtils.compressUnderSize(FilesUtils.getImageFromNetByUrl(MainConfig.p.get("imgServerUrl")+"imgserver/"+idHandleImgUrl), 40960L));
            /**
             * 验证 身份证
             */
            // update by cwf  2019/10/15 10:54 Reason:改为加密后进行数据判断 原 idNO 现idNoMw
            // update by cwf  2019/11/6 13:42 Reason:改为回前端加密 原 idNoMW 现 idNO


            VDeptUser record = VDeptUser.dao.findFirst("select * from " + TableList.DEPT_USER + " where idNo=? and isAuth ='T'  and currentStatus ='normal'", idNO);
           // Object o = Db.queryFirst("select id from " + TableList.DEPT_USER + " where idNo=? and isAuth ='T'  and currentStatus ='normal'", idNO);
            if (record != null) {
               String authPhone =  record.getStr("phone");
               if(StringUtils.isNotBlank(authPhone)) {
                   return Result.unDataResult("fail", "该身份证已实名，无法再次进行实名认证！");
               }
            }
            //实人认证  update by cwf  2019/11/25 11:30 Reason:先查询本地库是否有实名认证 如果没有 则调用CTID认证  判断实人认证是否过期，过期重新走ctid
            String sql = "select distinct * from " + TableList.LOCAL_AUTH + " where idNo='" + idNO + "' and realName='" + realName + "'";
            VDeptUser user = VDeptUser.dao.findById(userId);
//            Calendar curr = Calendar.getInstance();
//            Calendar start = Calendar.getInstance();
            //判断时间是否需要重新实名
//            Record localAuth = Db.findFirst(sql);
//            if (localAuth != null && !curr.after(start)) {//可以改为连接外部api进行本地实人认证
//                idHandleImgUrl = BaseUtil.objToStr(localAuth.get("idHandleImgUrl"), idHandleImgUrl);
//                log.info("本地实人认证成功上一张成功图片为：{}", idHandleImgUrl);
//            } else {
            /**
             * 实人认证
             */
                JSONObject photoResult = AuthUtil.auth(idNoMW, realName, photo);
                if (!"00000".equals(photoResult.getString("return_code"))) {
                    return Result.unDataResult("fail", photoResult.getString("ret_explain"));
//                }
            }
            String address = deptUser.get("addr");
            //非空判断
            idHandleImgUrl = URLDecoder.decode(idHandleImgUrl, "UTF-8");
            //暂时注释
//            String idType = URLDecoder.decode(BaseUtil.objToStr(paramMap.get("idType"), null), "UTF-8");

            user.setIdHandleImgUrl(idHandleImgUrl)
                    .setRealName(realName).setIsAuth("T").setIdNO(idNO).setAddr(address)
                    .setAuthDate(DateUtil.getCurDate());

            boolean result = Db.tx(()->{

                try {
                    //如果存在随行人员记录，删除随行人员记录，并修改访客记录的ID
                    if(record!=null) {
                        record.setCurrentStatus("deleted");
                        user.setUserType(record.getUserType());
                        user.setDeptId(record.getDeptId());
                        user.setActiveDate(record.getActiveDate());
                        user.setExpiryDate(record.getExpiryDate());
                        user.setSex(record.getSex());
                        user.setCardNO(record.getCardNO());
                        user.setAddr(record.getAddr());
                        user.setIntime(record.getIntime());
                        user.setUserNo(record.getUserNo());
                        user.setRemark(record.getRemark());
                        record.update();
                        Db.update("update v_visitor_record set userId=? where userId =?",user.getId(),record.getId());
                        Db.update("update v_visitor_record set visitorId=? where visitorId =?",user.getId(),record.getId());
                        Db.update("update v_user_post set userId=? where userId =?",user.getId(),record.getId());
                    }
                    user.update();
                }
                catch (Exception ex)
                {
                    return false;
                }

                return true;
            });

            if (result) {
//                Integer apiAuthCheckReduserloginisDbIndex = Integer.valueOf(ParamService.me.findValueByName("apiAuthCheckRedisDbIndex"));//存储在缓存中的位置
//                String key = userId + "_isAuth";
                //redis修改
//                RedisUtil.setStr(apiAuthCheckRedisDbIndex, key, "T", null);
                Map<String, Object> resultMap = new HashMap<String, Object>();
                resultMap.put("isAuth", "T");
                //本地实人记录
                int authSave = Db.update("insert into " + TableList.LOCAL_AUTH + "(userId,idNO,realName,idHandleImgUrl,authDate) " +
                        "values('" + userId + "','" + idNO + "','" + realName + "','" + idHandleImgUrl + "',SYSDATE())");
                log.info("插入本地实人：" + authSave);
                resultMap.put("isSetTransPwd", BaseUtil.objToStr(user.get("isSetTransPwd"), "F"));

                WebSocketSyncData.me.sendVisitorData();
                WebSocketSyncData.me.sendStaffData();
                WebSocketMonitor.me.getAllData();

                return ResultData.dataResult("success", "实名认证成功", resultMap);
            }
            return Result.unDataResult("fail", "实名认证失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.unDataResult("fail", "异常，请稍后再试");
        }
    }

    public Result forget(String code, String phone, String sysPwd) {
        if ("".equals(sysPwd)) {
            return Result.unDataResult("fail", "新密码不能为空");
        }
        VDeptUser user = VDeptUser.dao.findFirst(Db.getSql("deptUser.findByPhone"), phone);
        if (user == null) {
            return Result.unDataResult("fail", "手机号未注册");
        }
//        String sql = "select * from " + TableList.DEPT_USER + " where phone =?";
        boolean flag = CodeService.me.verifyCode(phone, code, 1, Constant.SYS_CODE);
        if (!flag) {
            return Result.unDataResult("fail", "验证码错误");
        }
        user.setSysPwd(sysPwd);
        boolean update = user.update();
        log.info("更新成功？{}", update);
        return update ? Result.success() : Result.fail();
    }

    public Result getUserByUserToken(String userId, String token) {
        //model使用了驼峰命名 导致有下划线的字段被转换为驼峰命名
        VDeptUser user = VDeptUser.dao.findFirst("select * from " + TableList.DEPT_USER + " where id=? and token=?", userId, token);
        return user == null ?
                Result.unDataResult("fail", "找不到用户的信息")
                : ResultData.dataResult("success", "获取成功", user);
    }

    public Result checkPhone(String phone) {
        String str = Db.queryStr("select phone from " + TableList.DEPT_USER + " where phone=?", phone);
        if (str == null) {
            return Result.unDataResult("fail", "用户存在！");
        }
        return Result.unDataResult("success", "欢迎注册");
    }


    public Result updatePassword(Object userId,Object oldPassword, Object newPassword) {
        VDeptUser user = VDeptUser.dao.findFirst("select * from " + TableList.DEPT_USER + " where id=?", userId);
        if(oldPassword.equals(user.getSysPwd())){
            user.setSysPwd(newPassword.toString());
            user.update();
            return Result.success();
        }else{
            return  new Result(500,"旧密码错误");
        }
    }

    public Result updatePhone(Object userId, String code, String phone) {
        boolean flag = CodeService.me.verifyCode(phone,code,1, Constant.SYS_CODE);
        if(!flag){
            return Result.unDataResult("fail","验证码错误");
        }
        SqlPara para = Db.getSqlPara("deptUser.findByPhone", phone);//根据手机查找用户
        VDeptUser user = VDeptUser.dao.findFirst(para);
        if(user != null){
            return Result.unDataResult("fail","手机号已被注册");
        }
        VDeptUser upUser = VDeptUser.dao.findById(userId);
        upUser.setPhone(phone);
        return upUser.update()?Result.unDataResult("success","更新手机成功"):Result.unDataResult("fail","更新手机失败");
    }

    public Result nick(Long userId, VDeptUser appUser) {
        appUser.setId(userId);
        if (appUser.update()) {
           return Result.unDataResult("success","保存成功");
        }else{
            return Result.unDataResult("fail","保存失败");
        }
    }
}
