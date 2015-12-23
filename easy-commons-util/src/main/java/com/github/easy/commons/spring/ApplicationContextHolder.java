package com.github.easy.commons.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring Context工具类
 * 用法；
 * 在项目中引入bean
 * <bean class="com.github.easy.commons.spring.ApplicationContextHolder"/>
 * @author LIUCHAOHONG
 *
 */
public class ApplicationContextHolder implements ApplicationContextAware{

	private static Logger logger = LoggerFactory.getLogger(ApplicationContextHolder.class);
	private static ApplicationContext applicationContext;
	
	@SuppressWarnings("all")
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.applicationContext = context;
		logger.info("holded applicationContext,displayName:"+applicationContext.getDisplayName());
	}
	
	public static ApplicationContext getApplicationContext() {
		if(applicationContext == null)
			throw new IllegalStateException("'applicationContext' property is null,ApplicationContextHolder not yet init.");
		return applicationContext;
	}
	
	public static Object getBean(String beanName) {
		return getApplicationContext().getBean(beanName);
	}
	
	public static <T> T getBean(Class<T> clazz) {
	    T beanInstance;
	    try {
	      beanInstance = applicationContext.getBean(clazz);
	    } catch (NoSuchBeanDefinitionException e) {
	    	logger.info(String.format("Bean '{}' not be found", clazz));
	      return null;
	    }
	    return beanInstance;
	}

	public static <T> T getBean(String beanName, Class<T> clazz) {
		Object beanInstance = getBean(beanName);
		if (beanInstance == null) {
			return null;
		}
		return clazz.cast(beanInstance);
	}
		  
	public static void cleanHolder() {
		applicationContext = null;
	}
	
}
