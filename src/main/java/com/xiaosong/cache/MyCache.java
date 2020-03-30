package com.xiaosong.cache;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.cache.ICache;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;

/**
* @author xiaojf
* @version 创建时间：2019年12月3日 下午4:29:21
* 类说明
*/
public class MyCache implements ICache {
    public static final MyCache cache = new MyCache();
    private static String getCacheType() {
        return PropKit.get("cache.type", "ehcache");
    }

    public static boolean isEhCache() {
        return "ehcache".equals(getCacheType());
    }

    public static boolean isRedis() {
        return "redis".equals(getCacheType());
    }
 
    public <T> T get(String cacheName, Object key) {
        if (isEhCache()) {
            return CacheKit.get(cacheName, key);
 
        } else if (isRedis()) {
            return Redis.use(cacheName).get(key);
        }
        return null;
    }
 
    public void put(String cacheName, Object key, Object value) {
        if (isEhCache()) {
            CacheKit.put(cacheName, key, value);
 
        } else if (isRedis()) {
            Redis.use(cacheName).set(key, value);
        }
    }
 
    public void remove(String cacheName, Object key) {
        if (isEhCache()) {
            CacheKit.remove(cacheName, key);
 
        } else if (isRedis()) {
            Redis.use(cacheName).del(key);
        }
 
    }
 
    public void removeAll(String cacheName) {
        if (isEhCache()) {
            CacheKit.removeAll(cacheName);
        } else if (isRedis()) {
            Cache cache = Redis.use(cacheName);
            cache.del(cache.keys("*").toArray());
        }
 
    }
	
}
