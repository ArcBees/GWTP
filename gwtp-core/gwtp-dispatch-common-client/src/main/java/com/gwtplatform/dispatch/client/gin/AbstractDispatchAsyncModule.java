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

import java.lang.annotation.Annotation;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.binder.GinAnnotatedBindingBuilder;
import com.google.gwt.inject.client.binder.GinLinkedBindingBuilder;
import com.gwtplatform.dispatch.client.DefaultExceptionHandler;
import com.gwtplatform.dispatch.client.DefaultSecurityCookieAccessor;
import com.gwtplatform.dispatch.client.ExceptionHandler;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry;
import com.gwtplatform.dispatch.client.actionhandler.DefaultClientActionHandlerRegistry;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;

/**
 * This gin module provides provides access to the dispatcher singleton, which is used to make calls to the server.
 * This module requires an {@link ExceptionHandler}, a {@link DefaultClientActionHandlerRegistry} and a
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
        protected Class<? extends ExceptionHandler> exceptionHandlerType = DefaultExceptionHandler.class;
        protected Class<? extends ClientActionHandlerRegistry> clientActionHandlerRegistryType =
                DefaultClientActionHandlerRegistry.class;
        protected Class<? extends SecurityCookieAccessor> sessionAccessorType = DefaultSecurityCookieAccessor.class;

        /**
         * Constructs {@link AbstractDispatchAsyncModule} builder.
         */
        public Builder() {
        }

        /**
         * Specify an alternative exception handler.
         *
         * @param exceptionHandlerType The {@link ExceptionHandler} class.
         *
         * @return a {@link Builder} object.
         */
        public Builder exceptionHandler(Class<? extends ExceptionHandler> exceptionHandlerType) {
            this.exceptionHandlerType = exceptionHandlerType;
            return this;
        }

        /**
         * Specify an alternate client action handler registry.
         *
         * @param clientActionHandlerRegistryType A {@link ClientActionHandlerRegistry} class.
         *
         * @return a {@link Builder} object.
         */
        public Builder clientActionHandlerRegistry(
                Class<? extends ClientActionHandlerRegistry> clientActionHandlerRegistryType) {
            this.clientActionHandlerRegistryType = clientActionHandlerRegistryType;
            return this;
        }

        /**
         * Specify an alternate session accessor.
         *
         * @param sessionAccessorType The {@link SecurityCookieAccessor} class.
         *
         * @return a {@link Builder} object.
         */
        public Builder sessionAccessor(Class<? extends SecurityCookieAccessor> sessionAccessorType) {
            this.sessionAccessorType = sessionAccessorType;
            return this;
        }

        /**
         * Build the {@link AbstractDispatchAsyncModule}.
         *
         * @return The built {@link AbstractDispatchAsyncModule}.
         */
        public abstract AbstractDispatchAsyncModule build();
    }

    private final Builder builder;
    private final Class<? extends Annotation> annotationClass;

    protected AbstractDispatchAsyncModule(
            Builder builder,
            Class<? extends Annotation> annotationClass) {
        this.builder = builder;
        this.annotationClass = annotationClass;
    }

    @Override
    protected final void configure() {
        bindAnnotated(ClientActionHandlerRegistry.class).to(builder.clientActionHandlerRegistryType).asEagerSingleton();
        bindAnnotated(ExceptionHandler.class).to(builder.exceptionHandlerType);
        bindAnnotated(SecurityCookieAccessor.class).to(builder.sessionAccessorType);

        configureDispatch();
    }

    /**
     * Override this method to perform additional bindings in your implementation of
     * {@link AbstractDispatchAsyncModule}.
     */
    protected void configureDispatch() {
    }

    private <T> GinLinkedBindingBuilder<T> bindAnnotated(Class<T> clazz) {
        GinAnnotatedBindingBuilder<T> binding = bind(clazz);

        if (annotationClass != null) {
            return binding.annotatedWith(annotationClass);
        }

        return binding;
    }
}
