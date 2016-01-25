package com.github.easy.commons.redis;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import redis.clients.jedis.JedisPool;
/**
 * 
 * redis dao base
 *
 */
public class RedisDaoSupport implements InitializingBean{
	
	private RedisTemplate redisTemplate;
	
	public RedisTemplate getRedisTemplate() {
		return redisTemplate;
	}
	
	public void setJedisPool(JedisPool jedisPool) {
		setRedisTemplate(new RedisTemplate(jedisPool));
	}

	public void setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(redisTemplate,"redisTemplate must be not null");
	}
	
}
