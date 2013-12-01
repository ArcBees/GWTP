/**
 * Copyright 2011 ArcBees Inc.
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

package com.gwtplatform.dispatch.rpc.client.gin;

import javax.inject.Singleton;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;
import com.gwtplatform.dispatch.rpc.client.DefaultRpcDispatchCallFactory;
import com.gwtplatform.dispatch.rpc.client.PhoneGapDispatchAsync;
import com.gwtplatform.dispatch.rpc.client.RpcDispatchCallFactory;
import com.gwtplatform.dispatch.rpc.shared.DispatchAsync;
import com.gwtplatform.dispatch.rpc.shared.DispatchService;
import com.gwtplatform.dispatch.rpc.shared.DispatchServiceAsync;

/**
 * This gin module provides provides access to a {@link DispatchAsync} singleton that will work when used in a Phone Gap
 * application.
 */
public class PhoneGapDispatchAsyncModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(RpcDispatchCallFactory.class).to(DefaultRpcDispatchCallFactory.class).in(Singleton.class);

        bind(DispatchAsync.class).to(PhoneGapDispatchAsync.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    DispatchServiceAsync provideDispatchServiceAsync(DispatchService dispatchService) {
        return (DispatchServiceAsync) dispatchService;
    }
}
