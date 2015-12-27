package com.github.easy.commons.es;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.easy.commons.es.EsService;

/**
 * Unit test for simple App.
 */
public class EsTest {
	
	private ApplicationContext ctx;
	private EsService esService;
	
	@Before
	public void before(){
		ctx = new ClassPathXmlApplicationContext("classpath:spring/*.xml");
	}
	
	@Test
	public void testEs(){
		esService = (EsService) ctx.getBean("esService");
		esService.createIndex("movies");
	}
	
}
