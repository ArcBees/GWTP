/**
 * Copyright 2011 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.dispatch.rpc.server.spring.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;

public class SpringUtils {
    public static <B> B getOrCreate(ApplicationContext applicationContext, Class<B> clazz) throws BeansException {
        try {
            return getInstance(applicationContext, clazz);
        } catch (BeansException ex) {
        }

        return instantiate(applicationContext, clazz);
    }

    @SuppressWarnings("unchecked")
    public static <B> B instantiate(ApplicationContext applicationContext, Class<B> clazz) throws BeansException {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory(applicationContext);
        return (B) beanFactory.createBean(clazz, AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR, false);
    }

    public static <B> void registerBean(ApplicationContext applicationContext,
            B instance) throws BeansException {

        if (applicationContext instanceof GenericApplicationContext) {
            ConfigurableListableBeanFactory beanFactory =
                    ((GenericApplicationContext) applicationContext).getBeanFactory();
            beanFactory.registerSingleton(generateName(beanFactory, createBeanDefinition(instance)), instance);
        } else if (applicationContext instanceof AbstractRefreshableWebApplicationContext) {
            ConfigurableListableBeanFactory beanFactory =
                    ((AbstractRefreshableWebApplicationContext) applicationContext).getBeanFactory();
            beanFactory.registerSingleton(generateName(beanFactory, createBeanDefinition(instance)), instance);
        }
    }

    public static <B> B getInstance(ApplicationContext applicationContext, Class<B> clazz) throws BeansException {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory(applicationContext);
        return beanFactory.getBean(clazz);
    }

    private static <B> RootBeanDefinition createBeanDefinition(B instance) {
        RootBeanDefinition bd = new RootBeanDefinition(instance.getClass(), AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR,
                false);
        bd.setScope(BeanDefinition.SCOPE_SINGLETON);
        return bd;
    }

    private static String generateName(ConfigurableListableBeanFactory registry, RootBeanDefinition definition) {
        String generatedBeanName = definition.getBeanClassName();
        if (generatedBeanName == null) {
            if (definition.getParentName() != null) {
                generatedBeanName = definition.getParentName() + "$child";
            } else if (definition.getFactoryBeanName() != null) {
                generatedBeanName = definition.getFactoryBeanName() + "$created";
            }
        }
        if (!StringUtils.hasText(generatedBeanName)) {
            throw new BeanDefinitionStoreException(
                    "Unnamed bean definition specifies neither 'class' nor 'parent' nor 'factory-bean' - can't " +
                            "generate bean name");
        }

        String id = generatedBeanName;

        // Top-level bean: use plain class name.
        // Increase counter until the id is unique.
        int counter = -1;
        while (counter == -1 || (registry.containsSingleton(id))) {
            counter++;
            id = generatedBeanName + "#" + counter;
        }

        return id;
    }
}
