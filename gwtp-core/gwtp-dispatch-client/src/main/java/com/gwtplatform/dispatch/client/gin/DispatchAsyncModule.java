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

package com.gwtplatform.dispatch.client.gin;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.gwtplatform.dispatch.client.ExceptionHandler;
import com.gwtplatform.dispatch.client.RpcDispatchAsync;
import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;

/**
 * A default implementation of {@link AbstractDispatchAsyncModule} that uses GWT-RPC.
 */
public class DispatchAsyncModule extends AbstractDispatchAsyncModule {
    public static class Builder extends AbstractDispatchAsyncModule.Builder {
        @Override
        public DispatchAsyncModule build() {
            return new DispatchAsyncModule(this);
        }
    }

    public DispatchAsyncModule() {
        this(new Builder());
    }

    private DispatchAsyncModule(Builder builder) {
        super(builder);
    }

    @Provides
    @Singleton
    protected  DispatchAsync provideDispatchAsync(ExceptionHandler exceptionHandler,
            SecurityCookieAccessor secureSessionAccessor, ClientActionHandlerRegistry registry) {
        return new RpcDispatchAsync(exceptionHandler, secureSessionAccessor, registry);
    }
}
