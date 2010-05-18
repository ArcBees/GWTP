package com.philbeaudoin.gwtp.dispatch.server.actionHandlerValidator;

import java.util.HashMap;
import java.util.Map;

import com.philbeaudoin.gwtp.dispatch.server.actionValidator.ActionValidator;
import com.philbeaudoin.gwtp.dispatch.server.actionValidator.InstanceActionValidatorRegistry;
import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

public class DefaultActionHandlerValidatorRegistry implements InstanceActionHandlerValidatorRegistry {
  private final Map<Class<? extends Action<? extends Result>>, ActionHandlerValidatorInstance> actionHandlerValidatorInstances;
  private final Map<Class<? extends ActionValidator>, ActionValidator> validators;
  
  public DefaultActionHandlerValidatorRegistry() {
    actionHandlerValidatorInstances = new HashMap<Class<? extends Action<? extends Result>>, ActionHandlerValidatorInstance>();
    validators = new HashMap<Class<? extends ActionValidator>, ActionValidator>();
  }

  @Override
  public <A extends Action<R>, R extends Result> void addActionHandlerValidator(
      Class<A> actionClass,
      ActionHandlerValidatorInstance actionHandlerValidatorInstance) {
    actionHandlerValidatorInstances.put(actionClass, actionHandlerValidatorInstance);
  }

  @Override
  public <A extends Action<R>, R extends Result> boolean removeActionHandlerValidator(
      Class<A> actionClass) {
    return actionHandlerValidatorInstances.remove(actionClass) != null;
  }

  @Override
  public void clearActionHandlerValidators() {
    actionHandlerValidatorInstances.clear();
  }

  @Override
  public <A extends Action<R>, R extends Result> ActionHandlerValidatorInstance findActionHandlerValidator(
      A action) {
    return actionHandlerValidatorInstances.get(action.getClass());
  }
}