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
import com.gwtplatform.dispatch.client.rest.ApplicationPath;
import com.gwtplatform.dispatch.client.rest.RestDispatchAsync;
import com.gwtplatform.dispatch.shared.DispatchAsync;

/**
 * An implementation of {@link AbstractDispatchAsyncModule} that uses HTTP REST calls.
 * <p/>
 * Warning: This is still a work in progress and subject to many changes.
 */
public class RestDispatchAsyncModule extends AbstractDispatchAsyncModule {
    public static class Builder extends AbstractDispatchAsyncModule.Builder {
        protected String applicationPath = "";

        public Builder applicationPath(String applicationPath) {
            this.applicationPath = applicationPath;
            return this;
        }

        @Override
        public RestDispatchAsyncModule build() {
            return new RestDispatchAsyncModule(this);
        }
    }

    private String applicationPath;

    public RestDispatchAsyncModule() {
        this(new Builder());
    }

    private RestDispatchAsyncModule(Builder builder) {
        super(builder);

        applicationPath = builder.applicationPath;
    }

    @Override
    protected void configure() {
        super.configure();

        bindConstant().annotatedWith(ApplicationPath.class).to(applicationPath);
    }

    @Provides
    @Singleton
    protected DispatchAsync provideDispatchAsync(@ApplicationPath String applicationPath) {
        // TODO: Add support for the required parameters
        return new RestDispatchAsync(applicationPath);
    }
}
