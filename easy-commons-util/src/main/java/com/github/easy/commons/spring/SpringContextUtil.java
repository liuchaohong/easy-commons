package com.github.easy.commons.spring;

import org.springframework.context.ApplicationContext;

/**
 * Spring Context工具类
 * @author LIUCHAOHONG
 *
 */
public class SpringContextUtil {

	private static ApplicationContext context = null;

	public static ApplicationContext getContext() {
		return context;
	}

	public static void setContext(ApplicationContext context) {
		SpringContextUtil.context = context;
	}

	public static Object getBean(String beanId) {
		return context.getBean(beanId);
	}
	
}
