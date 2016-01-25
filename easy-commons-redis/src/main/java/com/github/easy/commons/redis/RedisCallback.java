package com.github.easy.commons.redis;

import redis.clients.jedis.Jedis;
/**
 * Redis模板方法的回调接口
 * 
 * @see RedisTemplate
 * @param <T>
 */
public interface RedisCallback <T>{
	
	public T doInRedis(Jedis jedis);
	
}
