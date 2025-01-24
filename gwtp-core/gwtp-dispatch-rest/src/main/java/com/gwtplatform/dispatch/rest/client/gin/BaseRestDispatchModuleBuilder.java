/*
 * Copyright 2015 ArcBees Inc.
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

import com.gwtplatform.dispatch.client.gin.AbstractDispatchAsyncModule.Builder;
import com.gwtplatform.dispatch.rest.client.core.CoreModule;
import com.gwtplatform.dispatch.rest.client.filter.DefaultRestFilterRegistry;
import com.gwtplatform.dispatch.rest.client.filter.RestFilterRegistry;
import com.gwtplatform.dispatch.rest.shared.DateFormat;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;

/**
 * A {@link RestDispatchAsyncModule} builder. Use it to configure constant parameters such as the default date format,
 * the default timeout and more.
 */
public abstract class BaseRestDispatchModuleBuilder<B extends BaseRestDispatchModuleBuilder<B>> extends Builder<B> {
    private String xsrfTokenHeaderName = RestDispatchAsyncModule.DEFAULT_XSRF_NAME;
    private int requestTimeoutMs;
    private String defaultDateFormat = DateFormat.DEFAULT;
    private RestParameterBindings globalHeaderParams = new RestParameterBindings();
    private RestParameterBindings globalQueryParams = new RestParameterBindings();
    private Class<? extends RestFilterRegistry> filterRegistry = DefaultRestFilterRegistry.class;

    protected BaseRestDispatchModuleBuilder() {
    }

    protected BaseRestDispatchModuleBuilder(BaseRestDispatchModuleBuilder<?> copy) {
        xsrfTokenHeaderName = copy.xsrfTokenHeaderName;
        requestTimeoutMs = copy.requestTimeoutMs;
        defaultDateFormat = copy.defaultDateFormat;
        globalHeaderParams = copy.globalHeaderParams;
        globalQueryParams = copy.globalQueryParams;
        filterRegistry = copy.filterRegistry;
    }

    @Override
    public RestDispatchAsyncModule build() {
        return new RestDispatchAsyncModule(this);
    }

    /**
     * Initiate the creation of a global header parameter that will be attached to all requests.
     *
     * @param key The key used for this parameter
     *
     * @return the parameter builder instance
     */
    public RestParameterBuilder<B> addGlobalHeaderParam(String key) {
        return new RestParameterBuilder<>(self(), Type.HEADER, globalHeaderParams, key);
    }

    /**
     * Initiate the creation of a global query parameter that will be attached to all requests.
     *
     * @param key The key used for this parameter
     *
     * @return the parameter builder instance
     */
    public RestParameterBuilder<B> addGlobalQueryParam(String key) {
        return new RestParameterBuilder<>(self(), Type.QUERY, globalQueryParams, key);
    }

    /**
     * Specify the pattern to use to format dates before they are sent to the end-point. The pattern must follow the
     * rules defined by {@link com.google.gwt.i18n.shared.DateTimeFormat DateTimeFormat}.
     * <p>
     * Default is {@link com.gwtplatform.dispatch.rest.shared.DateFormat#DEFAULT}.
     *
     * @param defaultDateFormat The pattern used to format dates.
     *
     * @return this {@link com.gwtplatform.dispatch.rest.client.gin.BaseRestDispatchModuleBuilder builder} object.
     */
    public B defaultDateFormat(String defaultDateFormat) {
        this.defaultDateFormat = defaultDateFormat;
        return self();
    }

    /**
     * Specify the number of milliseconds to wait for a request to complete. If the timeout is reached, {@link
     * com.google.gwt.user.client.rpc.AsyncCallback#onFailure(Throwable) AsyncCallback#onFailure(Throwable)} will be
     * called. Default is <code>0</code>: no timeout.
     *
     * @param timeoutMs The maximum time to wait, in milliseconds, or {@code 0} for no timeout.
     *
     * @return this {@link com.gwtplatform.dispatch.rest.client.gin.BaseRestDispatchModuleBuilder builder} object.
     */
    public B requestTimeout(int timeoutMs) {
        this.requestTimeoutMs = timeoutMs;
        return self();
    }

    /**
     * Specify the XSRF token header name.
     *
     * @param xsrfTokenHeaderName The XSRF token header name.
     *
     * @return this {@link com.gwtplatform.dispatch.rest.client.gin.BaseRestDispatchModuleBuilder builder} object.
     */
    public B xsrfTokenHeaderName(String xsrfTokenHeaderName) {
        this.xsrfTokenHeaderName = xsrfTokenHeaderName;
        return self();
    }

    /**
     * Specify an alternate REST filter registry.
     *
     * @param filterRegistry A {@link com.gwtplatform.dispatch.rest.client.filter.RestFilterRegistry} class.
     *
     * @return this {@link com.gwtplatform.dispatch.rest.client.gin.BaseRestDispatchModuleBuilder builder} object.
     */
    public B filterRegistry(Class<? extends RestFilterRegistry> filterRegistry) {
        this.filterRegistry = filterRegistry;
        return self();
    }

    public abstract CoreModule getCoreModule();

    String getDefaultDateFormat() {
        return defaultDateFormat;
    }

    RestParameterBindings getGlobalHeaderParams() {
        return globalHeaderParams;
    }

    RestParameterBindings getGlobalQueryParams() {
        return globalQueryParams;
    }

    int getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    String getXsrfTokenHeaderName() {
        return xsrfTokenHeaderName;
    }

    Class<? extends RestFilterRegistry> getFilterRegistry() {
        return filterRegistry;
    }
}
