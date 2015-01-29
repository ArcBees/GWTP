/**
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

import javax.inject.Singleton;

import com.google.gwt.inject.client.AbstractGinModule;
import com.gwtplatform.dispatch.rest.client.RestDispatch;
import com.gwtplatform.dispatch.rest.client.core.parameters.DefaultHttpParameterFactory;
import com.gwtplatform.dispatch.rest.client.core.parameters.HttpParameterFactory;

public class CoreModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(BodyFactory.class).to(DefaultBodyFactory.class).in(Singleton.class);
        bind(CookieManager.class).to(DefaultCookieManager.class).in(Singleton.class);
        bind(HeaderFactory.class).to(DefaultHeaderFactory.class).in(Singleton.class);
        bind(UriFactory.class).to(DefaultUriFactory.class).in(Singleton.class);
        bind(DispatchCallFactory.class).to(DefaultDispatchCallFactory.class).in(Singleton.class);
        bind(RequestBuilderFactory.class).to(DefaultRequestBuilderFactory.class).in(Singleton.class);
        bind(ResponseDeserializer.class).to(DefaultResponseDeserializer.class).in(Singleton.class);
        bind(HttpParameterFactory.class).to(DefaultHttpParameterFactory.class).in(Singleton.class);
        bind(RestDispatch.class).to(RestDispatchAsync.class).in(Singleton.class);
    }
}
