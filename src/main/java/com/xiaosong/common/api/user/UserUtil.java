package com.xiaosong.common.api.user;

import com.jfinal.plugin.activerecord.CPI;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.xiaosong.common.api.notice.NoticeUserService;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.constant.Constant;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VDept;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.param.ParamService;
import com.xiaosong.util.BaseUtil;
import com.xiaosong.util.ConsantCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @program: xiaosong
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
        //token
//        RedisUtil.setStr(apiAuthCheckRedisDbIndex,userId+"_token", token, expire*60);
        //是否实名
//        RedisUtil.setStr(apiAuthCheckRedisDbIndex,userId+"_isAuth", isAuth,  expire*60);
    }

    public Result updateDeviceToken(VDeptUser user){
//
        return null;
    }

    public boolean updateDeviceToken(VDeptUser user, VDeptUser appUser) {
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
        //默认库可以不写
//        Cache cache = Redis.use();
//        key = cache.get("key_workKey");
//        if(key == null){
            key =  findKeyFromDB(status);
//            if(key != null){
//                RedisUtil.setStr(0,"key_workKey",key, 32);
//            }
//        }
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
     Result loginSave(VDeptUser user, VDeptUser appUser) throws Exception {
//         PasswordService.me.resetPwdInputNum(user.getId(), Status.PWD_TYPE_SYS);
         user.setToken(UUID.randomUUID().toString());

         logger.info("登入人为:" + user.getId() + "，token" + appUser.getToken());
//                user._setAttrs(appUser);//改变了modifyFlag
//                //更新缓存中的Token,实名
//         UserUtil.me.updateRedisTokenAndAuth(user.getId(), user.getToken(), user.getIsAuth());
         String workKey =  UserUtil.me.findKeyByStatus(Constant.KEY_STATUS_NORMAL); //获取密钥
         if(workKey != null){
             user.put("workKey",workKey);
         }
         UserUtil.me.updateDeviceToken(user,appUser);  //更新用户数据
         /**
          * 获取用户的公告
          */
         Record noticeUser=    NoticeUserService.me.findByUserId(user.getId());
         List<Record> notices = null;
//         Integer authCheckRedisDbIndex = Integer.valueOf(ParamService.me.findValueByName("apiAuthCheckRedisDbIndex"));//存储在缓存中的位置
//         Integer expire = Integer.valueOf(ParamService.me.findValueByName("apiAuthCheckRedisExpire"));//过期时间(分钟)
//         String redisValue = null;
         if(noticeUser == null){
//                    //获取所有"normal"的公告
             notices  = Db.find(" select * from "+TableList.NOTICE +" where cstatus = 'normal' order by createDate desc ");
             if(notices != null && !notices.isEmpty() ){
//                        //获取最新的公告id
                 Integer maxNoticeId = Db.queryInt("select max(id) from " + TableList.NOTICE);
                 noticeUser=new Record();
                 noticeUser.set("userId",user.getId());
                 noticeUser.set("maxNoticeId",maxNoticeId);
                 Db.save(TableList.USER_NOTICE,noticeUser);
//                 redisValue = JSON.toJSONString(noticeUser);
//                        //redis修改
//                 RedisUtil.setStr(authCheckRedisDbIndex,user.getId()+"_noticeUser",redisValue ,expire*60);
             }
         }else{
             //查询是否有最新的公告
             notices = Db.find(" select * from "+TableList.NOTICE +" where cstatus = 'normal' and id > ? order by createDate desc ", noticeUser.getInt("maxNoticeId"));
             if(notices != null && !notices.isEmpty()) {
                 Integer maxNoticeId = Db.queryInt("select max(id) from " + TableList.NOTICE);
                 noticeUser.set("maxNoticeId", maxNoticeId);
                 Db.save(TableList.USER_NOTICE,noticeUser);
//                 redisValue = JSON.toJSONString(noticeUser);
//                 RedisUtil.setStr(authCheckRedisDbIndex,user.getId()+"_noticeUser",redisValue ,expire*60);
             }
         }
                Map<String,Object> result = new HashMap<String, Object>();
                result.put("notices",notices);
         Map<String, Object> users = CPI.getAttrs(user);
         users.put("companyId",user.getDeptId());
         users.put("applyType",user.getRoleType());
         users.put("role",user.getRoleType());
         result.put("user",users);

         if (user.getDeptId()!=null){
             VDept dept = VDept.dao.findById(user.getDeptId());
             if (dept!=null){
//
                 users.put("companyName",dept.getDeptName());
             }
         }
                String orgCode =BaseUtil.objToStr(findOrgCodeByUserId(user.getId()),"无");
         users.put("orgCode", orgCode);
         return ResultData.dataResult(ConsantCode.SUCCESS,"登录成功",result);
     }
    public String findOrgCodeByUserId(Long userId) throws Exception{
        SqlPara para = Db.getSqlPara("org.findOrgCodeByUserId", userId);//查询大楼
            Record org = Db.findFirst(para);
        if (org==null){
            return "无";
        }
        logger.info("登入人大楼不为空！");
        String str = BaseUtil.objToStr(org.get("org_code"), "无");
        return str;
    }
}
