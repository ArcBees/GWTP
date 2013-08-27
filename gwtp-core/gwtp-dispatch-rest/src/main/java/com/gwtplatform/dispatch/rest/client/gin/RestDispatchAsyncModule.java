/**
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.client.gin;

import javax.inject.Singleton;

import com.gwtplatform.dispatch.client.gin.AbstractDispatchAsyncModule;
import com.gwtplatform.dispatch.rest.client.ActionMetadataProvider;
import com.gwtplatform.dispatch.rest.client.RestDispatchAsync;
import com.gwtplatform.dispatch.rest.client.RestRequestBuilderFactory;
import com.gwtplatform.dispatch.rest.client.RestResponseDeserializer;
import com.gwtplatform.dispatch.rest.client.XCSRFHeaderName;
import com.gwtplatform.dispatch.rest.client.actionhandler.ClientRestActionHandlerRegistry;
import com.gwtplatform.dispatch.rest.client.actionhandler.DefaultClientRestActionHandlerRegistry;
import com.gwtplatform.dispatch.rest.shared.RestDispatch;

public class RestDispatchAsyncModule extends AbstractDispatchAsyncModule {
    public static class Builder extends AbstractDispatchAsyncModule.Builder {
        private String xcsrfTokenHeaderName = "X-CSRF-Token";
        private Class<? extends ClientRestActionHandlerRegistry> clientActionHandlerRegistryType =
                DefaultClientRestActionHandlerRegistry.class;

        /**
         * Specify an alternate client action handler registry.
         *
         * @param clientActionHandlerRegistryType
         *         A {@link ClientRestActionHandlerRegistry} class.
         * @return a {@link Builder} object.
         */
        public Builder clientActionHandlerRegistry(
                Class<? extends ClientRestActionHandlerRegistry> clientActionHandlerRegistryType) {
            this.clientActionHandlerRegistryType = clientActionHandlerRegistryType;
            return this;
        }

        public Builder xcsrfTokenHeaderName(String xcsrfTokenHeaderName) {
            this.xcsrfTokenHeaderName = xcsrfTokenHeaderName;
            return this;
        }

        @Override
        public RestDispatchAsyncModule build() {
            return new RestDispatchAsyncModule(this);
        }
    }

    private final String xcsrfTokenHeaderName;
    private final Class<? extends ClientRestActionHandlerRegistry> clientActionHandlerRegistryType;

    public RestDispatchAsyncModule() {
        this(new Builder());
    }

    private RestDispatchAsyncModule(Builder builder) {
        super(builder);

        clientActionHandlerRegistryType = builder.clientActionHandlerRegistryType;
        xcsrfTokenHeaderName = builder.xcsrfTokenHeaderName;
    }

    @Override
    protected void configure() {
        super.configure();

        bindConstant().annotatedWith(XCSRFHeaderName.class).to(xcsrfTokenHeaderName);

        bind(ClientRestActionHandlerRegistry.class).to(clientActionHandlerRegistryType).asEagerSingleton();
        bind(ActionMetadataProvider.class).asEagerSingleton();
        bind(RestRequestBuilderFactory.class).in(Singleton.class);
        bind(RestResponseDeserializer.class).in(Singleton.class);

        bind(RestDispatch.class).to(RestDispatchAsync.class).in(Singleton.class);
    }
}
