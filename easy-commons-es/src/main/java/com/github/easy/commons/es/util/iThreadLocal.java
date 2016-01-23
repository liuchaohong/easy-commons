package com.github.easy.commons.es.util;

import java.util.HashMap;
import java.util.Map;

public class iThreadLocal {
	
	private static ThreadLocal<Map<String,Object>> threadLocal = new ThreadLocal<Map<String,Object>>();

	public static void set(String key,Object value){
		Map<String,Object> map = threadLocal.get();
		if(map == null){
			map = new HashMap<String, Object>();
			threadLocal.set(map);
		}
		map.put(key, value);
	}
	
	public static Object get(String key){
		Map<String,Object> map = threadLocal.get();
		return map!=null ? map.get(key):null;
	}
	
	public static void putAll(Map<String, ? extends Object> map){
		Map<String,Object> imap = threadLocal.get();
		if(imap == null){
			imap = new HashMap<String, Object>();
			threadLocal.set(imap);
		}
		imap.putAll(map);
	}

}
