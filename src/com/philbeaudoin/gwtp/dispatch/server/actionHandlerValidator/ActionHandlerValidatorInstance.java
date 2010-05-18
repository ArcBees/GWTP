package com.philbeaudoin.gwtp.dispatch.server.actionHandlerValidator;

import com.philbeaudoin.gwtp.dispatch.server.actionHandler.ActionHandler;
import com.philbeaudoin.gwtp.dispatch.server.actionValidator.ActionValidator;
import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

/**
 * Small convenience class that contain both an action handler and his validator.
 *
 * @param <A>
 *            The {@link Action} implementation.
 * @param <R>
 *            The {@link Result} implementation.
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