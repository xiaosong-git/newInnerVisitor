package com.xiaosong.common.api.notice;

import com.alibaba.fastjson.JSON;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VAdBanner;
import com.xiaosong.param.ParamService;
import com.xiaosong.util.BaseUtil;
import com.xiaosong.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/18.
 */
public class AdBannerService {


    /**
     * 获取广告
     * @return
     * @throws Exception
     */
    public Result list() throws Exception {
        List<Object> banners = null;
        String key = "api_adBanner";
        Integer apiAuthCheckRedisDbIndex = Integer.valueOf(ParamService.me.findValueByName("apiAuthCheckRedisDbIndex"));//存储在缓存中的位置
        //redis修改
        String json = RedisUtil.getStrVal(key, apiAuthCheckRedisDbIndex);
        if(!StringUtils.isBlank(json)){
            //先从缓存中获取
            banners = JSON.parseObject(json, List.class);
        }else{
            //从数据库中取
            List<VAdBanner> vAdBanners = VAdBanner.dao.find(" select * from " + TableList.AD_BANNER + " where status = 1 order by orders desc ");
            json = JSON.toJSONString(vAdBanners);
            //redis修改
            RedisUtil.setStr(apiAuthCheckRedisDbIndex,key, json,  null);
        }
        //api中没有这些字段 不知道为什么要进行循环 先注释掉
//        for(int i=0; i<banners.size(); i++) {
//            Object o = banners.get(i);
//            System.out.println(o);
//            String androidParams = BaseUtil.objToStr(banners.get(i).get("androidParams"), null);
//            String iosParams = BaseUtil.objToStr(banners.get(i).get("iosParams"), null);
//            if (!StringUtils.isBlank(androidParams)) {
//                System.out.println("androidParams:"+androidParams+"--");
//                Map<String, Object> androidParamsMap = JSON.parseObject(androidParams, Map.class);
//                banners.get(i).set("androidParams", androidParamsMap);
//            }else{
//                banners.get(i).remove("androidParams");
//            }
//            if (!StringUtils.isBlank(iosParams)) {
//                System.out.println("iosParams:"+iosParams+"--");
//                Map<String, Object> iosParamsMap = JSON.parseObject(iosParams, Map.class);
//                banners.get(i).set("iosParams", iosParamsMap);
//            }else{
//                banners.get(i).remove("iosParams");
//            }
//        }
        return ResultData.dataResult("success","获取成功",banners);
    }
//    @Override
//    public List bannerList(Map<String,Object> paramMap) throws Exception{
//        String coloumSql="select * ";
//        String andSql="";
//        Integer companyId=BaseUtil.objToInteger(paramMap.get("companyId"),null);
//        if (companyId!=null){
//            andSql="companyId ="+companyId;
//        }
//        String fromSql="from "+ TableList.AD_BANNER +" where status = 1 "+andSql+" order by orders desc ";
//        return findList(coloumSql,fromSql);
//    }
}
