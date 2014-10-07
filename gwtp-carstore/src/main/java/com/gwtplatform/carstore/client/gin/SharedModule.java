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

package com.gwtplatform.carstore.client.gin;

import com.gwtplatform.carstore.client.dispatch.rest.AppRestDispatchHooks;
import com.gwtplatform.carstore.client.dispatch.rest.RestInterceptorRegistry;
import com.gwtplatform.carstore.client.dispatch.rpc.AppRpcDispatchHooks;
import com.gwtplatform.carstore.client.dispatch.rpc.RpcInterceptorRegistry;
import com.gwtplatform.carstore.client.place.NameTokens;
import com.gwtplatform.carstore.client.security.SecurityModule;
import com.gwtplatform.dispatch.rest.client.gin.RestDispatchAsyncModule;
import com.gwtplatform.dispatch.rpc.client.gin.RpcDispatchAsyncModule;
import com.gwtplatform.mvp.client.annotations.DefaultPlace;
import com.gwtplatform.mvp.client.annotations.ErrorPlace;
import com.gwtplatform.mvp.client.annotations.UnauthorizedPlace;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;

public class SharedModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        install(new DefaultModule());
        install(new SecurityModule());

        install(new RestDispatchAsyncModule.Builder()
            .dispatchHooks(AppRestDispatchHooks.class)
            .interceptorRegistry(RestInterceptorRegistry.class)
            .build());

        install(new RpcDispatchAsyncModule.Builder()
            .dispatchHooks(AppRpcDispatchHooks.class)
            .interceptorRegistry(RpcInterceptorRegistry.class)
            .build());

        // DefaultPlaceManager Places
        bindConstant().annotatedWith(DefaultPlace.class).to(NameTokens.LOGIN);
        bindConstant().annotatedWith(ErrorPlace.class).to(NameTokens.LOGIN);
        bindConstant().annotatedWith(UnauthorizedPlace.class).to(NameTokens.UNAUTHORIZED);

        // Load and inject CSS resources
        bind(ResourceLoader.class).asEagerSingleton();
    }
}
