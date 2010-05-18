package com.philbeaudoin.gwtp.dispatch.server.actionHandlerValidator;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

public class LazyActionHandlerValidatorRegistry implements ClassActionHandlerValidatorRegistry {
  private final Injector injector;
  private final Map<Class<? extends Action<?>>, ActionHandlerValidatorClass<? extends Action<?>, ? extends Result>> actionHandlerValidatorClasses;
  private final Map<Class<? extends Action<?>>, ActionHandlerValidatorInstance> actionHandlerValidatorInstances;
  
  @Inject
  LazyActionHandlerValidatorRegistry(Injector injector) {
    this.injector = injector;
    actionHandlerValidatorClasses = new HashMap<Class<? extends Action<?>>, ActionHandlerValidatorClass<? extends Action<?>,? extends Result>>();
    actionHandlerValidatorInstances = new HashMap<Class<? extends Action<?>>, ActionHandlerValidatorInstance>();
  }
  
  @Override
  public <A extends Action<R>, R extends Result> void addActionHandlerValidatorClass(
      Class<A> actionClass,
      ActionHandlerValidatorClass<A, R> actionHandlerValidatorClass) {
      actionHandlerValidatorClasses.put(actionClass, actionHandlerValidatorClass);
  }

  @Override
  public <A extends Action<R>, R extends Result> void removeActionHandlerValidatorClass(
      Class<A> actionClass,
      ActionHandlerValidatorClass<A, R> actionHandlerValidatorClass) {
      
    ActionHandlerValidatorClass<?, ?> oldActionHandlerValidatorClass = actionHandlerValidatorClasses.get(actionClass);
    
    if (oldActionHandlerValidatorClass == actionHandlerValidatorClass) {
      actionHandlerValidatorClasses.remove(actionClass);
      actionHandlerValidatorInstances.remove(actionClass);
    }
  }

  @Override
  public void clearActionHandlerValidators() {
    actionHandlerValidatorInstances.clear();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <A extends Action<R>, R extends Result> ActionHandlerValidatorInstance findActionHandlerValidator(
      A action) {
    
    ActionHandlerValidatorInstance actionHandlerValidatorInstance = actionHandlerValidatorInstances.get(action.getClass());

    if (actionHandlerValidatorInstance == null) {
      ActionHandlerValidatorClass<? extends Action<?>, ? extends Result> actionHandlerValidatorClass = actionHandlerValidatorClasses.get(action.getClass());
      if (actionHandlerValidatorClass != null) {
        actionHandlerValidatorInstance = createInstance(actionHandlerValidatorClass);
        if (actionHandlerValidatorInstance != null)
          actionHandlerValidatorInstances.put((Class<? extends Action<?>>) action.getClass(), actionHandlerValidatorInstance);
      }
    }

    return (ActionHandlerValidatorInstance) actionHandlerValidatorInstance;
  }

  private ActionHandlerValidatorInstance createInstance(
      ActionHandlerValidatorClass<? extends Action<?>, ? extends Result> actionHandlerValidatorClass) {
    
    ActionHandlerValidatorInstance actionHandlerValidatorInstance = new ActionHandlerValidatorInstance(
        injector.getInstance(actionHandlerValidatorClass.getActionValidatorClass()), 
        injector.getInstance(actionHandlerValidatorClass.getActionHandlerClass()));
    
    if (actionHandlerValidatorInstance.getActionHandler() == null || actionHandlerValidatorInstance.getActionValidator() == null) {
      return null;
    }
    
    return actionHandlerValidatorInstance;
  }
}