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

package com.philbeaudoin.gwtp.dispatch.server.actionHandlerValidator;

import com.philbeaudoin.gwtp.dispatch.server.actionHandler.ActionHandler;
import com.philbeaudoin.gwtp.dispatch.server.actionValidator.ActionValidator;
import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

/**
 * Convenience class to store {@link ActionHandler} and
 * {@link ActionValidator} instance definitions.
 * 
 * @param <A>
 *          Type of associated {@link Action}
 * @param <R>
 *          Type of associated {@link Result}
 * 
 * @author Christian Goudreau
 */
public class ActionHandlerValidatorInstance {
  private final ActionValidator actionValidator;
  private final ActionHandler<? extends Action<?>, ? extends Result> actionHandler;
  
  public ActionHandlerValidatorInstance(final ActionValidator actionValidator, ActionHandler<?, ?> actionHandler) {
    this.actionHandler = actionHandler;
    this.actionValidator = actionValidator;
  }

  public ActionValidator getActionValidator() {
    return actionValidator;
  }

  public ActionHandler<? extends Action<?>, ? extends Result> getActionHandler() {
    return actionHandler;
  }
}