/**
 * Copyright 2010 Gwt-Platform
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philbeaudoin.gwtp.dispatch.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.philbeaudoin.gwtp.dispatch.server.Dispatch;
import com.philbeaudoin.gwtp.dispatch.server.DispatchImpl;
import com.philbeaudoin.gwtp.dispatch.server.actionHandler.ActionHandler;
import com.philbeaudoin.gwtp.dispatch.server.actionHandlerValidator.ActionHandlerValidatorLinker;
import com.philbeaudoin.gwtp.dispatch.server.actionHandlerValidator.ActionHandlerValidatorRegistry;
import com.philbeaudoin.gwtp.dispatch.server.actionHandlerValidator.EagerActionHandlerValidatorRegistryImpl;
import com.philbeaudoin.gwtp.dispatch.server.actionHandlerValidator.EagerActionHandlerValidatorRegistry;
import com.philbeaudoin.gwtp.dispatch.server.actionValidator.ActionValidator;

/**
 * This module will configure the implementation for the {@link Dispatch} and
 * {@link ActionHandlerValidatorRegistry} interfaces. Also every
 * {@link ActionHandler} and {@link ActionValidator} will be loaded eagerly.
 * 
 * 
 * If you want to override the defaults ({@link DispatchImpl},
 * {@link EagerActionHandlerValidatorRegistryImpl} pass the override values into
 * the constructor for this module and ensure it is installed <b>before</b> any
 * {@link HandlerModule} instances.
 * 
 * @author Christian Goudreau
 */
public class EagerDispatchModule extends AbstractModule {
  private Class<? extends Dispatch> dispatchClass;
  private Class<? extends ActionHandlerValidatorRegistry> actionHandlerValidatorRegistryClass;

  public EagerDispatchModule() {
    this(DispatchImpl.class, EagerActionHandlerValidatorRegistryImpl.class);
  }

  public EagerDispatchModule(Class<? extends Dispatch> dispatchClass) {
    this(dispatchClass, EagerActionHandlerValidatorRegistryImpl.class);
  }

  public EagerDispatchModule(Class<? extends Dispatch> dispatchClass, Class<? extends ActionHandlerValidatorRegistry> actionHandlerValidatorRegistryClass) {
    this.dispatchClass = dispatchClass;
    this.actionHandlerValidatorRegistryClass = actionHandlerValidatorRegistryClass;
  }

  @Override
  protected final void configure() {
    bind(ActionHandlerValidatorRegistry.class).to(actionHandlerValidatorRegistryClass).in(Singleton.class);
    bind(Dispatch.class).to(dispatchClass).in(Singleton.class);

    // This will bind registered validators and handlers to the registry eagerly.
    if (EagerActionHandlerValidatorRegistry.class.isAssignableFrom(actionHandlerValidatorRegistryClass))
      requestStaticInjection(ActionHandlerValidatorLinker.class);
  }

  /**
   * Override so that only one instance of this class will ever be installed
   * in an {@link Injector}.
   */
  @Override
  public boolean equals(Object obj) {
    return obj instanceof DispatchModule;
  }

  /**
   * Override so that only one instance of this class will ever be installed
   * in an {@link Injector}.
   */
  @Override
  public int hashCode() {
    return DispatchModule.class.hashCode();
  }
}
