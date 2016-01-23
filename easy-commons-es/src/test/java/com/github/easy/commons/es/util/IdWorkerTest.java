package com.github.easy.commons.es.util;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class IdWorkerTest {
	
	IdWorker idWorker=null;

	@Before
	public void setUp() throws Exception {
		idWorker=new IdWorker(1);
	}

	@Test
	public void testIdWorker() {
		for(int i=0;i<10000;i++){
			System.out.println(idWorker.nextId());
		}
	}

	@Test
	public void testNextId() {
		fail("Not yet implemented");
	}

}
