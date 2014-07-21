/**
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

package com.gwtplatform.dispatch.client.gin;

import javax.inject.Singleton;

import com.google.gwt.inject.client.AbstractGinModule;
import com.gwtplatform.dispatch.client.DefaultDispatchHooks;
import com.gwtplatform.dispatch.client.DefaultExceptionHandler;
import com.gwtplatform.dispatch.client.DefaultSecurityCookieAccessor;
import com.gwtplatform.dispatch.client.DispatchHooks;
import com.gwtplatform.dispatch.client.ExceptionHandler;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry;
import com.gwtplatform.dispatch.client.actionhandler.DefaultClientActionHandlerRegistry;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;

/**
 * This gin module provides provides access to the dispatcher singleton, which is used to make calls to the server. This
 * module requires an {@link ExceptionHandler}, a {@link DefaultClientActionHandlerRegistry} and a
 * {@link SecurityCookieAccessor}. By default, these will be bound to {@link DefaultExceptionHandler},
 * {@link DefaultClientActionHandlerRegistry} and {@link DefaultSecurityCookieAccessor} respectively.
 * <p/>
 * Install the module in one of your {@link #configure()} methods:
 * <p/>
 * <pre>
 * install(new RestDispatchAsyncModule.Builder()
 *                 .exceptionHandler(MyExceptionHandler.class)
 *                 .clientActionHandlerRegistry(MyClientActionHandlerRegistry.class)
 *                 .build());
 * </pre>
 *
 * @see com.gwtplatform.dispatch.rpc.client.gin.RpcDispatchAsyncModule
 * @see com.gwtplatform.dispatch.rest.client.gin.RestDispatchAsyncModule
 */
public abstract class AbstractDispatchAsyncModule extends AbstractGinModule {
    /**
     * A {@link AbstractDispatchAsyncModule} builder.
     * <p/>
     * By default, this builder configures the {@link AbstractDispatchAsyncModule} to use
     * {@link DefaultExceptionHandler}, {@link DefaultClientActionHandlerRegistry} and
     * {@link DefaultSecurityCookieAccessor}.
     *
     * @see com.gwtplatform.dispatch.rpc.client.gin.RpcDispatchAsyncModule.Builder
     * @see com.gwtplatform.dispatch.rest.client.gin.RestDispatchAsyncModule.Builder
     */
    public abstract static class Builder {
        private Class<? extends ExceptionHandler> exceptionHandlerType = DefaultExceptionHandler.class;
        private Class<? extends ClientActionHandlerRegistry> clientActionHandlerRegistryType =
                DefaultClientActionHandlerRegistry.class;
        private Class<? extends SecurityCookieAccessor> sessionAccessorType = DefaultSecurityCookieAccessor.class;
        private Class<? extends DispatchHooks> dispatchHooks = DefaultDispatchHooks.class;

        /**
         * Constructs {@link AbstractDispatchAsyncModule} builder.
         */
        public Builder() {
        }

        /**
         * Build the {@link AbstractDispatchAsyncModule}.
         *
         * @return The built {@link AbstractDispatchAsyncModule}.
         */
        public abstract AbstractDispatchAsyncModule build();

        /**
         * Specify an alternate client action handler registry.
         *
         * @param clientActionHandlerRegistryType A {@link ClientActionHandlerRegistry} class.
         * @return a {@link Builder} object.
         */
        public Builder clientActionHandlerRegistry(
                final Class<? extends ClientActionHandlerRegistry> clientActionHandlerRegistryType) {
            this.clientActionHandlerRegistryType = clientActionHandlerRegistryType;
            return this;
        }

        /**
         * Supply your own implementation of {@link com.gwtplatform.dispatch.client.DispatchHooks}.
         * Default is {@link com.gwtplatform.dispatch.client.DefaultDispatchHooks}
         *
         * @param dispatchHooks The {@link com.gwtplatform.dispatch.client.DispatchHooks} implementation.
         * @return this {@link Builder} object.
         */
        public Builder dispatchHooks(final Class<? extends DispatchHooks> dispatchHooks) {
            this.dispatchHooks = dispatchHooks;
            return this;
        }

        /**
         * Specify an alternative exception handler.
         *
         * @param exceptionHandlerType The {@link ExceptionHandler} class.
         * @return a {@link Builder} object.
         */
        public Builder exceptionHandler(final Class<? extends ExceptionHandler> exceptionHandlerType) {
            this.exceptionHandlerType = exceptionHandlerType;
            return this;
        }

        /**
         * Specify an alternate session accessor.
         *
         * @param sessionAccessorType The {@link SecurityCookieAccessor} class.
         * @return a {@link Builder} object.
         */
        public Builder sessionAccessor(final Class<? extends SecurityCookieAccessor> sessionAccessorType) {
            this.sessionAccessorType = sessionAccessorType;
            return this;
        }
    }

    private static Boolean alreadyBound = false;
    private static Class<? extends AbstractDispatchAsyncModule> boundType;

    private final Builder builder;

    protected AbstractDispatchAsyncModule(final Builder builder) {
        this.builder = builder;
    }

    @Override
    protected final void configure() {
        if (alreadyBound) {
            if (!boundType.equals(getClass())) {
                throw new RuntimeException("You are trying to use more than one DispatchAsync implementation. " +
                        boundType.getName() + " was already installed.");
            }
        } else {
            alreadyBound = true;
            boundType = getClass();

            bind(ClientActionHandlerRegistry.class).to(builder.clientActionHandlerRegistryType).asEagerSingleton();
            bind(ExceptionHandler.class).to(builder.exceptionHandlerType);
            bind(SecurityCookieAccessor.class).to(builder.sessionAccessorType);
            bind(DispatchHooks.class).to(builder.dispatchHooks).in(Singleton.class);

            configureDispatch();
        }
    }

    /**
     * Override this method to perform additional bindings in your implementation of
     * {@link AbstractDispatchAsyncModule}.
     */
    protected void configureDispatch() {
    }
}
