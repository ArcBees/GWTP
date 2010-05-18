package com.philbeaudoin.gwtp.dispatch.server.actionHandlerValidator;

import com.philbeaudoin.gwtp.dispatch.server.actionHandler.ActionHandler;
import com.philbeaudoin.gwtp.dispatch.server.actionValidator.ActionValidator;
import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

public class ActionHandlerValidatorClass<A extends Action<R>, R extends Result> {
  private final Class<? extends ActionValidator> actionValidatorClass;
  private final Class<? extends ActionHandler<A, R>> actionHandlerClass;
  
  public ActionHandlerValidatorClass(final Class<? extends ActionHandler<A, R>> handlerClass, final Class<? extends ActionValidator> actionValidatorClass) {
    this.actionHandlerClass = handlerClass;
    this.actionValidatorClass = actionValidatorClass;
  }

  public Class<? extends ActionValidator> getActionValidatorClass() {
    return actionValidatorClass;
  }

  public Class<? extends ActionHandler<A, R>> getActionHandlerClass() {
    return actionHandlerClass;
  }
}