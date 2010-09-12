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

package com.gwtplatform.dispatch.client.actionhandler;

import com.google.inject.Singleton;

import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

import java.util.HashMap;

/**
 * This is a eager-loading implementation of the registry. It will create action
 * handlers and validators at startup. All {@link ClientActionHandler}
 * implementations <b>must</b> have a public, default constructor.
 * 
 * @author Christian Goudreau
 */
@Singleton
public class ClientActionHandlerRegistryImpl implements
    ClientActionHandlerRegistry {

  private final HashMap<Class<? extends Action<?>>, ClientActionHandler<?, ?>> clientActionHandlers;

  ClientActionHandlerRegistryImpl() {
    clientActionHandlers = new HashMap<Class<? extends Action<?>>, ClientActionHandler<?, ?>>();
  }

  @Override
  public <A extends Action<R>, R extends Result> void addClientActionHandler(
      Class<A> actionClass, ClientActionHandler<A, R> handler) {
    clientActionHandlers.put(actionClass, handler);
  }

  @Override
  public void clearActionInterceptors() {
    clientActionHandlers.clear();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <A extends Action<R>, R extends Result> ClientActionHandler<A, R> findActionInterceptor(
      Class<A> actionClass) {

    return (ClientActionHandler<A, R>) clientActionHandlers.get(actionClass);
  }

  @Override
  public <A extends Action<R>, R extends Result> void removeActionInterceptorClass(
      Class<A> actionClass) {

    ClientActionHandler<?, ?> actionInterceptorClass = clientActionHandlers.get(actionClass);
    if (actionInterceptorClass != null) {
      clientActionHandlers.remove(actionInterceptorClass);
    }
  }

}