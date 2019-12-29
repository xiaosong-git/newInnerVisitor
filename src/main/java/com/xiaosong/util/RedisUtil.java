package com.xiaosong.util;

import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import redis.clients.jedis.Jedis;

/**
 * @program: jfinal_demo_for_maven
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
            return cache.incr(key);
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
    public static String setStr(Cache cache,String key, String value,  Integer expire) {
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
}
