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

import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

/**
 * Registry definition for {@link ClientActionHandler}.
 * 
 * @author Brendan Doherty
 */
public interface ClientActionHandlerRegistry {
  /**
   * Clears all registered {@link ClientActionHandler} from the registry.
   */
  void clearActionInterceptors();

  /**
   * Searches the registry and returns the first {@link ClientActionHandler}
   * wich supports the specified {@link Action} , or <code>null</code> if none
   * is available.
   * 
   * @param <A> Type of associated {@link Action}
   * @param <R> Type of associated {@link Result}
   * @param action The {@link Action}
   * @return The {@link ClientActionHandler}
   */
  <A extends Action<R>, R extends Result> ClientActionHandler<A, R> findActionInterceptor(
      Class<A> actionClass);

  /**
   * Registers the specified {@link ClientActionHandler} class with the
   * registry.
   * 
   * @param <A> Type of associated {@link Action}
   * @param <R> Type of associated {@link Result}
   * @param actionClass The {@link Action} class
   * @param handler The {@link ActionInterceptorClass}
   */
  <A extends Action<R>, R extends Result> void addClientActionHandler(
      Class<A> actionClass, ClientActionHandler<A, R> handler);

  /**
   * Removes any registration of specified class, as well as any instances which
   * have been created.
   * 
   * @param <A> Type of associated {@link Action}
   * @param <R> Type of associated {@link Result}
   * @param actionClass The {@link Action} class
   */
  <A extends Action<R>, R extends Result> void removeActionInterceptorClass(
      Class<A> actionClass);

}