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

import com.gwtplatform.dispatch.rest.client.RestDispatch;
import com.gwtplatform.dispatch.rest.client.core.parameters.DefaultHttpParameterFactory;
import com.gwtplatform.dispatch.rest.client.core.parameters.HttpParameterFactory;
import com.gwtplatform.dispatch.rest.client.gin.BaseRestDispatchModuleBuilder;

public class CoreModuleBuilder extends BaseRestDispatchModuleBuilder<CoreModuleBuilder> {
    private Class<? extends BodyFactory> bodyFactory = DefaultBodyFactory.class;
    private Class<? extends CookieManager> cookieManager = DefaultCookieManager.class;
    private Class<? extends DispatchCallFactory> dispatchCallFactory = DefaultDispatchCallFactory.class;
    private Class<? extends HeaderFactory> headerFactory = DefaultHeaderFactory.class;
    private Class<? extends HttpParameterFactory> httpParameterFactory = DefaultHttpParameterFactory.class;
    private Class<? extends RequestBuilderFactory> requestBuilderFactory = DefaultRequestBuilderFactory.class;
    private Class<? extends ResponseDeserializer> responseDeserializer = DefaultResponseDeserializer.class;
    private Class<? extends RestDispatch> restDispatch = DefaultRestDispatch.class;
    private Class<? extends UriFactory> uriFactory = DefaultUriFactory.class;

    public CoreModuleBuilder(BaseRestDispatchModuleBuilder<?> baseBuilder) {
        super(baseBuilder);
    }

    public CoreModuleBuilder bodyFactory(Class<? extends BodyFactory> bodyFactory) {
        this.bodyFactory = bodyFactory;
        return self();
    }

    public CoreModuleBuilder cookieManager(Class<? extends CookieManager> cookieManager) {
        this.cookieManager = cookieManager;
        return self();
    }

    public CoreModuleBuilder dispatchCallFactory(Class<? extends DispatchCallFactory> dispatchCallFactory) {
        this.dispatchCallFactory = dispatchCallFactory;
        return self();
    }

    public CoreModuleBuilder headerFactory(Class<? extends HeaderFactory> headerFactory) {
        this.headerFactory = headerFactory;
        return self();
    }

    public CoreModuleBuilder httpParameterFactory(Class<? extends HttpParameterFactory> httpParameterFactory) {
        this.httpParameterFactory = httpParameterFactory;
        return self();
    }

    public CoreModuleBuilder requestBuilderFactory(Class<? extends RequestBuilderFactory> requestBuilderFactory) {
        this.requestBuilderFactory = requestBuilderFactory;
        return self();
    }

    public CoreModuleBuilder responseDeserializer(Class<? extends ResponseDeserializer> responseDeserializer) {
        this.responseDeserializer = responseDeserializer;
        return self();
    }

    public CoreModuleBuilder restDispatch(Class<? extends RestDispatch> restDispatch) {
        this.restDispatch = restDispatch;
        return self();
    }

    public CoreModuleBuilder uriFactory(Class<? extends UriFactory> uriFactory) {
        this.uriFactory = uriFactory;
        return self();
    }

    @Override
    public CoreModule getCoreModule() {
        return new CoreModule(this);
    }

    @Override
    protected CoreModuleBuilder self() {
        return this;
    }

    Class<? extends BodyFactory> getBodyFactory() {
        return bodyFactory;
    }

    Class<? extends CookieManager> getCookieManager() {
        return cookieManager;
    }

    Class<? extends DispatchCallFactory> getDispatchCallFactory() {
        return dispatchCallFactory;
    }

    Class<? extends HeaderFactory> getHeaderFactory() {
        return headerFactory;
    }

    Class<? extends HttpParameterFactory> getHttpParameterFactory() {
        return httpParameterFactory;
    }

    Class<? extends RequestBuilderFactory> getRequestBuilderFactory() {
        return requestBuilderFactory;
    }

    Class<? extends ResponseDeserializer> getResponseDeserializer() {
        return responseDeserializer;
    }

    Class<? extends RestDispatch> getRestDispatch() {
        return restDispatch;
    }

    Class<? extends UriFactory> getUriFactory() {
        return uriFactory;
    }
}
