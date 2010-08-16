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

import com.google.inject.Singleton;

import com.gwtplatform.dispatch.server.actionvalidatorr.ActionValidator;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a eager-loading implementation of the registry. It will create action
 * handlers and validators at startup. All {@link ActionHandler} and
 * {@link ActionValidator} implementations <b>must</b> have a public, default
 * constructor.
 * 
 * @author Christian Goudreau
 */
@Singleton
public class EagerActionHandlerValidatorRegistryImpl implements
    EagerActionHandlerValidatorRegistry {
  private final Map<Class<? extends Action<? extends Result>>, ActionHandlerValidatorInstance> actionHandlerValidatorInstances;
  private final Map<Class<? extends ActionValidator>, ActionValidator> validators;

  public EagerActionHandlerValidatorRegistryImpl() {
    actionHandlerValidatorInstances = new HashMap<Class<? extends Action<? extends Result>>, ActionHandlerValidatorInstance>();
    validators = new HashMap<Class<? extends ActionValidator>, ActionValidator>();
  }

  @Override
  public <A extends Action<R>, R extends Result> void addActionHandlerValidator(
      Class<A> actionClass,
      ActionHandlerValidatorInstance actionHandlerValidatorInstance) {
    actionHandlerValidatorInstances.put(actionClass,
        actionHandlerValidatorInstance);
    validators.put(
        actionHandlerValidatorInstance.getActionValidator().getClass(),
        actionHandlerValidatorInstance.getActionValidator());
  }

  @Override
  public void clearActionHandlerValidators() {
    actionHandlerValidatorInstances.clear();
    validators.clear();
  }

  @Override
  public <A extends Action<R>, R extends Result> ActionHandlerValidatorInstance findActionHandlerValidator(
      A action) {
    return actionHandlerValidatorInstances.get(action.getClass());
  }

  @Override
  public ActionValidator findActionValidator(
      Class<? extends ActionValidator> actionValidatorClass) {
    return validators.get(actionValidatorClass);
  }

  @Override
  public <A extends Action<R>, R extends Result> boolean removeActionHandlerValidator(
      Class<A> actionClass) {
    ActionHandlerValidatorInstance instance = actionHandlerValidatorInstances.remove(actionClass);

    if (instance != null) {
      if (!containValidator(instance.getActionValidator())) {
        return validators.remove(instance.getActionValidator().getClass()) != null;
      }
    } else {
      return false;
    }

    return actionHandlerValidatorInstances.remove(actionClass) != null;
  }

  private boolean containValidator(ActionValidator actionValidator) {
    for (ActionHandlerValidatorInstance validator : actionHandlerValidatorInstances.values()) {
      if (validator.getActionValidator().getClass().equals(
          actionValidator.getClass())) {
        return true;
      }
    }

    return false;
  }
}