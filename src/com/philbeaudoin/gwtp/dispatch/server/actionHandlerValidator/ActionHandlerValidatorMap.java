package com.philbeaudoin.gwtp.dispatch.server.actionHandlerValidator;

import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

/**
 * This is the interface that define the map of {@link ActionHandlerValidatorInstance}.
 * 
 * @param <A>
 *            Type of the associated {@link Action}
 * @param <R>
 *            Type of the associated {@link Result}
 * 
 * @author Christian Goudreau
 */
public interface ActionHandlerValidatorMap<A extends Action<R>, R extends Result> {
  /**
   * @return the {@link Action} class associated
   */
  public Class<A> getActionClass();
  
  /**
   * @return the {@link ActionHandlerValidatorClass} class associated
   */
  public ActionHandlerValidatorClass<A, R> getActionHandlerValidatorClass();
}