package com.github.easy.commons.es.util;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;


public class ContextFactory {

	private static String[] filenames = {"classpath*:spring/*.xml"};
	private static String configLocations = StringUtils.arrayToDelimitedString(filenames, ",");
            
	private static AbstractApplicationContext applicationContext = null;
	
	public static synchronized AbstractApplicationContext getApplicationContext() {
		if(applicationContext == null) {
			applicationContext = new ClassPathXmlApplicationContext(StringUtils.tokenizeToStringArray(configLocations,ConfigurableWebApplicationContext.CONFIG_LOCATION_DELIMITERS));
		}
		return applicationContext;
	}

	public static void setApplicationContext(AbstractApplicationContext applicationContext) {
		ContextFactory.applicationContext = applicationContext;
	}
	
	public static void autowireBeanPropertiesByName(Object existingBean) {
		ContextFactory.getApplicationContext().getAutowireCapableBeanFactory().autowireBeanProperties(existingBean, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);
	}

	public static Object getBean(String beanName) {
		return getApplicationContext().getBean(beanName);
	}

}
