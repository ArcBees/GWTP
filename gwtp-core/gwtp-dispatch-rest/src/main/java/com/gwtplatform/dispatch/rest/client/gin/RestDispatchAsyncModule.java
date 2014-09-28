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

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.inject.Provides;
import com.gwtplatform.common.client.CommonGinModule;
import com.gwtplatform.dispatch.client.gin.AbstractDispatchAsyncModule;
import com.gwtplatform.dispatch.rest.client.DefaultDateFormat;
import com.gwtplatform.dispatch.rest.client.DefaultRestDispatchCallFactory;
import com.gwtplatform.dispatch.rest.client.DefaultRestRequestBuilderFactory;
import com.gwtplatform.dispatch.rest.client.DefaultRestResponseDeserializer;
import com.gwtplatform.dispatch.rest.client.GlobalHeaderParams;
import com.gwtplatform.dispatch.rest.client.GlobalQueryParams;
import com.gwtplatform.dispatch.rest.client.RequestTimeout;
import com.gwtplatform.dispatch.rest.client.RestBinding;
import com.gwtplatform.dispatch.rest.client.RestDispatchAsync;
import com.gwtplatform.dispatch.rest.client.RestDispatchCallFactory;
import com.gwtplatform.dispatch.rest.client.RestDispatchHooks;
import com.gwtplatform.dispatch.rest.client.RestRequestBuilderFactory;
import com.gwtplatform.dispatch.rest.client.RestResponseDeserializer;
import com.gwtplatform.dispatch.rest.client.XsrfHeaderName;
import com.gwtplatform.dispatch.rest.client.interceptor.RestInterceptorRegistry;
import com.gwtplatform.dispatch.rest.client.serialization.MultimapJsonSerializer;
import com.gwtplatform.dispatch.rest.client.serialization.Serialization;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.RestDispatch;
import com.gwtplatform.dispatch.rest.shared.RestParameter;

/**
 * An implementation of {@link AbstractDispatchAsyncModule} that uses REST calls.
 * </p>
 * This gin module provides provides access to the {@link RestDispatch} singleton, which is used to make calls to the
 * server over HTTP. This module requires:
 * <p/>
 * <b>You must</b> manually bind {@literal @}{@link com.gwtplatform.dispatch.rest.client.RestApplicationPath} to point
 * to your server API root path.
 */
public class RestDispatchAsyncModule extends AbstractDispatchAsyncModule {
    /**
     * {@inheritDoc}.
     */
    public static class Builder extends RestDispatchAsyncModuleBuilder {
    }

    public static final String DEFAULT_XSRF_NAME = "X-CSRF-Token";

    private final RestDispatchAsyncModuleBuilder builder;
    private final MultimapJsonSerializer multimapJsonSerializer = new MultimapJsonSerializer();

    /**
     * Creates this module using the default values as specified by {@link RestDispatchAsyncModuleBuilder}.
     */
    public RestDispatchAsyncModule() {
        this(new RestDispatchAsyncModuleBuilder());
    }

    RestDispatchAsyncModule(RestDispatchAsyncModuleBuilder builder) {
        super(builder, RestBinding.class);

        this.builder = builder;
    }

    @Override
    protected void configureDispatch() {
        // Common
        install(new CommonGinModule());

        // Constants / Configurations
        // It's not possible to bind non-native type constants, so we must encode them at compile-time and decode them
        // at runtime (ie: Global Parameters)
        bindConstant().annotatedWith(XsrfHeaderName.class).to(builder.getXsrfTokenHeaderName());
        bindConstant().annotatedWith(RequestTimeout.class).to(builder.getRequestTimeoutMs());
        bindConstant().annotatedWith(DefaultDateFormat.class).to(builder.getDefaultDateFormat());
        bindConstant().annotatedWith(GlobalHeaderParams.class)
                .to(multimapJsonSerializer.serialize(builder.getGlobalHeaderParams()));
        bindConstant().annotatedWith(GlobalQueryParams.class)
                .to(multimapJsonSerializer.serialize(builder.getGlobalQueryParams()));

        // Workflow
        bind(RestDispatchCallFactory.class).to(DefaultRestDispatchCallFactory.class).in(Singleton.class);
        bind(RestRequestBuilderFactory.class).to(DefaultRestRequestBuilderFactory.class).in(Singleton.class);
        bind(RestResponseDeserializer.class).to(DefaultRestResponseDeserializer.class).in(Singleton.class);

        // Serialization
        bind(Serialization.class).to(builder.getSerializationClass()).in(Singleton.class);

        // Entry Point
        bind(RestDispatch.class).to(RestDispatchAsync.class).in(Singleton.class);

        // Dispatch Hooks
        bind(RestDispatchHooks.class).to(builder.getDispatchHooks()).in(Singleton.class);

        // Interceptor Registry
        bind(RestInterceptorRegistry.class).to(builder.getInterceptorRegistry()).in(Singleton.class);
    }

    @Provides
    @Singleton
    @GlobalHeaderParams
    Multimap<HttpMethod, RestParameter> getGlobalHeaderParams(@GlobalHeaderParams final String encodedParams) {
        return decodeParameters(encodedParams);
    }

    @Provides
    @Singleton
    @GlobalQueryParams
    Multimap<HttpMethod, RestParameter> getGlobalQueryParams(@GlobalQueryParams final String encodedParams) {
        return decodeParameters(encodedParams);
    }

    private Multimap<HttpMethod, RestParameter> decodeParameters(final String encodedParameters) {
        final Multimap<HttpMethod, RestParameter> parameters = LinkedHashMultimap.create();

        final JSONObject json = JSONParser.parseStrict(encodedParameters).isObject();
        for (final String method : json.keySet()) {
            final HttpMethod httpMethod = HttpMethod.valueOf(method);
            final JSONArray jsonParameters = json.get(method).isArray();

            for (int i = 0; i < jsonParameters.size(); ++i) {
                final JSONObject jsonParameter = jsonParameters.get(i).isObject();
                final String key = jsonParameter.get("key").isString().stringValue();
                final String value = jsonParameter.get("value").isString().stringValue();
                final RestParameter parameter = new RestParameter(key, value);

                parameters.put(httpMethod, parameter);
            }
        }

        return parameters;
    }
}
