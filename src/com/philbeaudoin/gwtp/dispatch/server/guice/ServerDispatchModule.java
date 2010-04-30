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
 * interfaces. If you want to override the defaults ({@link GuiceDispatch},
 * {@link DefaultActionHandlerRegistry} and
 * {@link DefaultActionValidatorRegistry}, respectively), pass the override
 * values into the constructor for this module and ensure it is installed
 * <b>before</b> any {@link DispatchModule}. instances.
 * 
 * @author Christian Goudreau
 * @author David Peterson
 */
public class ServerDispatchModule extends AbstractModule {
  private Class<? extends Dispatch> dispatchClass;
  private Class<? extends ActionHandlerRegistry> actionHandlerRegistryClass;
  private Class<? extends ActionValidatorRegistry> actionValidatorRegistryClass;

  public ServerDispatchModule() {
    this(DispatchImpl.class, DefaultActionHandlerRegistry.class, DefaultActionValidatorRegistry.class);
  }

  public ServerDispatchModule(Class<? extends Dispatch> dispatchClass) {
    this(dispatchClass, DefaultActionHandlerRegistry.class, DefaultActionValidatorRegistry.class);
  }

  public ServerDispatchModule(Class<? extends Dispatch> dispatchClass, Class<? extends ActionHandlerRegistry> actionHandlerRegistryClass,
      Class<? extends ActionValidatorRegistry> actionValidatorRegistryClass) {
    this.dispatchClass = dispatchClass;
    this.actionHandlerRegistryClass = actionHandlerRegistryClass;
    this.actionValidatorRegistryClass = actionValidatorRegistryClass;
  }

  @Override
  protected final void configure() {
    bind(ActionHandlerRegistry.class).to(getActionHandlerRegistryClass()).in(Singleton.class);
    bind(ActionValidatorRegistry.class).to(getActionValidatorRegistryClass()).in(Singleton.class);
    bind(Dispatch.class).to(getDispatchClass()).in(Singleton.class);

    // This will bind registered handlers to the registry.
    if (InstanceActionHandlerRegistry.class.isAssignableFrom(getActionHandlerRegistryClass()))
      requestStaticInjection(ActionHandlerLinker.class);

    // This will bind registered validators to the registry.
    if (InstanceActionValidatorRegistry.class.isAssignableFrom(getActionValidatorRegistryClass()))
      requestStaticInjection(ActionValidatorLinker.class);
  }

  /**
   * The class returned by this method is bound to the {@link Dispatch}
   * service. Subclasses may override this method to provide custom
   * implementations. Defaults to {@link GuiceDispatch}.
   * 
   * @return The {@link Dispatch} implementation class.
   */
  protected Class<? extends Dispatch> getDispatchClass() {
    return dispatchClass;
  }

  /**
   * The class returned by this method is bound to the
   * {@link ActionHandlerRegistry}. Subclasses may override this method to
   * provide custom implementations. Defaults to
   * {@link DefaultActionHandlerRegistry}.
   * 
   * @return The {@link ActionHandlerRegistry} implementation class.
   */
  protected Class<? extends ActionHandlerRegistry> getActionHandlerRegistryClass() {
    return actionHandlerRegistryClass;
  }

  /**
   * The class returned by this method is bound to the
   * {@link ActionValidatorRegistry}. Subclasses may override this method to
   * provide custom implementations. Defaults to
   * {@link DefaultActionValidatorRegistry}.
   * 
   * @return the {@link ActionValidatorRegistry} implementation class.
   */
  protected Class<? extends ActionValidatorRegistry> getActionValidatorRegistryClass() {
    return actionValidatorRegistryClass;
  }

  /**
   * Override so that only one instance of this class will ever be installed
   * in an {@link Injector}.
   */
  @Override
  public boolean equals(Object obj) {
    return obj instanceof ServerDispatchModule;
  }

  /**
   * Override so that only one instance of this class will ever be installed
   * in an {@link Injector}.
   */
  @Override
  public int hashCode() {
    return ServerDispatchModule.class.hashCode();
  }

}
