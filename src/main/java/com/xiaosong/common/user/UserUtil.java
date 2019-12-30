package com.xiaosong.common.user;

import com.alibaba.fastjson.JSON;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.xiaosong.common.compose.Result;
import com.xiaosong.common.compose.ResultData;
import com.xiaosong.common.notice.NoticeUserService;
import com.xiaosong.common.password.PasswordService;
import com.xiaosong.constant.Constant;
import com.xiaosong.constant.Status;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VAppUser;
import com.xiaosong.model.VCompany;
import com.xiaosong.param.ParamService;
import com.xiaosong.util.BaseUtil;
import com.xiaosong.util.ConsantCode;
import com.xiaosong.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: jfinal_demo_for_maven
 * @description: user通用方法放置
 * @author: cwf
 * @create: 2019-12-29 22:28
 **/
public class UserUtil {
    public static final UserUtil me = new UserUtil();
    Logger logger = Logger.getLogger(UserUtil.class);
    public void updateRedisTokenAndAuth(Long userId, String token, String isAuth) throws Exception {
        if( StringUtils.isBlank(token) || StringUtils.isBlank(isAuth)){
            return;
        }
        Integer apiAuthCheckRedisDbIndex = Integer.valueOf(ParamService.me.findValueByName("apiAuthCheckRedisDbIndex"));//存储在缓存中的位置
        Integer expire = Integer.valueOf(ParamService.me.findValueByName("apiAuthCheckRedisExpire"));//过期时间(分钟)
        Cache cache = Redis.use("db" + apiAuthCheckRedisDbIndex);
        //token
        RedisUtil.setStr(cache,userId+"_token", token, expire*60);
        //是否实名
        RedisUtil.setStr(cache,userId+"_isAuth", isAuth,  expire*60);
    }

    public Result updateDeviceToken(VAppUser user){
//
        return null;
    }

    public boolean updateDeviceToken(VAppUser user, VAppUser appUser) {
        String deviceToken = appUser.getDeviceToken();
        if (deviceToken !=null&&!"".equals(deviceToken)){
            user.setDeviceToken(deviceToken);
            if (appUser.getDeviceType()!=""){
                user.setDeviceType(appUser.getDeviceType());
            }
        }
        user.setIsOnlineApp("T");
        boolean update = user.update();
        if (update){
            logger.info("存储app登入信息成功: "+ deviceToken+","+appUser.getDeviceType());
            return true;
        }else {
            logger.info("存储app登入信息失败");
            return false;
        }
    }
    public String findKeyByStatus(String status) throws  Exception{
        String key = null;
        //redis修改，原dbNum=8 现在dbNum=32
        Cache cache = Redis.use();
        key = cache.get("key_workKey");
        if(key == null){
            key =  findKeyFromDB(status);
            if(key != null){
                //redis修改，原dbNum=8 现在dbNum=32
                RedisUtil.setStr(cache,"key_workKey",key, 32);
            }
        }
        return key;
    }
    /**
     * 从数据库中获取密钥
     * @param status
     * @return String
     */
    private String findKeyFromDB(String status){
        if(!StringUtils.isEmpty(status)){
            String sql = "select workKey from"+ TableList.KEY +" where cstatus = ?";
            Record record = Db.findFirst(sql, status);
            if(record != null){
                return BaseUtil.objToStr(record.get("workKey"),null);
            }
        }
        return null;
    }
    //登入成功后保存数据库与缓存数据
     Result loginSave(VAppUser user,VAppUser appUser) throws Exception {
         PasswordService.me.resetPwdInputNum(user.getId(), Status.PWD_TYPE_SYS);
         user.setToken(UUID.randomUUID().toString());
//                //实名有效日期过了
         if ("T".equals(user.getIsAuth())) {
             if (user.getValidityDate() != null && !"".equals(user.getValidityDate())
                     && !StringUtils.isBlank(user.getValidityDate())) {
                 String validityDate = user.getValidityDate();
                 Calendar curr = Calendar.getInstance();
                 Calendar start = Calendar.getInstance();
                 start.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(validityDate));
                 if (curr.after(start)) {
                     //设置实人状态为false
                     user.setIsAuth("F").setIdNO("");
                 }
             }
         }
         logger.info("登入人为:" + user.getId() + "，token" + appUser.getToken());
//                user._setAttrs(appUser);//改变了modifyFlag
//                //更新缓存中的Token,实名
         UserUtil.me.updateRedisTokenAndAuth(user.getId(), user.getToken(), user.getIsAuth());
         String workKey =  UserUtil.me.findKeyByStatus(Constant.KEY_STATUS_NORMAL); //获取密钥
         if(workKey != null){
             user.put("workKey",workKey);
         }
         UserUtil.me.updateDeviceToken(user,appUser);  //更新用户数据
         /**
          * 获取用户的公告
          */
         Record noticeUser = NoticeUserService.me.findByUserId(user.getId());
         List<Record> notices = null;
//                Map<String,Object> result = new HashMap<String, Object>();
         Integer authCheckRedisDbIndex = Integer.valueOf(ParamService.me.findValueByName("apiAuthCheckRedisDbIndex"));//存储在缓存中的位置
         Integer expire = Integer.valueOf(ParamService.me.findValueByName("apiAuthCheckRedisExpire"));//过期时间(分钟)
         String redisValue = null;
         if(noticeUser == null){
//                    //获取所有"normal"的公告
             notices  = Db.find(" select * from "+TableList.NOTICE +" where cstatus = 'normal' order by createDate desc ");
             if(notices != null && !notices.isEmpty() ){
//                        //获取最新的公告id
                 Integer maxNoticeId = Db.queryInt("select max(id) from " + TableList.NOTICE);
                 noticeUser.set("userId",user.getId());
                 noticeUser.set("maxNoticeId",maxNoticeId);
                 Db.save(TableList.USER_NOTICE,noticeUser);
                 redisValue = JSON.toJSONString(noticeUser);
                 Cache cache = Redis.use("db" + authCheckRedisDbIndex);
//                        //redis修改
                 RedisUtil.setStr(cache,user.getId()+"_noticeUser",redisValue ,expire*60);
             }
         }else{
             //查询是否有最新的公告
             notices = Db.find(" select * from "+TableList.NOTICE +" where cstatus = 'normal' and id > ? order by createDate desc ", noticeUser.getInt("maxNoticeId"));
             if(notices != null && !notices.isEmpty()) {
                 Integer maxNoticeId = Db.queryInt("select max(id) from " + TableList.NOTICE);
                 noticeUser.set("maxNoticeId", maxNoticeId);
                 Db.save(TableList.USER_NOTICE,noticeUser);
                 redisValue = JSON.toJSONString(noticeUser);
                 Cache cache = Redis.use("db" + authCheckRedisDbIndex);
//                        //redis修改
                 RedisUtil.setStr(cache,user.getId()+"_noticeUser",redisValue ,expire*60);
             }
         }
                Map<String,Object> result = new HashMap<String, Object>();
                result.put("notices",notices);
                result.put("user",user);
         String  applyType="";
         String  companyName="";
         if (user.getCompanyId()!=null){
             VCompany company = VCompany.dao.findById(user.getCompanyId());
             if (company!=null){
                 applyType = BaseUtil.objToStr(company.getApplyType(),"");
                 companyName = BaseUtil.objToStr(company.getCompanyName(),"");
             }
         }
         user.put("companyName",companyName);
         user.put("applyType",applyType);
//                //增加获取orgCode 需要改造企业版
//                String orgCode = BaseUtil.objToStr(orgService.findOrgCodeByUserId(userId),"无");
//                user.put("orgCode", orgCode);
         return ResultData.dataResult(ConsantCode.SUCCESS,"登录成功",result);

     }
}
