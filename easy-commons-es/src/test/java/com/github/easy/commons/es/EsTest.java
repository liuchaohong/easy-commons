package com.github.easy.commons.es;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 直接下载：
 * https://www.elastic.co/downloads/elasticsearch
 * 本地运行bin/elasticsearch.bat
 */
public class EsTest {
	
	private EsService esService;
	
	@Before
	public void before(){
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");
		esService = (EsService) ctx.getBean("esService");
	}
	
	@Test
	public void createIndex(){
		boolean result = esService.createIndex("movies3");
		System.out.println(result);
	}

	@Test
	public void getAllIndex(){
		Set<String> indexs = esService.getAllIndex();
		System.out.println(indexs);
	}
	
	@Test
	public void deleteIndex(){
		boolean result = esService.deleteIndex("goods");
		System.out.println(result);
	}
	
	@Test
	public void isExistsIndex(){
		boolean result = esService.isExistsIndex("movies");
		System.out.println(result);
	}
	
	
	
	
	
	
	
}
