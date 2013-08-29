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
import com.gwtplatform.dispatch.rest.shared.RestDispatch;

/**
 * An implementation of {@link AbstractDispatchAsyncModule} that uses REST calls.
 * </p>
 * This gin module provides provides access to the {@link RestDispatch} singleton, which is used to make calls to the
 * server over HTTP. This module requires a {@link XCSRFHeaderName}. By default, this will be bound to
 * {@link RestDispatchAsyncModule#DEFAULT_X_CSRF_NAME}.
 */
public class RestDispatchAsyncModule extends AbstractDispatchAsyncModule {
    /**
     * A {@link RestDispatchAsyncModule} builder.
     * <p/>
     * By default, this builder configures the {@link RestDispatchAsyncModule} to use
     * {@link RestDispatchAsyncModule#DEFAULT_X_CSRF_NAME}.
     */
    public static class Builder extends AbstractDispatchAsyncModule.Builder {
        private String xcsrfTokenHeaderName = DEFAULT_X_CSRF_NAME;

        /**
         * Specify the X-CSRF header name.
         *
         * @param xcsrfTokenHeaderName The X-CSRF header name..
         * @return a {@link Builder} object.
         */
        public Builder xcsrfTokenHeaderName(String xcsrfTokenHeaderName) {
            this.xcsrfTokenHeaderName = xcsrfTokenHeaderName;
            return this;
        }

        @Override
        public RestDispatchAsyncModule build() {
            return new RestDispatchAsyncModule(this);
        }
    }

    public static final String DEFAULT_X_CSRF_NAME = "X-CSRF-Token";

    private final String xcsrfTokenHeaderName;

    public RestDispatchAsyncModule() {
        this(new Builder());
    }

    private RestDispatchAsyncModule(Builder builder) {
        super(builder);

        xcsrfTokenHeaderName = builder.xcsrfTokenHeaderName;
    }

    @Override
    protected void configureDispatch() {
        bindConstant().annotatedWith(XCSRFHeaderName.class).to(xcsrfTokenHeaderName);

        bind(ActionMetadataProvider.class).asEagerSingleton();
        bind(RestRequestBuilderFactory.class).in(Singleton.class);
        bind(RestResponseDeserializer.class).in(Singleton.class);

        bind(RestDispatch.class).to(RestDispatchAsync.class).in(Singleton.class);
    }
}
