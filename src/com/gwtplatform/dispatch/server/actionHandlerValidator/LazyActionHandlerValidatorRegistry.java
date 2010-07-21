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

package com.gwtplatform.dispatch.server.actionHandlerValidator;

import com.gwtplatform.dispatch.server.actionValidator.ActionValidator;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

public interface LazyActionHandlerValidatorRegistry extends
    ActionHandlerValidatorRegistry {
  /**
   * Registers the specified {@link ActionValidator} class with the registry.
   * 
   * @param <A>
   *          Type of associated {@link Action}
   * @param <R>
   *          Type of associated {@link Result}
   * @param actionClass
   *          The {@link Action} class
   * @param actionHandlerValidatorClass
   *          The {@link ActionHandlerValidatorClass}
   */
  public <A extends Action<R>, R extends Result> void addActionHandlerValidatorClass(
      Class<A> actionClass,
      ActionHandlerValidatorClass<A, R> actionHandlerValidatorClass);

  /**
   * Removes any registration of specified class, as well as any instances which
   * have been created.
   * 
   * @param <A>
   *          Type of associated {@link Action}
   * @param <R>
   *          Type of associated {@link Result}
   * @param actionClass
   *          The {@link Action} class
   * @param actionValidatorClass
   *          The {@link ActionValidator} class
   */
  public <A extends Action<R>, R extends Result> void removeActionHandlerValidatorClass(
      Class<A> actionClass,
      ActionHandlerValidatorClass<A, R> actionHandlerValidatorClass);
}