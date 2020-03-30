package com.xiaosong.common.api.appversion;

import com.alibaba.fastjson.JSON;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xiaosong.cache.MyCache;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.constant.TableList;
import com.xiaosong.param.ParamService;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: cwf
 * @create: 2020-01-08 11:00
 **/
public class appVersionService {
    Log log =Log.getLog(appVersionService.class);
    public static final appVersionService me = new appVersionService();
    //获取版本
    private Map<String,Object> getVersion(String appType, String channel){
        // update by cwf  2019/11/19 17:44 Reason:版本信息改回为旧redis apiAuthCheckRedisDbIndex
//        Integer apiAuthCheckRedisDbIndex = Integer.valueOf(ParamService.me.findValueByName("apiAuthCheckRedisDbIndex"));//存储在缓存中的位置35
        Map<String,Object> appVersion = null;
        String key = "appVersion_android_"+appType+"_"+channel;
        //redis修改
        String json = CacheKit.get("PARAM",key);
//        System.out.println(json);
//        if(StringUtils.isNotBlank(json)){
//            log.info("---从缓存获取版本号----："+json);
//            appVersion = JSON.parseObject(json, Map.class);
//        }else{

            String sql = " select * from "+ TableList.APP_VERSION+" where appType = '" + appType + "' and channel='" + channel+"'";
            appVersion = Db.findFirst(sql).getColumns();
//            log.info("---从数据库获取版本号----："+appVersion);
//            //redis修改
//            MyCache.cache.put("PARAM",key, JSON.toJSONString(appVersion));
//        }
        return appVersion;
    }
    /**
     * 获取IOS最新版本信息
     * @Author Bzk
     * @Date 2017/4/11 14:12
     */
    private Map<String,Object> getIOSVersion(String appType,String channel){
        // update by cwf  2019/11/19 17:44 Reason:版本信息改回为旧redis apiAuthCheckRedisDbIndex
        Integer apiAuthCheckRedisDbIndex = Integer.valueOf(ParamService.me.findValueByName("apiAuthCheckRedisDbIndex"));//存储在缓存中的位置
        Map<String,Object> appVersion = null;
//        String key = "appVersion_ios_" + appType+"_"+channel;
        //redis修改
//        String json = RedisUtil.getStrVal(key, apiAuthCheckRedisDbIndex);
//        if(StringUtils.isNotBlank(json)){
//            appVersion = JSON.parseObject(json, Map.class);
//        }else{
            String sql = " select * from "+ TableList.APP_VERSION+" where appType = '" + appType + "' and channel='"+channel+"'" ;
            appVersion = Db.findFirst(sql).getColumns();
            //redis修改
//            RedisUtil.setStr(apiAuthCheckRedisDbIndex,key, JSON.toJSONString(appVersion), null);
//        }
        return appVersion;
    }
    public Result updateAndroid(String appType, String channel, Integer versionNum) {
        Map<String,Object> appVersion = getVersion(appType,channel);
//
        Result result = null;
        if (appVersion == null){
            log.info("-----数据库查不到版本号------");
            result = ResultData.unDataResult("fail","已经是最新版本了！");
        }else{
            //获取最新版本号
            String dbVersionNum = (String)appVersion.get("versionNum");
            log.info("-----数据库版本号------："+dbVersionNum+"-----传入版本号------："+versionNum);
            if (Long.valueOf(dbVersionNum) > versionNum){
                //用户的软件不是最新版
                Map<String,Object> appVersionInfo = new HashMap<>();
                //存放最新版本信息
                appVersionInfo.put("versionName",appVersion.get("versionName"));//版本名
                appVersionInfo.put("versionNum",appVersion.get("versionNum"));//最新版本
                appVersionInfo.put("isImmediatelyUpdate",appVersion.get("isImmediatelyUpdate"));//强制更新
                appVersionInfo.put("updateUrl",appVersion.get("uploadFile"));//更新地址
                appVersionInfo.put("memo",appVersion.get("memo"));//版本说明
                log.info("-----用户的软件不是最新版本------");
                return ResultData.dataResult("success", "不是最新版本", appVersionInfo);
            }else{
                //用户的软件已经是最新版
                log.info("-----用户的软件已经是最新版------");
                result = ResultData.unDataResult("fail","已经是最新版本了！");
            }
        }
        return result;

    }
//
    public Result updateIos(String appType,String channel ) {

        //获取最新版本信息
        String isoChannel = "AppStore";
        if (channel == null){
            isoChannel = "AppStore";
        }else{
            isoChannel = channel;
        }
        log.info("isoChannel:"+isoChannel);
        Map<String,Object> appVersion = getIOSVersion(appType,isoChannel);
        if(appVersion != null){
            Map<String,Object> appVersionInfo = new HashMap<String, Object>();
            //存放最新版本信息
            appVersionInfo.put("versionNum",appVersion.get("versionNum"));//版本号
            appVersionInfo.put("isImmediatelyUpdate",appVersion.get("isImmediatelyUpdate"));//立即更新？
            appVersionInfo.put("updateUrl",appVersion.get("uploadFile"));//更新地址
            appVersionInfo.put("memo",appVersion.get("memo"));//版本说明
            return ResultData.dataResult("success", "不是最新版本", appVersionInfo);
        }
        return ResultData.unDataResult("fail","已经是最新版本了");
    }

}
