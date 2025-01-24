/*
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

package com.gwtplatform.dispatch.rpc.server.spring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.context.ApplicationContext;

import com.gwtplatform.dispatch.rpc.server.actionhandlervalidator.ActionHandlerValidatorLinkerHelper.BeanProvider;
import com.gwtplatform.dispatch.rpc.server.actionhandlervalidator.ActionHandlerValidatorLinkerHelper.CommonBindingDescriptor;
import com.gwtplatform.dispatch.rpc.server.spring.utils.SpringUtils;

public class SpringBeanProvider implements BeanProvider {
    /**
     * Adapter for transforming Guice Binding into BeanProvider implementation.
     */
    public static class SpringBindingDescriptorAdapter<B> extends CommonBindingDescriptor<B> {
        public SpringBindingDescriptorAdapter(Entry<String, B> binding) {
            super(binding.getValue(), binding.getKey());
        }
    }

    private final ApplicationContext applicationContext;

    public SpringBeanProvider(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <B> B getInstance(Class<B> clazz) {
        return SpringUtils.getInstance(applicationContext, clazz);
    }

    @Override
    public <B> Iterator<BindingDescriptor<B>> getBindings(Class<B> clazz) {
        List<BindingDescriptor<B>> result = new ArrayList<BindingDescriptor<B>>();

        Map<String, B> beansOfType = applicationContext.getBeansOfType(clazz);

        for (Entry<String, B> beans : beansOfType.entrySet()) {
            result.add(new SpringBindingDescriptorAdapter<B>(beans));
        }

        return result.iterator();
    }
}
