package com.xiaosong.util;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.redis.RedisPlugin;
import redis.clients.jedis.Jedis;

/**
 * Redis加载类
 * @author wgm
 * 2019-11-06
 *
 */
public class ESRedisPlugin{

	private String hosts= PropKit.get("redis.hosts");//redis服务器地址
	private int port=PropKit.getInt("redis.port");//端口号
	private int timeout=PropKit.getInt("redis.timeout");//过期时间  单位：秒
	private String password=PropKit.get("redis.password");//密码
	private int maxTotal=PropKit.getInt("redis.maxTotal");//最大实例总数
	private int maxIdle=PropKit.getInt("redis.maxIdle");//空闲数 单位：秒
	private int maxWait = PropKit.getInt("redis.maxWaitMillis");//等待时间 单位：秒
	private int dataBase= PropKit.getInt("redis.database") ;//缓存库编号 单位：秒
	private String defaultCacheName = "REDIS";//默认缓存库名称
	
	RedisPlugin redisPlugin;
	
	public RedisPlugin config(){
		try {
			redisPlugin = new RedisPlugin(defaultCacheName, hosts, port, timeout*1000, password, dataBase);
			redisPlugin = config(redisPlugin);
		} catch (Exception e) {
	        throw new RuntimeException("init RedisPlugin config exception ",e);
		}
		return redisPlugin;
	}
	public RedisPlugin config(String CacheName, int dataBase){
			this.defaultCacheName=CacheName;
			this.dataBase=dataBase;
			redisPlugin =config();
		return redisPlugin;
	}
	public RedisPlugin config(RedisPlugin redisPlugin){
		redisPlugin.getJedisPoolConfig().setMaxTotal(maxTotal);
		redisPlugin.getJedisPoolConfig().setMaxIdle(maxIdle);
		redisPlugin.getJedisPoolConfig().setMaxWaitMillis(maxWait*1000);
		return redisPlugin;
	}
}
