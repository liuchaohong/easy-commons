package com.github.easy.commons.redis;

import org.junit.Assert;
import org.junit.Test;

import redis.clients.jedis.JedisPool;

import com.github.easy.commons.redis.RedisTemplate;


public class RedisTemplateTest extends Assert {

	String key = "RedisTemplateTest_"+System.currentTimeMillis();
	
	@Test
	public void test() {
		RedisTemplate t = new RedisTemplate();
		
		t.setJedisPool(new JedisPool("113.108.228.101"));
		
		t.set(key, "100");
		assertEquals("100",t.get(key));
	}
}
