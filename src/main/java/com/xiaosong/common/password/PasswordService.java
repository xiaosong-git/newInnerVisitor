package com.xiaosong.common.password;


import com.jfinal.plugin.redis.Redis;
import com.xiaosong.constant.Status;
import com.xiaosong.param.ParamService;
import com.xiaosong.util.RedisUtil;

public class PasswordService  {

    public static final PasswordService me = new PasswordService();


    /**
     * 限制用户在一定时间内密码输入错误的次数
     * @param userId 用户Id
     * @param pwdType 密码类型
     * @return
     */
    public boolean isErrInputOutOfLimit(String userId, String pwdType) {
        Integer limit = null;
        if(Status.PWD_TYPE_SYS.equals(pwdType)){
            limit = Integer.valueOf(ParamService.me.findValueByName("maxErrorInputSyspwdLimit"));
        }else{
            limit = Integer.valueOf(ParamService.me.findValueByName("maxErrorInputPaypwdLimit"));
        }
        String num = Redis.use().getJedis().get("ErrInputOutOfLimit_" + pwdType + "_" + userId);
        if(num == null){
            return false;
        }
        if(Long.valueOf(num) >= limit){
            return true;
        }
        return false;
    }
    public Long addErrInputNum(String userId, String pwdType) {
        /**
         * 获取限制参数
         */
        Integer time = null;
        Integer limit = null;
        if(Status.PWD_TYPE_SYS.equals(pwdType)){
            time = Integer.valueOf(ParamService.me.findValueByName("errorInputSyspwdWaitTime"));
            limit = Integer.valueOf(ParamService.me.findValueByName("maxErrorInputSyspwdLimit"));
        }else{
            time = Integer.valueOf(ParamService.me.findValueByName("errorInputPaypwdWaitTime"));
            limit = Integer.valueOf(ParamService.me.findValueByName("maxErrorInputPaypwdLimit"));
        }
        //redis修改，原dbNum=9 现在dbNum=33
        Long num  = RedisUtil.incr(Redis.use(),"ErrInputOutOfLimit_" + pwdType + "_"+userId, time*60);
        return limit - num;
    }
    /**
     * 重置允许用户输入错误密码的次数
     * @param userId 用户Id
     * @param pwdType 密码类型
     */
    public void resetPwdInputNum(Long userId, String pwdType) {
        /**
         * 获取限制参数
         */
        Integer time = null;
        if(Status.PWD_TYPE_SYS.equals(pwdType)){
            time = Integer.valueOf(ParamService.me.findValueByName("errorInputSyspwdWaitTime"));
        }else{
            time = Integer.valueOf(ParamService.me.findValueByName("errorInputPaypwdWaitTime"));
        }
        RedisUtil.setStr( Redis.use(),"ErrInputOutOfLimit_" + pwdType + "_"+userId, "0",  time*60);
    }


}
