/*
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

import com.google.inject.Provides;
import com.gwtplatform.common.client.CommonGinModule;
import com.gwtplatform.dispatch.client.gin.AbstractDispatchAsyncModule;
import com.gwtplatform.dispatch.rest.client.RestDispatch;
import com.gwtplatform.dispatch.rest.client.annotations.DefaultDateFormat;
import com.gwtplatform.dispatch.rest.client.annotations.GlobalHeaderParams;
import com.gwtplatform.dispatch.rest.client.annotations.GlobalQueryParams;
import com.gwtplatform.dispatch.rest.client.annotations.RequestTimeout;
import com.gwtplatform.dispatch.rest.client.annotations.RestBinding;
import com.gwtplatform.dispatch.rest.client.annotations.XsrfHeaderName;
import com.gwtplatform.dispatch.rest.client.filter.RestFilterRegistry;
import com.gwtplatform.dispatch.rest.client.serialization.SerializationModule;

import jakarta.inject.Singleton;

/**
 * An implementation of {@link AbstractDispatchAsyncModule} that binds classes used by a restful dispatch.
 * <p>
 * This gin module provides access to the {@link RestDispatch} singleton, which is used to make calls to the server over
 * HTTP.
 * <p>
 * <b>You must</b> manually bind {@literal @}{@link com.gwtplatform.dispatch.rest.client.RestApplicationPath
 * RestApplicationPath} to point to your server API root path.
 */
public class RestDispatchAsyncModule extends AbstractDispatchAsyncModule {
    /**
     * {@inheritDoc}.
     */
    public static class Builder extends RestDispatchAsyncModuleBuilder {
    }

    public static final String DEFAULT_XSRF_NAME = "X-CSRF-Token";

    private final BaseRestDispatchModuleBuilder<?> builder;
    private final RestParameterBindingsSerializer bindingsSerializer = new RestParameterBindingsSerializer();

    /**
     * Creates this module using the default values as specified by {@link RestDispatchAsyncModuleBuilder}.
     */
    public RestDispatchAsyncModule() {
        this(new RestDispatchAsyncModuleBuilder());
    }

    RestDispatchAsyncModule(BaseRestDispatchModuleBuilder<?> builder) {
        super(builder, RestBinding.class);

        this.builder = builder;
    }

    @Override
    protected void configureDispatch() {
        // Common
        install(new CommonGinModule());
        install(new SerializationModule());
        install(builder.getCoreModule());

        bind(RestFilterRegistry.class).to(builder.getFilterRegistry()).in(Singleton.class);

        // Constants / Configurations
        // It's not possible to bind non-native type constants, so we must encode them at compile-time and decode them
        // at runtime (ie: Global Parameters)
        String globalHeaderParams = bindingsSerializer.serialize(builder.getGlobalHeaderParams());
        String globalQueryParams = bindingsSerializer.serialize(builder.getGlobalQueryParams());

        bindConstant().annotatedWith(XsrfHeaderName.class).to(builder.getXsrfTokenHeaderName());
        bindConstant().annotatedWith(RequestTimeout.class).to(builder.getRequestTimeoutMs());
        bindConstant().annotatedWith(DefaultDateFormat.class).to(builder.getDefaultDateFormat());
        bindConstant().annotatedWith(GlobalHeaderParams.class).to(globalHeaderParams);
        bindConstant().annotatedWith(GlobalQueryParams.class).to(globalQueryParams);
    }

    @Provides
    @Singleton
    @GlobalHeaderParams
    RestParameterBindings getGlobalHeaderParams(@GlobalHeaderParams String encodedParams) {
        return bindingsSerializer.deserialize(encodedParams);
    }

    @Provides
    @Singleton
    @GlobalQueryParams
    RestParameterBindings getGlobalQueryParams(@GlobalQueryParams String encodedParams) {
        return bindingsSerializer.deserialize(encodedParams);
    }
}
