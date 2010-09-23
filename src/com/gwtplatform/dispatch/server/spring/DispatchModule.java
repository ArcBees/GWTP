package com.gwtplatform.dispatch.server.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.gwtplatform.dispatch.server.Dispatch;
import com.gwtplatform.dispatch.server.actionHandlerValidator.ActionHandlerValidatorRegistry;
import com.gwtplatform.dispatch.server.actionHandlerValidator.LazyActionHandlerValidatorRegistry;
import com.gwtplatform.dispatch.server.spring.actionHandlerValidator.ActionHandlerValidatorLinker;
import com.gwtplatform.dispatch.server.spring.actionHandlerValidator.LazyActionHandlerValidatorRegistryImpl;
import com.gwtplatform.dispatch.server.spring.utils.SpringUtils;

public class DispatchModule {

	private Class<? extends Dispatch> dispatchClass;
	private Class<? extends ActionHandlerValidatorRegistry> lazyActionHandlerValidatorRegistryClass;

	@Autowired
	private ApplicationContext context;

	public DispatchModule() {
		this(DispatchImpl.class, LazyActionHandlerValidatorRegistryImpl.class);
	}

	public DispatchModule(Class<? extends Dispatch> dispatchClass) {
		this(dispatchClass, LazyActionHandlerValidatorRegistryImpl.class);
	}

	public DispatchModule(Class<? extends Dispatch> dispatchClass, Class<? extends ActionHandlerValidatorRegistry> lazyActionHandlerValidatorRegistryClass) {
		this.dispatchClass = dispatchClass;
		this.lazyActionHandlerValidatorRegistryClass = lazyActionHandlerValidatorRegistryClass;
	}

	@Bean
	public ActionHandlerValidatorRegistry getActionHandlerValidatorRegistry() {
		ActionHandlerValidatorRegistry instance = SpringUtils.getOrCreate(context, lazyActionHandlerValidatorRegistryClass);

		//TODO check this out
		if (LazyActionHandlerValidatorRegistry.class.isAssignableFrom(lazyActionHandlerValidatorRegistryClass)) {
			ActionHandlerValidatorLinker.linkValidators(context, getActionHandlerValidatorRegistry());
		}

		return instance;
	}

	@Bean
	public Dispatch getDispatch() {
		Dispatch instance = SpringUtils.getOrCreate(context, dispatchClass);
		return instance;
	}

	protected final void configure() {
	}
}
