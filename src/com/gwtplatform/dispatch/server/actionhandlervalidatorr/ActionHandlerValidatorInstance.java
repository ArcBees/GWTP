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

package com.gwtplatform.dispatch.server.actionhandlervalidatorr;

import com.gwtplatform.dispatch.server.actionhandlerr.ActionHandler;
import com.gwtplatform.dispatch.server.actionvalidatorr.ActionValidator;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

/**
 * Convenience class to store {@link ActionHandler} and {@link ActionValidator}
 * instance definitions.
 * 
 * @author Christian Goudreau
 */
public class ActionHandlerValidatorInstance {
  private final ActionHandler<? extends Action<?>, ? extends Result> actionHandler;
  private final ActionValidator actionValidator;

  public ActionHandlerValidatorInstance(final ActionValidator actionValidator,
      ActionHandler<?, ?> actionHandler) {
    this.actionHandler = actionHandler;
    this.actionValidator = actionValidator;
  }

  public ActionHandler<? extends Action<?>, ? extends Result> getActionHandler() {
    return actionHandler;
  }

  public ActionValidator getActionValidator() {
    return actionValidator;
  }
}