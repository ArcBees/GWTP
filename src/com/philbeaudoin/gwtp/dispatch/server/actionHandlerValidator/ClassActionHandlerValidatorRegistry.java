package com.philbeaudoin.gwtp.dispatch.server.actionHandlerValidator;

import com.philbeaudoin.gwtp.dispatch.server.actionValidator.ActionValidator;
import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

public interface ClassActionHandlerValidatorRegistry extends ActionHandlerValidatorRegistry {
  /**
   * Registers the specified {@link ActionValidator} class with the
   * registry.
   * 
   * @param <A>
   *            Type of associated {@link Action}
   * @param <R>
   *            Type of associated {@link Result}
   * @param actionClass
   *            The {@link Action} class
   * @param actionHandlerValidatorClass
   *            The {@link ActionHandlerValidatorClass}
   */
  public <A extends Action<R>, R extends Result> void addActionHandlerValidatorClass(Class<A> actionClass, ActionHandlerValidatorClass<A, R> actionHandlerValidatorClass);

  /**
   * Removes any registration of specified class, as well as any instances
   * which have been created.
   * 
   * @param <A>
   *            Type of associated {@link Action}
   * @param <R>
   *            Type of associated {@link Result}
   * @param actionClass
   *            The {@link Action} class
   * @param actionValidatorClass
   *            The {@link ActionValidator} class
   */
  public <A extends Action<R>, R extends Result> void removeActionHandlerValidatorClass(Class<A> actionClass, ActionHandlerValidatorClass<A, R> actionHandlerValidatorClass);
}