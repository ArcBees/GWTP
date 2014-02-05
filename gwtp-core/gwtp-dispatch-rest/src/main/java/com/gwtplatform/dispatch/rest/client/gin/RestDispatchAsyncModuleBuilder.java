/**
 * Copyright 2014 ArcBees Inc.
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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.gwtplatform.dispatch.client.gin.AbstractDispatchAsyncModule;
import com.gwtplatform.dispatch.rest.client.serialization.JsonSerialization;
import com.gwtplatform.dispatch.rest.client.serialization.Serialization;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.RestParameter;

/**
 * A {@link RestDispatchAsyncModule} builder.
 * <p/>
 * The possible configurations are:
 * <ul>
 * <li>All configuration methods listed by {@link AbstractDispatchAsyncModule.Builder}</li>
 * <li>{@link #xsrfTokenHeaderName(String) XSRF Token Header Name}:
 * A {@link com.gwtplatform.dispatch.rest.client.XSRFHeaderName}.
 * The default value is {@link RestDispatchAsyncModule#DEFAULT_XSRF_NAME}.</li>
 * <li>{@link #serialization(Class) Serialization Implementation}: A {@link Serialization} implementation.
 * The default is {@link JsonSerialization}.</li>
 * <li>{@link #requestTimeout(int) Request timeout}: The number of milliseconds to wait for a request to complete.
 * The default value is 0 (no timeout).</li>
 * </ul>
 */
public class RestDispatchAsyncModuleBuilder extends AbstractDispatchAsyncModule.Builder {
    enum ParameterType {
        HEADER, QUERY
    }

    String xsrfTokenHeaderName = RestDispatchAsyncModule.DEFAULT_XSRF_NAME;
    Class<? extends Serialization> serializationClass = JsonSerialization.class;
    int requestTimeoutMs = 0;
    Multimap<HttpMethod, RestParameter> globalHeaderParams = HashMultimap.create();
    Multimap<HttpMethod, RestParameter> globalQueryParams = HashMultimap.create();

    /**
     * Specify the XSRF token header name.
     *
     * @deprecated See {@link #xsrfTokenHeaderName(String)}
     */
    @Deprecated
    public RestDispatchAsyncModuleBuilder xcsrfTokenHeaderName(String xsrfTokenHeaderName) {
        this.xsrfTokenHeaderName = xsrfTokenHeaderName;
        return this;
    }

    /**
     * Specify the XSRF token header name.
     *
     * @param xsrfTokenHeaderName The XSRF token header name.
     * @return this {@link RestDispatchAsyncModuleBuilder builder} object.
     */
    public RestDispatchAsyncModuleBuilder xsrfTokenHeaderName(String xsrfTokenHeaderName) {
        this.xsrfTokenHeaderName = xsrfTokenHeaderName;
        return this;
    }

    /**
     * Specify the serialization implementation to use.
     * Default is {@link JsonSerialization}.
     *
     * @param serializationClass The {@link Serialization} implementation to use.
     * @return this {@link RestDispatchAsyncModuleBuilder builder} object.
     */
    public RestDispatchAsyncModuleBuilder serialization(Class<? extends Serialization> serializationClass) {
        this.serializationClass = serializationClass;
        return this;
    }

    /**
     * Specify the number of milliseconds to wait for a request to complete. If the timeout is reached,
     * {@link com.google.gwt.user.client.rpc.AsyncCallback#onFailure(Throwable) AsyncCallback#onFailure(Throwable)}
     * will be called.
     * Default is <code>0</code>: no timeout.
     *
     * @param timeoutMs The maximum time to wait, in milliseconds, or {@code 0} for no timeout.
     * @return this {@link RestDispatchAsyncModuleBuilder builder} object.
     */
    public RestDispatchAsyncModuleBuilder requestTimeout(int timeoutMs) {
        this.requestTimeoutMs = timeoutMs;
        return this;
    }

    public RestParameterBuilder addGlobalHeaderParam(String key) {
        return new RestParameterBuilder(this, globalHeaderParams, key);
    }

    public RestParameterBuilder addGlobalQueryParam(String key) {
        return new RestParameterBuilder(this, globalQueryParams, key);
    }

    @Override
    public RestDispatchAsyncModule build() {
        return new RestDispatchAsyncModule(this);
    }
}
