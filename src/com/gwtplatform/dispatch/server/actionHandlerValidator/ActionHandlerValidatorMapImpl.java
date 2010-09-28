package com.gwtplatform.dispatch.server.actionHandlerValidator;

import com.gwtplatform.dispatch.server.actionHandler.ActionHandler;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

/**
 * Implementation of {@link ActionHandlerMap} that links {@link Action}s to {@link ActionHandler}s
 * 
 * @param <A> Type of {@link Action}
 * @param <R> Type of {@link Result}
 * @author David Paterson
 */
public class ActionHandlerValidatorMapImpl<A extends Action<R>, R extends Result> implements ActionHandlerValidatorMap<A, R> {

	private final Class<A> actionClass;
	private final ActionHandlerValidatorClass<A, R> actionHandlerValidatorClass;

	public ActionHandlerValidatorMapImpl(final Class<A> actionClass, final ActionHandlerValidatorClass<A, R> actionHandlerValidatorClass) {
		this.actionClass = actionClass;
		this.actionHandlerValidatorClass = actionHandlerValidatorClass;
	}

	@Override
	public Class<A> getActionClass() {
		return actionClass;
	}

	@Override
	public ActionHandlerValidatorClass<A, R> getActionHandlerValidatorClass() {
		return actionHandlerValidatorClass;
	}
}