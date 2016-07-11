package com.github.easy.commons.util.http;

import java.util.HashMap;
import java.util.Map;

public class IThreadLocal {
	
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
		return map != null ? map.get(key) : null;
	}
	
	public static void putAll(Map<String, ? extends Object> childMap){
		Map<String,Object> map  = threadLocal.get();
		if(map == null){
			map = new HashMap<String, Object>();
			threadLocal.set(map );
		}
		map .putAll(childMap);
	}
	
	public static void remove(){
		threadLocal.remove();
	}

}
