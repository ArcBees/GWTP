package com.gwtplatform.dispatch.server.actionHandlerValidator;

import java.util.Iterator;

import com.gwtplatform.dispatch.server.actionHandler.ActionHandler;
import com.gwtplatform.dispatch.server.actionHandlerValidator.ActionHandlerValidatorLinkerHelper.BeanProvider.BindingDescriptor;
import com.gwtplatform.dispatch.server.actionValidator.ActionValidator;

public class ActionHandlerValidatorLinkerHelper {

	public static interface BeanProvider {

		<B> B getInstance(Class<B> clazz);

		<B> Iterator<BindingDescriptor<B>> getBindings(Class<B> clazz);

		public static interface BindingDescriptor<B> {

			String getBeanName();

			B getBean();
		}
	}

	/**
	 * BingingDescriptor implementation for the Guice/Spring. This allows us to obtain bindings from guice injector/or
	 * from Spring application context
	 * 
	 * @author Peter Simun (simun@seges.sk)
	 */
	public static class CommonBindingDescriptor<B> implements BindingDescriptor<B> {

		private String name;
		private B bean;

		public CommonBindingDescriptor(B bean, String name) {
			this.name = name;
			this.bean = bean;
		}

		@Override
		public String getBeanName() {
			return name;
		}

		@Override
		public B getBean() {
			return bean;
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void linkValidators(BeanProvider beanProvider, ActionHandlerValidatorRegistry registry) {
		Iterator<BindingDescriptor<ActionHandlerValidatorMap>> bindings = beanProvider.getBindings(ActionHandlerValidatorMap.class);

		if (registry instanceof EagerActionHandlerValidatorRegistry) {
			EagerActionHandlerValidatorRegistry instanceRegistry = (EagerActionHandlerValidatorRegistry) registry;

			while (bindings.hasNext()) {
				BindingDescriptor<ActionHandlerValidatorMap> binding = bindings.next();

				Class<? extends ActionValidator> actionValidatorClass = binding.getBean().getActionHandlerValidatorClass().getActionValidatorClass();
				Class<? extends ActionHandler<?, ?>> handlerClass = binding.getBean().getActionHandlerValidatorClass().getActionHandlerClass();

				ActionHandlerValidatorInstance actionHandlerValidatorInstance = null;
				ActionValidator actionValidator = instanceRegistry.findActionValidator(actionValidatorClass);

				if (actionValidator == null) {
					actionValidator = beanProvider.getInstance(actionValidatorClass);
				}

				actionHandlerValidatorInstance = new ActionHandlerValidatorInstance(actionValidator, beanProvider.getInstance(handlerClass));

				instanceRegistry.addActionHandlerValidator(binding.getBean().getActionClass(), actionHandlerValidatorInstance);
			}
		} else if (registry instanceof LazyActionHandlerValidatorRegistry) {
			LazyActionHandlerValidatorRegistry classRegistry = (LazyActionHandlerValidatorRegistry) registry;

			while (bindings.hasNext()) {
				BindingDescriptor<ActionHandlerValidatorMap> binding = bindings.next();
				ActionHandlerValidatorMap map = binding.getBean();
				classRegistry.addActionHandlerValidatorClass(map.getActionClass(), map.getActionHandlerValidatorClass());
			}
		}
	}
}