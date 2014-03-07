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

package com.gwtplatform.dispatch.rpc.server.spring;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.gwtplatform.dispatch.rpc.server.Dispatch;
import com.gwtplatform.dispatch.rpc.server.actionhandlervalidator.ActionHandlerValidatorRegistry;
import com.gwtplatform.dispatch.rpc.server.actionhandlervalidator.LazyActionHandlerValidatorRegistry;
import com.gwtplatform.dispatch.rpc.server.spring.actionhandlervalidator.ActionHandlerValidatorLinker;
import com.gwtplatform.dispatch.rpc.server.spring.actionhandlervalidator.LazyActionHandlerValidatorRegistryImpl;
import com.gwtplatform.dispatch.rpc.server.spring.utils.SpringUtils;

/**
 * Dispatch module spring configuration.
 */
public class DispatchModule {
    private final Class<? extends Dispatch> dispatchClass;
    private final Class<? extends ActionHandlerValidatorRegistry> lazyActionHandlerValidatorRegistryClass;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private List<HandlerModule> handlerModules;

    public DispatchModule() {
        this(DispatchImpl.class, LazyActionHandlerValidatorRegistryImpl.class);
    }

    public DispatchModule(Class<? extends Dispatch> dispatchClass) {
        this(dispatchClass, LazyActionHandlerValidatorRegistryImpl.class);
    }

    public DispatchModule(Class<? extends Dispatch> dispatchClass,
            Class<? extends ActionHandlerValidatorRegistry> lazyActionHandlerValidatorRegistryClass) {
        this.dispatchClass = dispatchClass;
        this.lazyActionHandlerValidatorRegistryClass = lazyActionHandlerValidatorRegistryClass;
    }

    @Autowired
    public void setHandlerModules(List<HandlerModule> handlerModules) {
        this.handlerModules = handlerModules;
    }

    @Bean
    public ActionHandlerValidatorRegistry getActionHandlerValidatorRegistry() {
        for (HandlerModule handlerModule : handlerModules) {
            handlerModule.configureHandlers();
        }

        ActionHandlerValidatorRegistry instance = SpringUtils.getOrCreate(context,
                lazyActionHandlerValidatorRegistryClass);

        // TODO check this out
        if (LazyActionHandlerValidatorRegistry.class.isAssignableFrom(lazyActionHandlerValidatorRegistryClass)) {
            ActionHandlerValidatorLinker.linkValidators(context, instance);
        }

        return instance;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public Dispatch getDispatch() {
        return SpringUtils.getOrCreate(context, dispatchClass);
    }
}
