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

package com.gwtplatform.dispatch.server.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.gwtplatform.dispatch.server.Dispatch;
import com.gwtplatform.dispatch.server.actionhandlervalidator.ActionHandlerValidatorRegistry;
import com.gwtplatform.dispatch.server.actionhandlervalidator.LazyActionHandlerValidatorRegistry;
import com.gwtplatform.dispatch.server.spring.actionhandlervalidator.ActionHandlerValidatorLinker;
import com.gwtplatform.dispatch.server.spring.actionhandlervalidator.LazyActionHandlerValidatorRegistryImpl;
import com.gwtplatform.dispatch.server.spring.utils.SpringUtils;

/**
 * @author Peter Simun
 */
public class DispatchModule {

  private Class<? extends Dispatch> dispatchClass;
  private Class<? extends ActionHandlerValidatorRegistry> lazyActionHandlerValidatorRegistryClass;

  @Autowired
  private ApplicationContext context;

  @Autowired
  private HandlerModule handlerModule;

  public DispatchModule() {
    this(DispatchImpl.class, LazyActionHandlerValidatorRegistryImpl.class);
  }

  public DispatchModule(Class<? extends Dispatch> dispatchClass) {
    this(dispatchClass, LazyActionHandlerValidatorRegistryImpl.class);
  }

  public DispatchModule(Class<? extends Dispatch> dispatchClass, Class<? extends ActionHandlerValidatorRegistry> lazyActionHandlerValidatorRegistryClass) {
    this.dispatchClass = dispatchClass;
    this.lazyActionHandlerValidatorRegistryClass = lazyActionHandlerValidatorRegistryClass;
  }

  @Bean
  public ActionHandlerValidatorRegistry getActionHandlerValidatorRegistry() {
    handlerModule.configureHandlers();

    ActionHandlerValidatorRegistry instance = SpringUtils.getOrCreate(context, lazyActionHandlerValidatorRegistryClass);

    // TODO check this out
    if (LazyActionHandlerValidatorRegistry.class.isAssignableFrom(lazyActionHandlerValidatorRegistryClass)) {
      ActionHandlerValidatorLinker.linkValidators(context, instance);
    }

    return instance;
  }

  @Bean
  public Dispatch getDispatch() {
    Dispatch instance = SpringUtils.getOrCreate(context, dispatchClass);
    return instance;
  }
}
