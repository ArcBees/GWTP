package com.gwtplatform.dispatch.server;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.server.actionhandlervalidator.ActionHandlerValidatorClass;
import com.gwtplatform.dispatch.server.actionhandlervalidator.ActionHandlerValidatorMap;
import com.gwtplatform.dispatch.server.actionhandlervalidator.ActionHandlerValidatorMapImpl;
import com.gwtplatform.dispatch.server.actionhandlervalidator.ActionHandlerValidatorRegistry;
import com.gwtplatform.dispatch.server.actionvalidator.ActionValidator;
import com.gwtplatform.dispatch.server.guice.DispatchImpl;
import com.gwtplatform.dispatch.server.guice.actionhandlervalidator.ActionHandlerValidatorLinker;
import com.gwtplatform.dispatch.server.guice.actionhandlervalidator.LazyActionHandlerValidatorRegistryImpl;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

public class ServiceModule extends AbstractModule {
    private final Class<? extends ActionValidator> actionValidator;

    public ServiceModule(Class<? extends ActionValidator> actionValidator) {
        this.actionValidator = actionValidator;
    }

    @Override
    protected void configure() {
        bind(Dispatch.class).to(DispatchImpl.class);
        bind(ActionHandlerValidatorRegistry.class).to(
                LazyActionHandlerValidatorRegistryImpl.class).in(Singleton.class);

        bindHandler(SomeAction.class, HandlerThatThrowsActionException.class, actionValidator);
        requestStaticInjection(ActionHandlerValidatorLinker.class);
    }

    protected <A extends Action<R>, R extends Result> void bindHandler(
            Class<A> actionClass, Class<? extends ActionHandler<A, R>> handlerClass,
            Class<? extends ActionValidator> actionValidator) {

        bind(ActionHandlerValidatorMap.class).toInstance(
                new ActionHandlerValidatorMapImpl<A, R>(actionClass,
                        new ActionHandlerValidatorClass<A, R>(handlerClass, actionValidator)));
    }
}
