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

import java.util.HashMap;

import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

/**
 * The default implementation that {@link ClientActionHandlerRegistry} that if
 * bound will not any client-side action handlers. </p> To register client-side
 * action handlers, extend this class and pass one or more handlers in the
 * constructor.
 * 
 * @author Brendan Doherty
 */
public class DefaultClientActionHandlerRegistry implements
    ClientActionHandlerRegistry {

  private HashMap<Class<? extends Action<?>>, ClientActionHandler<?, ?>> clientActionHandlers;

  DefaultClientActionHandlerRegistry() {
  }

  protected DefaultClientActionHandlerRegistry(
      ClientActionHandler<?, ?>... handlers) {
    clientActionHandlers = new HashMap<Class<? extends Action<?>>, ClientActionHandler<?, ?>>();

    for (ClientActionHandler<?, ?> handler : handlers) {
      clientActionHandlers.put(handler.getActionType(), handler);
    }
  }

  @SuppressWarnings("unchecked")
  public <A extends Action<R>, R extends Result> ClientActionHandler<A, R> find(
      Class<A> actionClass) {

    if (clientActionHandlers == null) {
      return null;
    } else {
      return (ClientActionHandler<A, R>) clientActionHandlers.get(actionClass);
    }
  }
}