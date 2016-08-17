package com.github.easy.commons.util.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

public class UrlUtil {

	public static String decode(String content)  {
		try {
			return URLDecoder.decode(content, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("url encode error, content:" + content, e);
		}
	}
	
	public static String encode(String content)  {
		try {
			return URLEncoder.encode(content, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("url encode error, content:" + content, e);
		}
	}
	
	@SuppressWarnings("rawtypes") 
	public static String toUrlParams(Map<String, String> param) {
		StringBuilder sb = new StringBuilder();
		for(Map.Entry entry : param.entrySet()) {
			Object value = entry.getValue();
			Object key = entry.getKey();
			if(value == null) {
				continue;
			}
			sb.append(key+"="+encode(value.toString())).append("&");
		}
		return sb.toString();
	}
	
}
