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

package com.gwtplatform.dispatch.client.gin;

import java.lang.annotation.Annotation;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.binder.GinAnnotatedBindingBuilder;
import com.google.gwt.inject.client.binder.GinLinkedBindingBuilder;
import com.gwtplatform.dispatch.client.DefaultExceptionHandler;
import com.gwtplatform.dispatch.client.DefaultSecurityCookieAccessor;
import com.gwtplatform.dispatch.client.ExceptionHandler;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;

/**
 * This gin module provides provides access to the dispatcher singleton, which is used to make calls to the server. This
 * module requires an {@link ExceptionHandler} and a {@link SecurityCookieAccessor}. By default, these will be bound to
 * {@link DefaultExceptionHandler}, {@link DefaultSecurityCookieAccessor} respectively.
 * <p>
 * Install the module in one of your {@link #configure()} methods:
 * <p>
 * <pre>
 * install(new RestDispatchAsyncModule.Builder()
 *                 .exceptionHandler(MyExceptionHandler.class)
 *                 .sessionAccessor(MySessionAccessor.class)
 *                 .build());
 * </pre>
 */
public abstract class AbstractDispatchAsyncModule extends AbstractGinModule {
    /**
     * A {@link AbstractDispatchAsyncModule} builder.
     * <p>
     * By default, this builder configures the {@link AbstractDispatchAsyncModule} to use {@link
     * DefaultExceptionHandler} and {@link DefaultSecurityCookieAccessor}.
     */
    public abstract static class Builder<B extends Builder<B>> {
        private Class<? extends ExceptionHandler> exceptionHandlerType = DefaultExceptionHandler.class;
        private Class<? extends SecurityCookieAccessor> sessionAccessorType = DefaultSecurityCookieAccessor.class;

        /**
         * Constructs {@link AbstractDispatchAsyncModule} builder.
         */
        protected Builder() {
        }

        /**
         * Build the {@link AbstractDispatchAsyncModule}.
         *
         * @return The built {@link AbstractDispatchAsyncModule}.
         */
        public abstract AbstractDispatchAsyncModule build();

        /**
         * Specify an alternative exception handler.
         *
         * @param exceptionHandlerType The {@link ExceptionHandler} class.
         *
         * @return a {@link Builder} object.
         */
        public B exceptionHandler(Class<? extends ExceptionHandler> exceptionHandlerType) {
            this.exceptionHandlerType = exceptionHandlerType;
            return self();
        }

        /**
         * Specify an alternate session accessor.
         *
         * @param sessionAccessorType The {@link SecurityCookieAccessor} class.
         *
         * @return a {@link Builder} object.
         */
        public B sessionAccessor(Class<? extends SecurityCookieAccessor> sessionAccessorType) {
            this.sessionAccessorType = sessionAccessorType;
            return self();
        }

        protected abstract B self();
    }

    private final Builder<?> builder;
    private final Class<? extends Annotation> annotationClass;

    protected AbstractDispatchAsyncModule(
            Builder<?> builder,
            Class<? extends Annotation> annotationClass) {
        this.builder = builder;
        this.annotationClass = annotationClass;
    }

    @Override
    protected final void configure() {
        bindAnnotated(ExceptionHandler.class).to(builder.exceptionHandlerType);
        bindAnnotated(SecurityCookieAccessor.class).to(builder.sessionAccessorType);

        configureDispatch();
    }

    /**
     * Override this method to perform additional bindings in your implementation of {@link
     * AbstractDispatchAsyncModule}.
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
