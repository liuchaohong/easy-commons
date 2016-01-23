package com.github.easy.commons.es.util;

public class ESUtil {
	
	public static final String TOTALHITS = "_totalHits";
	
	public static String getTotalHitsThreadLocalKey(){
		return "es_TOTALHITS";
	}
	
	public static String getScrollIdThreadLocalKey(){
		return "es_scrollId";
	}

}
