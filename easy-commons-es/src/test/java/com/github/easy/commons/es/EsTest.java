package com.github.easy.commons.es;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.client.Client;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.easy.commons.es.client.EsClient;
import com.github.easy.commons.es.model.EsModel;
import com.github.easy.commons.es.model.Mapping;
import com.github.easy.commons.es.service.EsService;

/**
 * 直接下载：
 * https://www.elastic.co/downloads/elasticsearch
 * 本地运行bin/elasticsearch.bat
 */
public class EsTest {
	
	private EsClient esClient;
	private Client client;
	private EsService esService;
	private String index = "testindex";
	private String type = "testtype";
	
	@Before
	public void before(){
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");
		esClient = (EsClient) ctx.getBean("esClient");
		client = esClient.getClient();
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
	
	@Test
	public void testCreateTypeAndMapping() throws Exception {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("id", new Mapping("string", "not_analyzed", "yes"));
		map.put("title", new Mapping("string", "analyzed", "yes"));
		map.put("content", new Mapping("string", "analyzed", "yes"));
		map.put("num", new Mapping("integer", "not_analyzed", "yes"));
		Assert.assertTrue(esService.createTypeAndMapping(index, type, map));
	}
	
	/*@Test
	public void testDeleteType() throws Exception {
		Assert.assertTrue(esService.deleteType(index, type));
	}*/
	
	@Test
	public void testIndexMap() throws Exception {
		int num=1;
		int bulkNum=10000;
		long start=System.currentTimeMillis();
		int i=bulkNum;
		while(i>0){
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("id", UUID.randomUUID().toString());
			map.put("title", "test name"+i);
			map.put("content", "test content"+i);
			map.put("num", i);
			i--;
			System.out.println(map.get("id"));
			Assert.assertTrue(esService.indexMap(index, type, map));
		}
		System.out.println((num*bulkNum)+"条记录用时"+((System.currentTimeMillis()-start))+"ms");
	}
	
	@Test
	public void testIndexList() throws Exception {
		int num=1000;
		int bulkNum=10000;
		long start=System.currentTimeMillis();
		for(int j=0;j<num;j++){
			int i=bulkNum;
			List<Object> list=new ArrayList<Object>();
			long startx=System.currentTimeMillis();
			while(i>0){
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("id", UUID.randomUUID().toString());
				map.put("title", "test name"+i);
				map.put("content", "test content"+i);
				map.put("num", i);
				i--;
				list.add(map);
				//Assert.assertTrue(esaction.indexMap(index, type, map));
			}
			//System.out.println("DDD用时"+((System.currentTimeMillis()-startx))+"ms");
			startx=System.currentTimeMillis();
			Assert.assertTrue(esService.indexList(index, type, list));
			System.out.println(j+"用时:"+((System.currentTimeMillis()-startx))+"ms");
		}
		CountResponse res = client.prepareCount(index).execute().actionGet();
		long sumTime=System.currentTimeMillis()-start;
		System.out.println("本次测试每次提交"+bulkNum+"条，总计"+num+"次，合计"+(num*bulkNum)+"条记录用时"+(sumTime)+"ms,平均每秒处理："+(num*bulkNum*1000)/sumTime+"条,"+"此时数据合计有"+res.getCount()+"条。");
	}

	@Test
	public void testGetIndexById() throws Exception {
		EsModel model=esService.getIndexById(index, type, "abcdefghijklmn", EsModel.class);
		Assert.assertTrue(model!=null&&model.getId().equals("abcdefghijklmn"));
	}

	/*@Test
	public void testIndexModel() throws Exception {
		EsModel model=new EsModel();
		model.setId("abcdefghijklmn");
		model.setTitle("wo meng 一起吧");
		model.setContent("oh my god ,天啊 我的上帝！");;
		model.setNum(123);
		Assert.assertTrue(esService.indexModel(index, type, model));
	}*/

	@Test
	public void testDeleteIndexById() throws Exception {
		Assert.assertTrue(esService.deleteIndexById(index, type, "abcdefghijklmn"));
		EsModel model=esService.getIndexById(index, type, "abcdefghijklmn", EsModel.class);
		Assert.assertTrue(model==null);
	}

	/*@Test
	public void testDeleteIndexByQuery() throws Exception {
		QueryBuilder builder=QueryBuilders.matchQuery("title", "一起吧");
		esService.deleteIndexByQuery(index,builder);
		EsModel model=esService.getIndexById(index, type, "abcdefghijklmn", EsModel.class);
		Assert.assertTrue(model==null);
	}*/	
	
	
	
}
