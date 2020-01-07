package com.xiaosong.common.code;

import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.xiaosong.common.compose.Result;
import com.xiaosong.constant.Constant;
import com.xiaosong.param.ParamService;
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
        if (Constant.DEV_MODE && "222333".equals(code)) {
            return true;
        }
        //1
        Cache dbCode = Redis.use("db1");
        //默认存的是byte
        //从Redis中取出正确验证码
        String redisCode = dbCode.get(phone);
        //比对
        if (code.equals(redisCode)) {
            Long del = Redis.use("db1").del(phone);
            return true;
        }
        //比对错误就删除
//        Long del = Redis.use("code").del(phone);
        return false;
    }
    //发送云片网短信
    public Result sendMsg(String phone, Integer type, String visitorResult, String visitorBy, String visitorDateTime, String visitor) {
        String code = NumberUtil.getRandomCode(6);
        String limit = ParamService.me.findValueByName("maxErrorInputSyspwdLimit");
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String state = YunPainSmsUtil.sendSmsCode(code, phone, type, date, limit, visitorResult, visitorBy, visitorDateTime, visitor);

        if ("0000".equals(state)) {
            //插入redis缓存别名为“db1”库的信息
            String setex = Redis.use("db1").setex(phone, 60 * 30, code);//1800s
            return "OK".equals(setex)?Result.success():Result.fail();
        } else {
            return Result.unDataResult("fail", state);
        }
    }
}
