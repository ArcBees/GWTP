/**
 * Copyright 2010 Philippe Beaudoin
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
import com.philbeaudoin.gwtp.dispatch.server.DispatchImpl;
import com.philbeaudoin.gwtp.dispatch.server.Dispatch;
import com.philbeaudoin.gwtp.dispatch.server.actionHandler.ActionHandlerLinker;
import com.philbeaudoin.gwtp.dispatch.server.actionHandler.ActionHandlerRegistry;
import com.philbeaudoin.gwtp.dispatch.server.actionHandler.DefaultActionHandlerRegistry;
import com.philbeaudoin.gwtp.dispatch.server.actionHandler.InstanceActionHandlerRegistry;
import com.philbeaudoin.gwtp.dispatch.server.actionValidator.ActionValidatorLinker;
import com.philbeaudoin.gwtp.dispatch.server.actionValidator.ActionValidatorRegistry;
import com.philbeaudoin.gwtp.dispatch.server.actionValidator.DefaultActionValidatorRegistry;
import com.philbeaudoin.gwtp.dispatch.server.actionValidator.InstanceActionValidatorRegistry;

/**
 * This module will configure the implementation for the {@link Dispatch},
 * {@link ActionHandlerRegistry} interfaces and {@link ActionValidatorRegistry}
 * interfaces. If you want to override the defaults ({@link DispatchImpl},
 * {@link DefaultActionHandlerRegistry} and
 * {@link DefaultActionValidatorRegistry}, respectively), pass the override
 * values into the constructor for this module and ensure it is installed
 * <b>before</b> any {@link HandlerModule} instances.
 * 
 * @author Christian Goudreau
 * @author David Peterson
 */
public class DispatchModule extends AbstractModule {
  private Class<? extends Dispatch> dispatchClass;
  private Class<? extends ActionHandlerRegistry> actionHandlerRegistryClass;
  private Class<? extends ActionValidatorRegistry> actionValidatorRegistryClass;

  public DispatchModule() {
    this(DispatchImpl.class, DefaultActionHandlerRegistry.class, DefaultActionValidatorRegistry.class);
  }

  public DispatchModule(Class<? extends Dispatch> dispatchClass) {
    this(dispatchClass, DefaultActionHandlerRegistry.class, DefaultActionValidatorRegistry.class);
  }

  public DispatchModule(Class<? extends Dispatch> dispatchClass, Class<? extends ActionHandlerRegistry> actionHandlerRegistryClass,
      Class<? extends ActionValidatorRegistry> actionValidatorRegistryClass) {
    this.dispatchClass = dispatchClass;
    this.actionHandlerRegistryClass = actionHandlerRegistryClass;
    this.actionValidatorRegistryClass = actionValidatorRegistryClass;
  }

  @Override
  protected final void configure() {
    bind(ActionHandlerRegistry.class).to(actionHandlerRegistryClass).in(Singleton.class);
    bind(ActionValidatorRegistry.class).to(actionValidatorRegistryClass).in(Singleton.class);
    bind(Dispatch.class).to(dispatchClass).in(Singleton.class);

    // This will bind registered handlers to the registry.
    if (InstanceActionHandlerRegistry.class.isAssignableFrom(actionHandlerRegistryClass))
      requestStaticInjection(ActionHandlerLinker.class);

    // This will bind registered validators to the registry.
    if (InstanceActionValidatorRegistry.class.isAssignableFrom(actionValidatorRegistryClass))
      requestStaticInjection(ActionValidatorLinker.class);
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
