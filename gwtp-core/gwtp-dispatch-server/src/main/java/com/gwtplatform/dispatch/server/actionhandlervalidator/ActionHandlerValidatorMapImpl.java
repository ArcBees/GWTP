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

package com.gwtplatform.dispatch.server.actionhandlervalidator;

import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

/**
 * Implementation of {@link ActionHandlerValidatorMap} that links {@link Action}s
 * to {@link ActionHandlerValidatorClass}es.
 *
 * @param <A> Type of {@link Action}
 * @param <R> Type of {@link Result}
 * @author David Paterson
 */
public class ActionHandlerValidatorMapImpl<A extends Action<R>, R extends Result> implements ActionHandlerValidatorMap<A, R> {

  private final Class<A> actionClass;
  private final ActionHandlerValidatorClass<A, R> actionHandlerValidatorClass;

  public ActionHandlerValidatorMapImpl(final Class<A> actionClass, final ActionHandlerValidatorClass<A, R> actionHandlerValidatorClass) {
    this.actionClass = actionClass;
    this.actionHandlerValidatorClass = actionHandlerValidatorClass;
  }

  @Override
  public Class<A> getActionClass() {
    return actionClass;
  }

  @Override
  public ActionHandlerValidatorClass<A, R> getActionHandlerValidatorClass() {
    return actionHandlerValidatorClass;
  }
}