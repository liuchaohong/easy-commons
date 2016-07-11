package com.github.easy.commons.util.http;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class CookieMap implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Map<String, Map<String,String>> cookieMap = new HashMap<String, Map<String,String>>();
	
	public void set(String key,Map<String,String> value){
		Map<String,String> map = cookieMap.get(key);
		if(map == null){
			cookieMap.put(key, value);
		}else{
			map.putAll(value);
		}
	}
	
	public void set(String key,String name,String value){
		Map<String,String> map = cookieMap.get(key);
		if(map == null){
			map = new HashMap<String,String>();
			map.put(name, value);
			cookieMap.put(key, map);
		}else{
			map.put(name, value);
		}
	}
	
	public Map<String,String> getCookieMap(String domain){
		return cookieMap.get(domain);
	}
	
	public String set(String host,String cookie){
		if(StringUtils.isBlank(host) || StringUtils.isBlank(cookie)){
			return null;
		}
		cookie=cookie.trim();
		String[] values = cookie.split(";");
		String domain = null;
		Map<String,String> map = new HashMap<String,String>();
		for(String str : values){
			str = str.trim();
			if(StringUtils.isNotBlank(str) && str.indexOf("=") > 0 && str.lastIndexOf("=") > 0){
				String[] keyValue = str.split("=");
				if(keyValue.length == 2){
					keyValue[0] = keyValue[0].trim();
					if("domain".equalsIgnoreCase(keyValue[0])){
						domain = keyValue[1];
					}else if("expires".equalsIgnoreCase(keyValue[0]) || "path".equalsIgnoreCase(keyValue[0]) || "Max-Age".equalsIgnoreCase(keyValue[0])){
						continue;
					}else{
						if(keyValue[0].startsWith("secure")){
							keyValue[0] = keyValue[0].split(" ")[1];
						}
						keyValue[0] = keyValue[0].trim();
						keyValue[1] = keyValue[1].trim();
						if(StringUtils.isNotBlank(keyValue[0]) && StringUtils.isNotBlank(keyValue[1])){
							map.put(keyValue[0], keyValue[1].trim());
						}
					}
				}
			}
		}
		if(!map.isEmpty() && (host.indexOf(".") > 0 || StringUtils.countMatches(host, ".") > 1)){
			if(StringUtils.isNotBlank(domain) && host.endsWith(domain)){
				set(domain, map);
				return domain;
			}else{
				set(host, map);
				return host;
			}
			
		}
		return null;
	}
	
	public String get(String host){
		Map<String,String> map = cookieMap.get(host);
		String cookies = null;
		if(map != null && !map.isEmpty()){
			findCookieMap(host);
			for(String key : map.keySet()){
				String value = map.get(key);
				if(cookies != null){
					cookies = cookies + ";" + key + "=" +value;
				}else{
					cookies = key + "=" + value;
				}
			}
			return cookies;
		}
		return null;
	}
	
	public Map<String,String> findCookieMap(String host){
		Map<String,String> map = cookieMap.get(host);
		if(map == null){
			if(StringUtils.countMatches(host, ".") == 1){
				return null;
			}
			int pos = host.indexOf(".");
			if(pos > -1){
				if(host.indexOf(".") == 0 && StringUtils.countMatches(host, ".") > 1){
					host = host.substring(pos + 1);
				}else if(host.indexOf(".") > 0){
					host = host.substring(pos);
				}
				return findCookieMap(host);
			}
			return null;
		}else{
			return map;
		}
	}
	
	public void clear(){
		cookieMap.clear();
	}
}
