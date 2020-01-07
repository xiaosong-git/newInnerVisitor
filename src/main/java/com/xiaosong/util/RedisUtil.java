package com.xiaosong.util;

import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

/**
 * @program: xiaosong
 * @description: redis
 * @author: cwf
 * @create: 2019-12-29 21:44
 **/
public class RedisUtil {
    /**
     * 通过key 对value进行加值+1操作,当value不是int类型时会返回错误,当key不存在是则value为1
     *
     * @param key
     * @return 加值后的结果
     */
    public static Long incr(Cache cache,String key,Integer expire) {
        try {
            return cache.getJedis().incr(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            cache.expire(key, expire);
        }
    }
    /**
     * 覆盖key所对应的值(覆盖，key不存在则为添加)
     * 成功返回ok
     * @Author cwf
     * @Date 2019/12/29 14:06
     */
    public static String setStr(int dbNum,String key, String value,  Integer expire) {
        Cache cache = Redis.use("db" + dbNum);
        try {
            return cache.set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (expire != null) {
                cache.expire(key, expire);
            }
        }
    }
    /**
     * 根据key获取对应的value
     *
     * @Author linyb
     * @Date 2017/2/23 14:04
     */
    public static String getStrVal(String key, Integer dbNum) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Cache cache = Redis.use("db" + dbNum);
        try {
            return cache.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
