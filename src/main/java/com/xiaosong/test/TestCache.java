package com.xiaosong.test;

import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;

/**
 * @program: xiaosong
 * @description:
 * @author: cwf
 * @create: 2019-12-30 17:06
 **/
public class TestCache {
        private static final Cache testCache = Redis.use() ;
        public static void incrThenGet() {
//            testCache.incr("ikey") ;
            testCache.getJedis().incr("ikey");
            testCache.getJedis().get("ikey") ;
        }
        public static void setThenIncr() {
            testCache.set("ikey", 0) ;
            testCache.incr("ikey") ;
        }

}
