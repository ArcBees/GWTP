package com.gwtplatform.dispatch.server.spring.actionHandlerValidator;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.gwtplatform.dispatch.server.actionHandler.ActionHandler;
import com.gwtplatform.dispatch.server.actionHandlerValidator.ActionHandlerValidatorClass;
import com.gwtplatform.dispatch.server.actionHandlerValidator.ActionHandlerValidatorInstance;
import com.gwtplatform.dispatch.server.actionHandlerValidator.LazyActionHandlerValidatorRegistry;
import com.gwtplatform.dispatch.server.actionValidator.ActionValidator;
import com.gwtplatform.dispatch.server.spring.utils.SpringUtils;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

@Component
public class LazyActionHandlerValidatorRegistryImpl implements LazyActionHandlerValidatorRegistry, ApplicationContextAware {

	private ApplicationContext applicationContext;

	private final Map<Class<? extends Action<?>>, ActionHandlerValidatorClass<? extends Action<?>, ? extends Result>> actionHandlerValidatorClasses;
	private final Map<Class<? extends Action<?>>, ActionHandlerValidatorInstance> actionHandlerValidatorInstances;
	private final Map<Class<? extends ActionValidator>, ActionValidator> validators;

	public LazyActionHandlerValidatorRegistryImpl() {
		actionHandlerValidatorClasses = new HashMap<Class<? extends Action<?>>, ActionHandlerValidatorClass<? extends Action<?>, ? extends Result>>();
		actionHandlerValidatorInstances = new HashMap<Class<? extends Action<?>>, ActionHandlerValidatorInstance>();
		validators = new HashMap<Class<? extends ActionValidator>, ActionValidator>();
	}

	@Override
	public <A extends Action<R>, R extends Result> void addActionHandlerValidatorClass(Class<A> actionClass,
			ActionHandlerValidatorClass<A, R> actionHandlerValidatorClass) {
		actionHandlerValidatorClasses.put(actionClass, actionHandlerValidatorClass);
	}

	@Override
	public void clearActionHandlerValidators() {
		actionHandlerValidatorInstances.clear();
		validators.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <A extends Action<R>, R extends Result> ActionHandlerValidatorInstance findActionHandlerValidator(A action) {

		ActionHandlerValidatorInstance actionHandlerValidatorInstance = actionHandlerValidatorInstances.get(action.getClass());

		if (actionHandlerValidatorInstance == null) {
			ActionHandlerValidatorClass<? extends Action<?>, ? extends Result> actionHandlerValidatorClass = actionHandlerValidatorClasses.get(action
					.getClass());
			if (actionHandlerValidatorClass != null) {
				actionHandlerValidatorInstance = createInstance(actionHandlerValidatorClass);
				if (actionHandlerValidatorInstance != null) {
					actionHandlerValidatorInstances.put((Class<? extends Action<?>>) action.getClass(), actionHandlerValidatorInstance);
				}
			}
		}

		return actionHandlerValidatorInstance;
	}

	@Override
	public ActionValidator findActionValidator(Class<? extends ActionValidator> actionValidatorClass) {
		return validators.get(actionValidatorClass);
	}

	@Override
	public <A extends Action<R>, R extends Result> void removeActionHandlerValidatorClass(Class<A> actionClass,
			ActionHandlerValidatorClass<A, R> actionHandlerValidatorClass) {

		ActionHandlerValidatorClass<?, ?> oldActionHandlerValidatorClass = actionHandlerValidatorClasses.get(actionClass);

		if (oldActionHandlerValidatorClass == actionHandlerValidatorClass) {
			actionHandlerValidatorClasses.remove(actionClass);
			ActionHandlerValidatorInstance instance = actionHandlerValidatorInstances.remove(actionClass);

			if (!containValidator(instance.getActionValidator())) {
				validators.remove(instance.getActionValidator().getClass());
			}
		}
	}

	private boolean containValidator(ActionValidator actionValidator) {
		for (ActionHandlerValidatorInstance validator : actionHandlerValidatorInstances.values()) {
			if (validator.getActionValidator().getClass().equals(actionValidator.getClass())) {
				return true;
			}
		}

		return false;
	}

	private ActionHandlerValidatorInstance createInstance(ActionHandlerValidatorClass<? extends Action<?>, ? extends Result> actionHandlerValidatorClass) {

		ActionHandlerValidatorInstance actionHandlerValidatorInstance = null;
		ActionValidator actionValidator = findActionValidator(actionHandlerValidatorClass.getActionValidatorClass());

		ActionHandler<?, ?> actionHandler = SpringUtils.getInstance(applicationContext, actionHandlerValidatorClass.getActionHandlerClass());

		if (actionValidator == null) {
			actionValidator = SpringUtils.getInstance(applicationContext, actionHandlerValidatorClass.getActionValidatorClass());
			actionHandlerValidatorInstance = new ActionHandlerValidatorInstance(actionValidator, actionHandler);

			validators.put(actionValidator.getClass(), actionValidator);
		} else {
			actionHandlerValidatorInstance = new ActionHandlerValidatorInstance(actionValidator, actionHandler);
		}

		if (actionHandlerValidatorInstance.getActionHandler() == null || actionHandlerValidatorInstance.getActionValidator() == null) {
			return null;
		}

		return actionHandlerValidatorInstance;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
