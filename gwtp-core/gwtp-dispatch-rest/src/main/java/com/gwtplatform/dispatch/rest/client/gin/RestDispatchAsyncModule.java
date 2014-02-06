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

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.inject.Provides;
import com.gwtplatform.common.client.CommonGinModule;
import com.gwtplatform.dispatch.client.gin.AbstractDispatchAsyncModule;
import com.gwtplatform.dispatch.rest.client.DefaultRestDispatchCallFactory;
import com.gwtplatform.dispatch.rest.client.DefaultRestRequestBuilderFactory;
import com.gwtplatform.dispatch.rest.client.DefaultRestResponseDeserializer;
import com.gwtplatform.dispatch.rest.client.GlobalHeaderParams;
import com.gwtplatform.dispatch.rest.client.GlobalQueryParams;
import com.gwtplatform.dispatch.rest.client.RequestTimeout;
import com.gwtplatform.dispatch.rest.client.RestDispatchAsync;
import com.gwtplatform.dispatch.rest.client.RestDispatchCallFactory;
import com.gwtplatform.dispatch.rest.client.RestRequestBuilderFactory;
import com.gwtplatform.dispatch.rest.client.RestResponseDeserializer;
import com.gwtplatform.dispatch.rest.client.XsrfHeaderName;
import com.gwtplatform.dispatch.rest.client.serialization.JsonSerialization;
import com.gwtplatform.dispatch.rest.client.serialization.Serialization;
import com.gwtplatform.dispatch.rest.shared.AsyncRestParameter;
import com.gwtplatform.dispatch.rest.shared.AsyncRestParameter.ValueProvider;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.rest.shared.RestDispatch;
import com.gwtplatform.dispatch.rest.shared.RestParameter;

import static com.google.inject.name.Names.named;

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
     * A {@link RestDispatchAsyncModule} builder.
     * <p/>
     * The possible configurations are:
     * <ul>
     * <li>A {@link com.gwtplatform.dispatch.rest.client.XsrfHeaderName}. The default value is
     * {@link RestDispatchAsyncModule#DEFAULT_XSRF_NAME}.</li>
     * <li>A {@link Serialization} implementation. The default is {@link JsonSerialization}.</li>
     * </ul>
     */
    public static class Builder extends RestDispatchAsyncModuleBuilder {
    }

    private static class StaticValueProvider implements ValueProvider {
        private final String value;

        StaticValueProvider(String value) {
            this.value = value;
        }

        @Override
        public String getValue(RestAction<?> action) {
            return value;
        }
    }

    public static final String DEFAULT_XSRF_NAME = "X-CSRF-Token";

    private static final String GLOBAL_HEADER_PARAMS = "GlobalHeaders";
    private static final String GLOBAL_QUERY_PARAMS = "GlobalQueries";

    private final RestDispatchAsyncModuleBuilder builder;

    /**
     * Creates this module using the default values as specified by {@link RestDispatchAsyncModuleBuilder}.
     */
    public RestDispatchAsyncModule() {
        this(new RestDispatchAsyncModuleBuilder());
    }

    RestDispatchAsyncModule(RestDispatchAsyncModuleBuilder builder) {
        super(builder);

        this.builder = builder;
    }

    @Override
    protected void configureDispatch() {
        // Common
        install(new CommonGinModule());

        // Constants / Configurations
        bindConstant().annotatedWith(XsrfHeaderName.class).to(builder.xsrfTokenHeaderName);
        bindConstant().annotatedWith(RequestTimeout.class).to(builder.requestTimeoutMs);
        bindConstant().annotatedWith(named(GLOBAL_HEADER_PARAMS)).to(encodeParameters(builder.globalHeaderParams));
        bindConstant().annotatedWith(named(GLOBAL_QUERY_PARAMS)).to(encodeParameters(builder.globalQueryParams));

        // Workflow
        bind(RestDispatchCallFactory.class).to(DefaultRestDispatchCallFactory.class).in(Singleton.class);
        bind(RestRequestBuilderFactory.class).to(DefaultRestRequestBuilderFactory.class).in(Singleton.class);
        bind(RestResponseDeserializer.class).to(DefaultRestResponseDeserializer.class).in(Singleton.class);

        // Serialization
        bind(Serialization.class).to(builder.serializationClass).in(Singleton.class);

        // Entry Point
        bind(RestDispatch.class).to(RestDispatchAsync.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    @GlobalHeaderParams
    Multimap<HttpMethod, AsyncRestParameter> getGlobalHeaderParams(@Named(GLOBAL_HEADER_PARAMS) String encodedParams) {
        return decodeParameters(encodedParams);
    }

    @Provides
    @Singleton
    @GlobalQueryParams
    Multimap<HttpMethod, AsyncRestParameter> getGlobalQueryParams(@Named(GLOBAL_QUERY_PARAMS) String encodedParams) {
        return decodeParameters(encodedParams);
    }

    private String encodeParameters(Multimap<HttpMethod, RestParameter> parameters) {
        return "{\"" + Joiner.on(",\"").withKeyValueSeparator("\":").join(parameters.asMap()) + "}";
    }

    private Multimap<HttpMethod, AsyncRestParameter> decodeParameters(String encodedParameters) {
        Multimap<HttpMethod, AsyncRestParameter> parameters = HashMultimap.create();

        JSONObject json = JSONParser.parseStrict(encodedParameters).isObject();
        for (String method : json.keySet()) {
            HttpMethod httpMethod = HttpMethod.valueOf(method);
            JSONArray jsonParameters = json.get(method).isArray();

            for (int i = 0; i < jsonParameters.size(); ++i) {
                JSONObject jsonParameter = jsonParameters.get(i).isObject();
                String key = jsonParameter.get("key").isString().stringValue();
                String value = jsonParameter.get("value").isString().stringValue();
                AsyncRestParameter parameter = new AsyncRestParameter(key, new StaticValueProvider(value));

                parameters.put(httpMethod, parameter);
            }
        }

        return parameters;
    }
}
