package com.gwtplatform.dispatch.server.spring.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class SpringUtils {

	public static <B> B getOrCreate(ApplicationContext applicationContext, Class<B> clazz) throws BeansException {
		try {
			return getInstance(applicationContext, clazz);
		} catch (BeansException ex) {}

		return instantiate(applicationContext, clazz);
	}

	@SuppressWarnings("unchecked")
	public static <B> B instantiate(ApplicationContext applicationContext, Class<B> clazz) throws BeansException {
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory(applicationContext);
		return (B) beanFactory.createBean(clazz, AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR, false);
	}

	public static <B> void registerBean(ApplicationContext applicationContext, B instance) throws BeansException {
		DefaultListableBeanFactory beanFactory = null;

		if (applicationContext instanceof GenericApplicationContext) {
			beanFactory = ((GenericApplicationContext) applicationContext).getDefaultListableBeanFactory();
		} else {
			beanFactory = new DefaultListableBeanFactory(applicationContext);
		}
		RootBeanDefinition bd = new RootBeanDefinition(instance.getClass(), AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR, false);
		bd.setScope(BeanDefinition.SCOPE_SINGLETON);
		beanFactory.registerSingleton(new DefaultBeanNameGenerator().generateBeanName(bd, beanFactory), instance);
	}

	public static <B> B getInstance(ApplicationContext applicationContext, Class<B> clazz) throws BeansException {
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory(applicationContext);
		return beanFactory.getBean(clazz);
	}
}