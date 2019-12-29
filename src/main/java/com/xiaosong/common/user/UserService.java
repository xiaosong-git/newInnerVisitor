package com.xiaosong.common.user;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;
import com.xiaosong.common.code.CodeService;
import com.xiaosong.common.compose.Result;
import com.xiaosong.common.compose.ResultData;
import com.xiaosong.common.password.PasswordService;
import com.xiaosong.constant.Status;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VAppUser;
import com.xiaosong.model.VAppUserAccount;
import com.xiaosong.util.BaseUtil;
import com.xiaosong.util.ConsantCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

/**
 * @program: innerVisitor
 * @description: 用户接口
 * @author: cwf
 * @create: 2019-12-27 16:33
 **/
public class UserService {
    public static final UserService me = new UserService();
    Logger logger = Logger.getLogger(UserService.class);
    //验证码登入
    public Result loginByVerifyCode(Map<String, Object> paramMap) {
        String phone = BaseUtil.objToStr(paramMap.get("phone"), null);//登录账号
        String code = BaseUtil.objToStr(paramMap.get("code"), null);//短信验证码
        return null;
    }
    //密码登入
    public Result login(Map<String, Object> paramMap) {
        return null;
    }

    //验证码登入
    public Result loginByVerifyCode(VAppUser appUser, String code) throws Exception {
        SqlPara para = Db.getSqlPara("vAppUser.findByPhone", appUser.getPhone());//根据手机查找用户
        VAppUser user = appUser.dao.findFirst(para);
        if (user==null){
            return Result.unDataResult(ConsantCode.FAIL,"用户不存在");
        }
        VAppUserAccount userAccount = VAppUserAccount.dao.findFirst("select * from " + TableList.USER_ACCOUNT + " where userId=?", user.getId());
        if(userAccount == null){
            return  Result.unDataResult(ConsantCode.FAIL,"未查询到相关账户信息");
        }
        //用户状态为normal
        if("normal".equals(userAccount.getCstatus())) {
            /**
             * 2,验证短信验证码
             */
            if (CodeService.me.verifyCode(appUser.getPhone(), code, 1)) {
//                //短信验证码正确
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
//                Db.update(TableList.APP_USER, user);
//                //更新缓存中的Token,实名
//                String token = BaseUtil.objToStr(user.get("token"), null);
                logger.info("登入人为:" + user.getId() + "，token" + user.getToken());
//                String isAuth = BaseUtil.objToStr(user.get("isAuth"), null);
                UserUtil.me.updateRedisTokenAndAuth(user.getId(), user.getToken(), user.getIsAuth());
//                /** update by cwf  2019/9/24 10:08 Reason:添加储存设备号用来推送消息
//                 */
                //存储数据
                UserUtil.me.updateDeviceToken(user,appUser);
//                //获取密钥
//                String workKey = keyService.findKeyByStatus(TableList.KEY_STATUS_NORMAL);
//                if(workKey != null){
//                    user.put("workKey",workKey);
//                }
//                /**
//                 * 获取用户的公告
//                 */
//                Map<String,Object> noticeUser = noticeUserService.findByUserId(userId);
//                List<Map<String,Object>> notices = null;
//                Map<String,Object> result = new HashMap<String, Object>();
//                Integer apiNewAuthCheckRedisDbIndex = Integer.valueOf(paramService.findValueByName("apiNewAuthCheckRedisDbIndex"));//存储在缓存中的位置
//                Integer expire = Integer.valueOf(paramService.findValueByName("apiAuthCheckRedisExpire"));//过期时间(分钟)
//                String redisValue = null;
//                if(noticeUser == null || noticeUser.isEmpty()){
//                    //获取所有"normal"的公告
//                    notices  = noticeService.findList(" select * ", "from "+TableList.NOTICE +" where cstatus = 'normal' order by createDate desc ");
//                    if(notices != null && !notices.isEmpty()){
//                        //获取最新的公告id
//                        Integer maxNoticeId = (Integer) baseDao.queryForObject("select max(id) from "+TableList.NOTICE,Integer.class);
//                        Map<String,Object> userNotice = new HashMap<String, Object>();
//                        userNotice.put("userId",userId);
//                        userNotice.put("maxNoticeId",maxNoticeId);
//                        noticeUserService.save(TableList.USER_NOTICE,userNotice);
//                        userNotice = noticeUserService.findByUserId(userId);
//                        redisValue = JSON.toJSONString(userNotice);
//                        //redis修改
//                        RedisUtil.setStr(userId+"_noticeUser",redisValue , apiNewAuthCheckRedisDbIndex, expire*60);
//                    }
//                }else{
//                    //查询是否有最新的公告
//                    notices = noticeService.findList(" select * ",
//                            "from "+TableList.NOTICE +" where cstatus = 'normal' and id > "+noticeUser.get("maxNoticeId")+" order by createDate desc ");
//                    if(notices != null && !notices.isEmpty()) {
//                        Integer maxNoticeId = (Integer) baseDao.queryForObject("select max(id) from " + TableList.NOTICE, Integer.class);
//                        Map<String, Object> userNotice = new HashMap<String, Object>();
//                        userNotice.put("maxNoticeId", maxNoticeId);
//                        userNotice.put("id", BaseUtil.objToInteger(noticeUser.get("id"), 0));
//                        noticeUserService.update(TableList.USER_NOTICE, userNotice);
//                        redisValue = JSON.toJSONString(userNotice);
//                        //redis修改
//                        RedisUtil.setStr(userId+"_noticeUser",redisValue , apiNewAuthCheckRedisDbIndex, expire*60);
//                    }
//                }
//                result.put("notices",notices);
//                result.put("user",user);
//                String  applyType="";
//                String  companyName="";
//                if (user.get("companyId")!=null){
//                    Map<String,Object> company =this.findById(TableList.COMPANY,Integer.parseInt(user.get("companyId").toString()));
//                    if (company!=null){
//                        applyType = BaseUtil.objToStr(company.get("applyType"),"");
//                        companyName = BaseUtil.objToStr(company.get("companyName"),"");
//                    }
//                }
//                user.put("companyName",companyName);
//                user.put("applyType",applyType);
//                //增加获取orgCode
//                String orgCode = BaseUtil.objToStr(orgService.findOrgCodeByUserId(userId),"无");
//                user.put("orgCode", orgCode);
//                return ResultData.dataResult(ConsantCode.SUCCESS,"登录成功",result);
//            }else{
//                //验证码输入错误
//                return  Result.unDataResult(ConsantCode.FAIL,"验证码输入错误，请重新获取!");
//            }
//        }else{
//            //返回账户冻结原因
//            String handleCause = userAccount.get("handleCause").toString();
//            return  Result.unDataResult(ConsantCode.FAIL, handleCause);
            }
        }
        return ResultData.dataResult(ConsantCode.SUCCESS,"成功",user);
    }


}
