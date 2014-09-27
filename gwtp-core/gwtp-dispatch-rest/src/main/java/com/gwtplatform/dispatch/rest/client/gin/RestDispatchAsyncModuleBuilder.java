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

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.gwtplatform.dispatch.client.gin.AbstractDispatchAsyncModule;
import com.gwtplatform.dispatch.rest.client.DateFormat;
import com.gwtplatform.dispatch.rest.client.DefaultRestDispatchHooks;
import com.gwtplatform.dispatch.rest.client.RestDispatchHooks;
import com.gwtplatform.dispatch.rest.client.interceptor.DefaultRestInterceptorRegistry;
import com.gwtplatform.dispatch.rest.client.interceptor.RestInterceptorRegistry;
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
 * A {@link com.gwtplatform.dispatch.rest.client.XsrfHeaderName}.
 * The default value is {@link RestDispatchAsyncModule#DEFAULT_XSRF_NAME}.</li>
 * <li>{@link #serialization(Class) Serialization Implementation}: A {@link Serialization} implementation.
 * The default is {@link JsonSerialization}.</li>
 * <li>{@link #requestTimeout(int) Request timeout}: The number of milliseconds to wait for a request to complete.
 * The default value is 0 (no timeout).</li>
 * </ul>
 */
public class RestDispatchAsyncModuleBuilder extends AbstractDispatchAsyncModule.Builder {
    private String xsrfTokenHeaderName = RestDispatchAsyncModule.DEFAULT_XSRF_NAME;
    private Class<? extends Serialization> serializationClass = JsonSerialization.class;
    private int requestTimeoutMs;
    private String defaultDateFormat = DateFormat.DEFAULT;
    private Multimap<HttpMethod, RestParameter> globalHeaderParams = LinkedHashMultimap.create();
    private Multimap<HttpMethod, RestParameter> globalQueryParams = LinkedHashMultimap.create();
    private Class<? extends RestDispatchHooks> dispatchHooks = DefaultRestDispatchHooks.class;
    private Class<? extends RestInterceptorRegistry> interceptorRegistry = DefaultRestInterceptorRegistry.class;

    /**
     * Initiate the creation of a global header parameter that will be attached to all requests.
     *
     * @param key The key used for this parameter
     * @return the parameter builder instance
     */
    public RestParameterBuilder addGlobalHeaderParam(final String key) {
        return new RestParameterBuilder(this, globalHeaderParams, key);
    }

    /**
     * Initiate the creation of a global query parameter that will be attached to all requests.
     *
     * @param key The key used for this parameter
     * @return the parameter builder instance
     */
    public RestParameterBuilder addGlobalQueryParam(final String key) {
        return new RestParameterBuilder(this, globalQueryParams, key);
    }

    @Override
    public RestDispatchAsyncModule build() {
        return new RestDispatchAsyncModule(this);
    }

    /**
     * Specify the pattern to use to format dates before they are sent to the end-point. The pattern must follow the
     * rules defined by {@link com.google.gwt.i18n.shared.DateTimeFormat DateTimeFormat}.
     * <p/>
     * Default is {@link DateFormat#DEFAULT}.
     *
     * @param defaultDateFormat The pattern used to format dates.
     * @return this {@link RestDispatchAsyncModuleBuilder builder} object.
     */
    public RestDispatchAsyncModuleBuilder defaultDateFormat(final String defaultDateFormat) {
        this.defaultDateFormat = defaultDateFormat;
        return this;
    }

    public String getDefaultDateFormat() {
        return defaultDateFormat;
    }

    public Multimap<HttpMethod, RestParameter> getGlobalHeaderParams() {
        return globalHeaderParams;
    }

    public Multimap<HttpMethod, RestParameter> getGlobalQueryParams() {
        return globalQueryParams;
    }

    public int getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    public Class<? extends Serialization> getSerializationClass() {
        return serializationClass;
    }

    public String getXsrfTokenHeaderName() {
        return xsrfTokenHeaderName;
    }

    public Class<? extends RestDispatchHooks> getDispatchHooks() {
        return dispatchHooks;
    }

    public Class<? extends RestInterceptorRegistry> getInterceptorRegistry() {
        return interceptorRegistry;
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
    public RestDispatchAsyncModuleBuilder requestTimeout(final int timeoutMs) {
        this.requestTimeoutMs = timeoutMs;
        return this;
    }

    /**
     * Specify the serialization implementation to use.
     * Default is {@link JsonSerialization}.
     *
     * @param serializationClass The {@link Serialization} implementation to use.
     * @return this {@link RestDispatchAsyncModuleBuilder builder} object.
     */
    public RestDispatchAsyncModuleBuilder serialization(final Class<? extends Serialization> serializationClass) {
        this.serializationClass = serializationClass;
        return this;
    }

    /**
     * Specify the XSRF token header name.
     *
     * @deprecated See {@link #xsrfTokenHeaderName(String)}
     */
    @Deprecated
    public RestDispatchAsyncModuleBuilder xcsrfTokenHeaderName(final String xsrfTokenHeaderName) {
        this.xsrfTokenHeaderName = xsrfTokenHeaderName;
        return this;
    }

    /**
     * Specify the XSRF token header name.
     *
     * @param xsrfTokenHeaderName The XSRF token header name.
     * @return this {@link RestDispatchAsyncModuleBuilder builder} object.
     */
    public RestDispatchAsyncModuleBuilder xsrfTokenHeaderName(final String xsrfTokenHeaderName) {
        this.xsrfTokenHeaderName = xsrfTokenHeaderName;
        return this;
    }

    /**
     * Supply your own implementation of {@link com.gwtplatform.dispatch.rest.client.RestDispatchHooks}.
     * Default is {@link com.gwtplatform.dispatch.rest.client.DefaultRestDispatchHooks}
     *
     * @param dispatchHooks The {@link com.gwtplatform.dispatch.rest.client.RestDispatchHooks} implementation.
     * @return this {@link RestDispatchAsyncModuleBuilder} object.
     */
    public RestDispatchAsyncModuleBuilder dispatchHooks(final Class<? extends RestDispatchHooks> dispatchHooks) {
        this.dispatchHooks = dispatchHooks;
        return this;
    }

    /**
     * Specify an alternate REST interceptor registry.
     *
     * @param interceptorRegistry A {@link RestInterceptorRegistry} class.
     *
     * @return this {@link RestDispatchAsyncModuleBuilder builder} object.
     */
    public RestDispatchAsyncModuleBuilder interceptorRegistry(
            final Class<? extends RestInterceptorRegistry> interceptorRegistry) {
        this.interceptorRegistry = interceptorRegistry;
        return this;
    }

}
