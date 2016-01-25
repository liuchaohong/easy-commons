package com.github.easy.commons.redis;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

public class RedisTest {
	
	private RedisTemplate redisTemplate;
	
	@Before
	public void setUp() throws Exception {
		ApplicationContext ctx = ContextFactory.getApplicationContext();
		redisTemplate = (RedisTemplate) ctx.getBean("redisTemplate");
	}
	
	@Test
	public void test(){
		String value = redisTemplate.get("test");
		System.out.println(value);
	}
	
}
