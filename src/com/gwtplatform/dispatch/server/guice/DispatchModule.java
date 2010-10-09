/**
 * Copyright 2010 ArcBees Inc.
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

package com.gwtplatform.dispatch.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import com.gwtplatform.dispatch.server.Dispatch;
import com.gwtplatform.dispatch.server.RequestProvider;
import com.gwtplatform.dispatch.server.actionhandlervalidator.ActionHandlerValidatorRegistry;
import com.gwtplatform.dispatch.server.actionhandlervalidator.LazyActionHandlerValidatorRegistry;
import com.gwtplatform.dispatch.server.guice.actionhandlervalidator.ActionHandlerValidatorLinker;
import com.gwtplatform.dispatch.server.guice.actionhandlervalidator.LazyActionHandlerValidatorRegistryImpl;
import com.gwtplatform.dispatch.server.guice.request.DefaultRequestProvider;

/**
 * This module will configure the implementation for the {@link Dispatch} and
 * {@link ActionHandlerValidatorRegistry} interfaces. Also every
 * {@link ActionHandler} and {@link ActionValidator} will be loaded lazily.
 * 
 * 
 * If you want to override the defaults ({@link DispatchImpl},
 * {@link LazyActionHandlerValidatorRegistryImpl} pass the override values into
 * the constructor for this module and ensure it is installed <b>before</b> any
 * {@link HandlerModule} instances.
 * 
 * @author Christian Goudreau
 * @author David Peterson
 */
public class DispatchModule extends AbstractModule {
  private Class<? extends Dispatch> dispatchClass;
  private Class<? extends ActionHandlerValidatorRegistry> lazyActionHandlerValidatorRegistryClass;

  public DispatchModule() {
    this(DispatchImpl.class, LazyActionHandlerValidatorRegistryImpl.class);
  }

  public DispatchModule(Class<? extends Dispatch> dispatchClass) {
    this(dispatchClass, LazyActionHandlerValidatorRegistryImpl.class);
  }

  public DispatchModule(
      Class<? extends Dispatch> dispatchClass,
      Class<? extends ActionHandlerValidatorRegistry> lazyActionHandlerValidatorRegistryClass) {
    this.dispatchClass = dispatchClass;
    this.lazyActionHandlerValidatorRegistryClass = lazyActionHandlerValidatorRegistryClass;
  }

  /**
   * Override so that only one instance of this class will ever be installed in
   * an {@link Injector}.
   */
  @Override
  public boolean equals(Object obj) {
    return obj instanceof DispatchModule;
  }

  /**
   * Override so that only one instance of this class will ever be installed in
   * an {@link Injector}.
   */
  @Override
  public int hashCode() {
    return DispatchModule.class.hashCode();
  }

  @Override
  protected final void configure() {
    bind(ActionHandlerValidatorRegistry.class).to(
        lazyActionHandlerValidatorRegistryClass).in(Singleton.class);
    bind(Dispatch.class).to(dispatchClass).in(Singleton.class);
    bind(RequestProvider.class).to(DefaultRequestProvider.class).in(Singleton.class);    
    
    // This will bind registered validators and handlers to the registry lazily.
    if (LazyActionHandlerValidatorRegistry.class.isAssignableFrom(lazyActionHandlerValidatorRegistryClass)) {
      requestStaticInjection(ActionHandlerValidatorLinker.class);
    }
  }

}
