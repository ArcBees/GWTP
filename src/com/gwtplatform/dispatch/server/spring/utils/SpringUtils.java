package com.gwtplatform.dispatch.server.spring.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

public class SpringUtils {

	public static <B> B getOrCreate(ApplicationContext applicationContext, Class<B> clazz) throws BeansException {
		try {
			return getInstance(applicationContext, clazz);
		} catch (BeansException ex) {}

		return instantiate(applicationContext, clazz);
	}

	public static <B> B instantiate(ApplicationContext applicationContext, Class<B> clazz) throws BeansException {
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory(applicationContext);
		return beanFactory.createBean(clazz);
	}

	public static <B> B getInstance(ApplicationContext applicationContext, Class<B> clazz) throws BeansException {
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory(applicationContext);
		return beanFactory.getBean(clazz);
	}

	//	@SuppressWarnings("unchecked")
	//	public static <T, B> T getInstance(ApplicationContext applicationContext, Class<B> clazz) {
	//
	//		Map<String, B> beans = applicationContext.getBeansOfType(clazz);
	//
	//		if (beans == null || beans.size() == 0) {
	//			throw new RuntimeException("No bean was found while instantiating bean class: " + clazz.getCanonicalName());
	//		}
	//
	//		if (beans.size() == 1) {
	//			return (T) beans.values().toArray()[0];
	//		}
	//
	//		throw new RuntimeException("More than one bean was found (" + beans.size() + ") while instantiating bean class: " + clazz.getCanonicalName());
	//	}
}