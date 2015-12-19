package com.github.easy.commons.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring Context工具类
 * @author LIUCHAOHONG
 *
 */
public class SpringContextUtil implements ApplicationContextAware{

	private static ApplicationContext context = null;

	public static Object getBean(String beanId) {
		return context.getBean(beanId);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
	}
	
}
