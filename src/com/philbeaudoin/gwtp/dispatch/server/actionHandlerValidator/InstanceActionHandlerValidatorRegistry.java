package com.philbeaudoin.gwtp.dispatch.server.actionHandlerValidator;

import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

public interface InstanceActionHandlerValidatorRegistry extends ActionHandlerValidatorRegistry {
  /**
   * @param <A>
   *            Type of associated {@link Action}
   * @param <R>
   *            Type of associated {@link Result}
   * @param actionClass
   *            The {@link Action} class
   * @param actionValidator
   *            The {@link ActionHandlerValidator}
   */
  public <A extends Action<R>, R extends Result> void addActionHandlerValidator(Class<A> actionClass, ActionHandlerValidatorInstance actionHandlerValidatorInstance);

  /**
   * @param <A>
   *            Type of associated {@link Action}
   * @param <R>
   *            Type of associated {@link Result}
   * @param actionClass
   *            The {@link Action} class
   * @return <code>true</code> if the handler was previously registered and
   *         was successfully removed.
   */
  public <A extends Action<R>, R extends Result> boolean removeActionHandlerValidator(Class<A> actionClass);
}