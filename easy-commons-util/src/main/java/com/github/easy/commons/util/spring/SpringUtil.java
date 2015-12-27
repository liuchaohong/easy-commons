package com.github.easy.commons.util.spring;

import org.springframework.beans.factory.InitializingBean;

/**
 * springbean 初始化
 * @author LIUCHAOHONG
 *
 */
public class SpringUtil {
	
	public static void initializing(Object[] array) {
		if(array == null) return;
		
		for(Object obj : array) {
			SpringUtil.initializing(obj);
		}
	}
	
	public static void initializing(Object obj) {
		if(obj == null) return;
		
		if(obj instanceof InitializingBean) {
			try {
				((InitializingBean)obj).afterPropertiesSet();
			}catch(Exception e) {
				throw new IllegalStateException(e);
			}
		}
	}
	
}
