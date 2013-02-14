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

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.gwtplatform.dispatch.client.ExceptionHandler;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry;
import com.gwtplatform.dispatch.client.rest.RestApplicationPath;
import com.gwtplatform.dispatch.client.rest.RestDispatchAsync;
import com.gwtplatform.dispatch.client.rest.SerializerProvider;
import com.gwtplatform.dispatch.client.rest.XCSRFHeaderName;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;

/**
 * An implementation of {@link AbstractDispatchAsyncModule} that uses HTTP REST calls.
 * <p/>
 * Warning: This is still a work in progress and subject to many changes.
 */
public class RestDispatchAsyncModule extends AbstractDispatchAsyncModule {
    public static class Builder extends AbstractDispatchAsyncModule.Builder {
        protected String applicationPath = "";
        protected String xcsrfTokenHeaderName = "X-CSRF-Token";

        public Builder() {
        }

        public Builder applicationPath(String applicationPath) {
            this.applicationPath = applicationPath;
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

    private String applicationPath;
    private String xcsrfTokenHeaderName;

    public RestDispatchAsyncModule() {
        this(new Builder());
    }

    private RestDispatchAsyncModule(Builder builder) {
        super(builder);

        applicationPath = builder.applicationPath;
        xcsrfTokenHeaderName = builder.xcsrfTokenHeaderName;
    }

    @Override
    protected void configure() {
        super.configure();
        bindConstant().annotatedWith(RestApplicationPath.class).to(applicationPath);
        bindConstant().annotatedWith(XCSRFHeaderName.class).to(xcsrfTokenHeaderName);
        bind(SerializerProvider.class).asEagerSingleton();
    }

    @Provides
    @Singleton
    protected DispatchAsync provideDispatchAsync(SerializerProvider serializerProvider,
            ExceptionHandler exceptionHandler,
            ClientActionHandlerRegistry clientActionHandlerRegistry,
            SecurityCookieAccessor securityCookieAccessor,
            @RestApplicationPath String applicationPath,
            @XCSRFHeaderName String headerName) {

        return new RestDispatchAsync(exceptionHandler, securityCookieAccessor, clientActionHandlerRegistry,
                serializerProvider, applicationPath, headerName);
    }
}
