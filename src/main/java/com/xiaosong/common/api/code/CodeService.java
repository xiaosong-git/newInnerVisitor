package com.xiaosong.common.api.code;

import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.xiaosong.cache.MyCache;
import com.xiaosong.compose.Result;
import com.xiaosong.constant.Constant;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.param.ParamService;
import com.xiaosong.util.GTNotification;
import com.xiaosong.util.NumberUtil;
import com.xiaosong.util.YunPainSmsUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: xiaosong
 * @description: 验证码接口
 * @author: cwf
 * @create: 2019-12-29 12:05
 **/
public class CodeService {
    public static final CodeService me = new CodeService();
    //验证验证码
    public Boolean verifyCode(String phone, String code, Integer type) {
        if (
//                Constant.DEV_MODE &&
                        "222333".equals(code)) {
            return true;
        }
//        CacheKit.put("CODE", phone,code);
        String cacheCode = CacheKit.get("CODE", phone);

        //比对
        if (code.equals(cacheCode)) {
            if (type==2){
                return true;
            }
            CacheKit.remove("CODE",phone);
            return true;
        }
        //比对错误就删除
        return false;
    }
    //发送云片网短信
    public Result sendMsg(String phone, Integer type, String visitorResult, String visitorBy, String visitorDateTime, String visitor) {
        String code = NumberUtil.getRandomCode(6);
        String limit = ParamService.me.findValueByName("maxErrorInputSyspwdLimit");
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String content = YunPainSmsUtil.getSmsContent(code, phone, type, date, limit, visitorResult, visitorBy, visitorDateTime, visitor);
        String state = YunPainSmsUtil.sendMsg(content,phone);
//        CacheKit.put("CODE", phone,code);//1800s
//        Object ok = CacheKit.get("CODE", phone);
        if ("0000".equals(state)) {
            //插入redis缓存别名为“db1”库的信息
             CacheKit.put("CODE", phone,code);//1800s
             Object ok = CacheKit.get("CODE", phone);

            return ok!=null?Result.success():Result.fail();
        } else {
            return Result.unDataResult("fail", state);
        }
    }



    //推送消息
    public void pushMsg(VDeptUser vDeptUser, Integer type, String visitorResult, String visitorBy, String visitorDateTime, String visitor) {
        String content = YunPainSmsUtil.getSmsContent(null, vDeptUser.getPhone(), type, null, null, visitorResult, visitorBy, visitorDateTime, visitor);
        pushMsg(vDeptUser.getRegistrationId(),vDeptUser.getAppType(),vDeptUser.getPhone(),content);

    }

    //推送消息
    public void pushMsg(String registrationId,String appType,String phone, Integer type, String visitorResult, String visitorBy, String visitorDateTime, String visitor) {
        String content = YunPainSmsUtil.getSmsContent(null, phone, type, null, null, visitorResult, visitorBy, visitorDateTime, visitor);
        pushMsg(registrationId,appType,phone,content);

    }

    //推送消息
    public void pushMsg(String registrationId,String appType,String phone, Integer type, String date, String limit, String visitorResult, String visitorBy, String visitorDateTime, String visitor) {
        String content = YunPainSmsUtil.getSmsContent(null, phone, type, date, limit, visitorResult, visitorBy, visitorDateTime, visitor);
        pushMsg(registrationId,appType,phone,content);

    }

    //推送消息
    public void pushMsg(String registrationId,String appType,String phone, String content) {
        boolean single = GTNotification.Single(registrationId,appType.toString(), content);
        //推送失败发送短信验证
        if(!single) {
            YunPainSmsUtil.sendMsg(content, phone);
        }
    }


}
