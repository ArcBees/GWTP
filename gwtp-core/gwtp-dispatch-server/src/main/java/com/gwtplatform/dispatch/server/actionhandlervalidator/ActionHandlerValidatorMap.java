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

package com.gwtplatform.dispatch.server.actionhandlervalidator;

import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

/**
 * This is the interface that define the map of
 * {@link ActionHandlerValidatorInstance}.
 *
 * @param <A> Type of the associated {@link Action}
 * @param <R> Type of the associated {@link Result}
 *
 * @author Christian Goudreau
 */
public interface ActionHandlerValidatorMap<A extends Action<R>, R extends Result> {
  /**
   * @return the {@link Action} class associated
   */
  Class<A> getActionClass();

  /**
   * @return the {@link ActionHandlerValidatorClass} class associated
   */
  ActionHandlerValidatorClass<A, R> getActionHandlerValidatorClass();
}