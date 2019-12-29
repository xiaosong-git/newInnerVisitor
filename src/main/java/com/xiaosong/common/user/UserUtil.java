package com.xiaosong.common.user;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.xiaosong.common.compose.Result;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VAppUser;
import com.xiaosong.param.ParamService;
import com.xiaosong.util.BaseUtil;
import com.xiaosong.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

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

    public void updateDeviceToken(VAppUser user, VAppUser appUser) {
        String deviceToken = appUser.getDeviceToken();
        if (deviceToken !=null&&!"".equals(deviceToken)){
            user.setDeviceToken(deviceToken);
            if (appUser.getDeviceType()!=""){
                user.setDeviceType(appUser.getDeviceType());
            }
        }
        user.setIsOnlineApp("T");
        int update = Db.update(TableList.APP_USER, user);
        if (update > 0) {
            logger.info("存储app登入信息成功: "+ deviceToken+","+appUser.getDeviceType());
        }else {
            logger.info("存储app登入信息失败");
        }
    }
}
