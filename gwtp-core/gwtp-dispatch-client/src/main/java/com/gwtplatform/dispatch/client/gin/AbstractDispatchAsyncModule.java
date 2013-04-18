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

import com.google.gwt.inject.client.AbstractGinModule;
import com.gwtplatform.dispatch.client.DefaultExceptionHandler;
import com.gwtplatform.dispatch.client.DefaultSecurityCookieAccessor;
import com.gwtplatform.dispatch.client.ExceptionHandler;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry;
import com.gwtplatform.dispatch.client.actionhandler.DefaultClientActionHandlerRegistry;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;

/**
 * This gin module provides provides access to the {@link DispatchAsync}
 * singleton, which is used to make remote procedure calls to the server. This
 * module requires an {@link ExceptionHandler} and a
 * {@link SecurityCookieAccessor}. By default, these will be bound to
 * {@link DefaultExceptionHandler} and {@link DefaultSecurityCookieAccessor}
 * respectively.
 * <p/>
 * If you want to prevent XSRF attack (you use secured
 * {@link com.gwtplatform.dispatch.shared.Action}s) the default
 * {@link EmptySecurityCookieAccessor} could leave your application vulnerable
 * to XSRF attacks. For more details see <a href="http://code.google
 * .com/intl/fr/webtoolkit/articles/security_for_gwt_applications.html"
 * > this document</a>. For more security use {@link DispatchAsyncSecureModule}.
 * <p/>
 * If you don't need XSRF protection, you can use directly this module with your
 * {@link com.google.gwt.inject.client.Ginjector}, i.e.:
 * <p/>
 * <pre>
 * {@literal @}GinModules( { {@link AbstractDispatchAsyncModule}.class, ... }
 * </pre>
 * For customization, skip the previous step and install the module in one of
 * your {@link #configure()} methods:
 * <p/>
 * <pre>
 * install(new DispatchAsyncModule.Builder().exceptionHandler(
 *     MyExceptionHandler.class).sessionAccessor(
 *     MySecurityCookieAccessor.class).build());
 * </pre>
 * You can pass {@code null} as any of the two parameter to fallback to the
 * default.
 *
 * @author David Peterson
 * @author Philippe Beaudoin
 * @author Brendan Doherty
 */
public abstract class AbstractDispatchAsyncModule extends AbstractGinModule {
    private static final Object lock = new Object();
    private static Boolean alreadyBound = false;
    private static Class<? extends AbstractDispatchAsyncModule> boundType;

    protected final Class<? extends ExceptionHandler> exceptionHandlerType;
    protected final Class<? extends SecurityCookieAccessor> sessionAccessorType;
    protected final Class<? extends ClientActionHandlerRegistry> clientActionHandlerRegistryType;

    /**
    /**
     * A {@link AbstractDispatchAsyncModule} builder.
     * <p/>
     * By default, this builder configures the {@link AbstractDispatchAsyncModule} to use
     * {@link DefaultExceptionHandler}, {@link DefaultSecurityCookieAccessor}, and
     * {@link DefaultClientActionHandlerRegistry}.
     *
     * @author Brendan Doherty
     */
    public abstract static class Builder {
        protected Class<? extends ExceptionHandler> exceptionHandlerType = DefaultExceptionHandler.class;
        protected Class<? extends SecurityCookieAccessor> sessionAccessorType = DefaultSecurityCookieAccessor.class;
        protected Class<? extends ClientActionHandlerRegistry> clientActionHandlerRegistryType =
                DefaultClientActionHandlerRegistry.class;

        /**
         * Constructs {@link AbstractDispatchAsyncModule} builder.
         */
        public Builder() {
        }

        /**
         * Specify an alternative exception handler.
         *
         * @param exceptionHandlerType The {@link ExceptionHandler} class.
         * @return a {@link Builder} object.
         */
        public Builder exceptionHandler(Class<? extends ExceptionHandler> exceptionHandlerType) {
            this.exceptionHandlerType = exceptionHandlerType;
            return this;
        }

        /**
         * Specify an alternate session accessor.
         *
         * @param sessionAccessorType The {@link SecurityCookieAccessor} class.
         * @return a {@link Builder} object.
         */
        public Builder sessionAccessor(Class<? extends SecurityCookieAccessor> sessionAccessorType) {
            this.sessionAccessorType = sessionAccessorType;
            return this;
        }

        /**
         * Specify an alternate client action handler registry.
         *
         * @param clientActionHandlerRegistryType
         *         A {@link ClientActionHandlerRegistry} class.
         * @return a {@link Builder} object.
         */
        public Builder clientActionHandlerRegistry(
                Class<? extends ClientActionHandlerRegistry> clientActionHandlerRegistryType) {
            this.clientActionHandlerRegistryType = clientActionHandlerRegistryType;
            return this;
        }

        /**
         * Build the {@link AbstractDispatchAsyncModule}.
         *
         * @return The built {@link AbstractDispatchAsyncModule}.
         */
        public abstract AbstractDispatchAsyncModule build();
    }

    protected AbstractDispatchAsyncModule(Builder builder) {
        this.exceptionHandlerType = builder.exceptionHandlerType;
        this.sessionAccessorType = builder.sessionAccessorType;
        this.clientActionHandlerRegistryType = builder.clientActionHandlerRegistryType;
    }

    @Override
    protected void configure() {
        synchronized (lock) {
            if (alreadyBound) {
                if (!boundType.equals(getClass())) {
                    throw new RuntimeException("You are trying to use more than one DispatchAsync implementation. " +
                            boundType.getName() + " was already installed.");
                }
            } else {
                alreadyBound = true;
                boundType = getClass();
                bind(ExceptionHandler.class).to(exceptionHandlerType);
                bind(SecurityCookieAccessor.class).to(sessionAccessorType);
                bind(ClientActionHandlerRegistry.class).to(clientActionHandlerRegistryType).asEagerSingleton();
            }
        }
    }
}
