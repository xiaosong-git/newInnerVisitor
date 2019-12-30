package com.xiaosong.common.notice;

import com.alibaba.fastjson.JSON;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.xiaosong.constant.TableList;
import com.xiaosong.param.ParamService;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @Author linyb
 * @Date 2019/12/30 14:47
 */
public class NoticeUserService  {
    public static final NoticeUserService me = new NoticeUserService();
    public Record findByUserId(Long userId) {
        Record record;
        Integer apiAuthCheckRedisDbIndex = Integer.valueOf(ParamService.me.findValueByName("apiAuthCheckRedisDbIndex"));//存储在缓存中的位置
        //redis修改
        Cache cache = Redis.use("db" + apiAuthCheckRedisDbIndex);
        String noticeUser = cache.get(userId+"_noticeUser");
        if(StringUtils.isBlank(noticeUser)){
            //缓存中不存在,从数据库查询
            record= Db.findFirst("select * from " + TableList.USER_NOTICE + " where userId = ? ", userId);
        }else{
            record = new Record().setColumns(JSON.parseObject(noticeUser, Map.class));
        }
        return record;
    }
}
