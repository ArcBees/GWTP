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

package com.gwtplatform.dispatch.rest.client.core;

import com.google.gwt.inject.client.AbstractGinModule;
import com.gwtplatform.dispatch.rest.client.RestDispatch;
import com.gwtplatform.dispatch.rest.client.core.parameters.HttpParameterFactory;

import jakarta.inject.Singleton;

public class CoreModule extends AbstractGinModule {
    private final CoreModuleBuilder builder;

    CoreModule(CoreModuleBuilder builder) {
        this.builder = builder;
    }

    @Override
    protected void configure() {
        bind(BodyFactory.class).to(builder.getBodyFactory()).in(Singleton.class);
        bind(CookieManager.class).to(builder.getCookieManager()).in(Singleton.class);
        bind(HeaderFactory.class).to(builder.getHeaderFactory()).in(Singleton.class);
        bind(UriFactory.class).to(builder.getUriFactory()).in(Singleton.class);
        bind(DispatchCallFactory.class).to(builder.getDispatchCallFactory()).in(Singleton.class);
        bind(RequestBuilderFactory.class).to(builder.getRequestBuilderFactory()).in(Singleton.class);
        bind(ResponseDeserializer.class).to(builder.getResponseDeserializer()).in(Singleton.class);
        bind(HttpParameterFactory.class).to(builder.getHttpParameterFactory()).in(Singleton.class);
        bind(RestDispatch.class).to(builder.getRestDispatch()).in(Singleton.class);

        requestStaticInjection(StaticParametersFactory.class);
    }
}
