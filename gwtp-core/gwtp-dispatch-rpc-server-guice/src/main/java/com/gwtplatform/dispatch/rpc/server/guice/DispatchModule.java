/*
 * Copyright 2011 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.dispatch.rpc.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.gwtplatform.dispatch.rpc.server.Dispatch;
import com.gwtplatform.dispatch.rpc.server.RequestProvider;
import com.gwtplatform.dispatch.rpc.server.actionhandlervalidator.ActionHandlerValidatorRegistry;
import com.gwtplatform.dispatch.rpc.server.actionhandlervalidator.LazyActionHandlerValidatorRegistry;
import com.gwtplatform.dispatch.rpc.server.guice.actionhandlervalidator.ActionHandlerValidatorLinker;
import com.gwtplatform.dispatch.rpc.server.guice.actionhandlervalidator.LazyActionHandlerValidatorRegistryImpl;
import com.gwtplatform.dispatch.rpc.server.guice.request.DefaultRequestProvider;

/**
 * This module will configure the implementation for the {@link Dispatch} and {@link ActionHandlerValidatorRegistry}
 * interfaces. Also every
 * {@link com.gwtplatform.dispatch.rpc.server.actionhandler.ActionHandler ActionHandler} and
 * {@link com.gwtplatform.dispatch.rpc.server.actionvalidator.ActionValidator ActionValidator} will be loaded lazily.
 * <p>
 * If you want to override the defaults ({@link DispatchImpl}, {@link LazyActionHandlerValidatorRegistryImpl} pass
 * the override values into the constructor for this module and ensure it is installed <b>before</b> any
 * {@link HandlerModule} instances.
 */
public class DispatchModule extends AbstractModule {
    private final Class<? extends Dispatch> dispatchClass;
    private final Class<? extends ActionHandlerValidatorRegistry> actionHandlerValidatorRegistryClass;
    private final Class<? extends RequestProvider> requestProviderClass;

    /**
     * A DispatchModule builder.
     */
    public static class Builder {
        private Class<? extends Dispatch> dispatchClass = DispatchImpl.class;
        private Class<? extends ActionHandlerValidatorRegistry> actionHandlerValidatorRegistryClass =
                LazyActionHandlerValidatorRegistryImpl.class;
        private Class<? extends RequestProvider> requestProviderClass = DefaultRequestProvider.class;

        public Builder() {
        }

        public Builder dispatch(Class<? extends Dispatch> dispatchClass) {
            this.dispatchClass = dispatchClass;
            return this;
        }

        public Builder actionHandlerValidatorRegistry(
                Class<? extends ActionHandlerValidatorRegistry> actionHandlerValidatorRegistryClass) {
            this.actionHandlerValidatorRegistryClass = actionHandlerValidatorRegistryClass;
            return this;
        }

        public Builder requestProvider(
                Class<? extends RequestProvider> requestProviderClass) {
            this.requestProviderClass = requestProviderClass;
            return this;
        }

        public DispatchModule build() {
            return new DispatchModule(this);
        }
    }

    public DispatchModule() {
        this(new Builder());
    }

    private DispatchModule(Builder builder) {
        this.dispatchClass = builder.dispatchClass;
        this.actionHandlerValidatorRegistryClass = builder.actionHandlerValidatorRegistryClass;
        this.requestProviderClass = builder.requestProviderClass;
    }

    /**
     * Override so that only one instance of this class will ever be installed in an {@link com.google.inject.Injector}.
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof DispatchModule;
    }

    /**
     * Override so that only one instance of this class will ever be installed in an {@link com.google.inject.Injector}.
     */
    @Override
    public int hashCode() {
        return DispatchModule.class.hashCode();
    }

    @Override
    protected final void configure() {
        bind(ActionHandlerValidatorRegistry.class).to(
                actionHandlerValidatorRegistryClass).in(Singleton.class);
        bind(Dispatch.class).to(dispatchClass).in(Singleton.class);
        bind(RequestProvider.class).to(requestProviderClass).in(Singleton.class);

        // This will bind registered validators and handlers to the registry lazily.
        if (LazyActionHandlerValidatorRegistry.class.isAssignableFrom(actionHandlerValidatorRegistryClass)) {
            requestStaticInjection(ActionHandlerValidatorLinker.class);
        }
    }

}
