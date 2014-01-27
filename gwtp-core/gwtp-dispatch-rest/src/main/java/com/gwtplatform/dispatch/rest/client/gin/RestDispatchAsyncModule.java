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
import com.gwtplatform.dispatch.rest.client.DefaultRestDispatchCallFactory;
import com.gwtplatform.dispatch.rest.client.DefaultRestRequestBuilderFactory;
import com.gwtplatform.dispatch.rest.client.DefaultRestResponseDeserializer;
import com.gwtplatform.dispatch.rest.client.RestDispatchAsync;
import com.gwtplatform.dispatch.rest.client.RestDispatchCallFactory;
import com.gwtplatform.dispatch.rest.client.RestRequestBuilderFactory;
import com.gwtplatform.dispatch.rest.client.RestResponseDeserializer;
import com.gwtplatform.dispatch.rest.client.XCSRFHeaderName;
import com.gwtplatform.dispatch.rest.client.serialization.JsonSerialization;
import com.gwtplatform.dispatch.rest.client.serialization.Serialization;
import com.gwtplatform.dispatch.rest.shared.RestDispatch;

/**
 * An implementation of {@link AbstractDispatchAsyncModule} that uses REST calls.
 * </p>
 * This gin module provides provides access to the {@link RestDispatch} singleton, which is used to make calls to the
 * server over HTTP. This module requires:
 * <ul>
 * <li>A {@link XCSRFHeaderName}. The default value is {@link RestDispatchAsyncModule#DEFAULT_X_CSRF_NAME}.</li>
 * <li>A {@link Serialization} implementation. The default is {@link JsonSerialization}.</li>
 * </ul>
 * <p/>
 * <b>You must</b> manually bind {@literal @}{@link com.gwtplatform.dispatch.rest.client.RestApplicationPath} to point
 * to your server API root path.
 */
public class RestDispatchAsyncModule extends AbstractDispatchAsyncModule {
    /**
     * A {@link RestDispatchAsyncModule} builder.
     * <p/>
     * By default, this builder configures the {@link XCSRFHeaderName} value to
     * {@link RestDispatchAsyncModule#DEFAULT_X_CSRF_NAME}.
     */
    public static class Builder extends AbstractDispatchAsyncModule.Builder {
        private String xcsrfTokenHeaderName = DEFAULT_X_CSRF_NAME;
        private Class<? extends Serialization> serializationClass = JsonSerialization.class;

        /**
         * Specify the X-CSRF header name.
         *
         * @param xcsrfTokenHeaderName The X-CSRF header name.
         * @return a {@link Builder} object.
         */
        public Builder xcsrfTokenHeaderName(String xcsrfTokenHeaderName) {
            this.xcsrfTokenHeaderName = xcsrfTokenHeaderName;
            return this;
        }

        /**
         * Specify the serialization implementation to use.
         * Default is {@link JsonSerialization}.
         *
         * @param serializationClass The {@link Serialization} implementation to use.
         * @return a {@link Builder} object.
         */
        public Builder serialization(Class<? extends Serialization> serializationClass) {
            this.serializationClass = serializationClass;
            return this;
        }

        @Override
        public RestDispatchAsyncModule build() {
            return new RestDispatchAsyncModule(this);
        }
    }

    public static final String DEFAULT_X_CSRF_NAME = "X-CSRF-Token";

    private final String xcsrfTokenHeaderName;
    private final Class<? extends Serialization> serializationClass;

    /**
     * Creates this module using the default values as specified by {@link Builder}.
     */
    public RestDispatchAsyncModule() {
        this(new Builder());
    }

    private RestDispatchAsyncModule(Builder builder) {
        super(builder);

        xcsrfTokenHeaderName = builder.xcsrfTokenHeaderName;
        serializationClass = builder.serializationClass;
    }

    @Override
    protected void configureDispatch() {
        bindConstant().annotatedWith(XCSRFHeaderName.class).to(xcsrfTokenHeaderName);

        bind(Serialization.class).to(serializationClass);

        bind(RestDispatchCallFactory.class).to(DefaultRestDispatchCallFactory.class).in(Singleton.class);
        bind(RestRequestBuilderFactory.class).to(DefaultRestRequestBuilderFactory.class).in(Singleton.class);
        bind(RestResponseDeserializer.class).to(DefaultRestResponseDeserializer.class).in(Singleton.class);

        bind(RestDispatch.class).to(RestDispatchAsync.class).in(Singleton.class);
    }
}
