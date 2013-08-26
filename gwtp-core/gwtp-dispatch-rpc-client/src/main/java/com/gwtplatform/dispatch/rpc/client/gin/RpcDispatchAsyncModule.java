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

package com.gwtplatform.dispatch.rpc.client.gin;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.gwtplatform.dispatch.rpc.client.ExceptionHandler;
import com.gwtplatform.dispatch.rpc.client.RpcDispatchAsync;
import com.gwtplatform.dispatch.rpc.client.actionhandler.ClientActionHandlerRegistry;
import com.gwtplatform.dispatch.rpc.client.actionhandler.DefaultClientActionHandlerRegistry;
import com.gwtplatform.dispatch.rpc.shared.DispatchAsync;
import com.gwtplatform.dispatch.rpc.shared.SecurityCookieAccessor;

/**
 * A default implementation of {@link AbstractDispatchAsyncModule} that uses GWT-RPC.
 */
public class RpcDispatchAsyncModule extends AbstractDispatchAsyncModule {
    public static class Builder extends AbstractDispatchAsyncModule.Builder {
        private Class<? extends ClientActionHandlerRegistry> clientActionHandlerRegistryType =
                DefaultClientActionHandlerRegistry.class;

        /**
         * Specify an alternate client action handler registry.
         *
         * @param clientActionHandlerRegistryType A {@link ClientActionHandlerRegistry} class.
         * @return a {@link Builder} object.
         */
        public Builder clientActionHandlerRegistry(
                Class<? extends ClientActionHandlerRegistry> clientActionHandlerRegistryType) {
            this.clientActionHandlerRegistryType = clientActionHandlerRegistryType;
            return this;
        }

        @Override
        public RpcDispatchAsyncModule build() {
            return new RpcDispatchAsyncModule(this);
        }
    }

    private final Class<? extends ClientActionHandlerRegistry> clientActionHandlerRegistryType;

    public RpcDispatchAsyncModule() {
        this(new Builder());
    }

    private RpcDispatchAsyncModule(Builder builder) {
        super(builder);

        this.clientActionHandlerRegistryType = builder.clientActionHandlerRegistryType;
    }

    @Override
    protected void configure() {
        super.configure();

        bind(ClientActionHandlerRegistry.class).to(clientActionHandlerRegistryType).asEagerSingleton();
    }

    @Provides
    @Singleton
    protected DispatchAsync provideDispatchAsync(ExceptionHandler exceptionHandler,
                                                 SecurityCookieAccessor secureSessionAccessor,
                                                 ClientActionHandlerRegistry registry) {
        return new RpcDispatchAsync(exceptionHandler, secureSessionAccessor, registry);
    }
}
