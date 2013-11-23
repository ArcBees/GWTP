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

import javax.inject.Singleton;

import com.google.gwt.inject.client.AbstractGinModule;
import com.gwtplatform.dispatch.client.PhoneGapDispatchAsync;
import com.gwtplatform.dispatch.shared.DispatchAsync;

/**
 * @deprecated Please use {@link com.gwtplatform.dispatch.rpc.client.gin.PhoneGapDispatchAsyncModule}.
 */
@Deprecated
public class PhoneGapDispatchAsyncModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(DispatchAsync.class).to(PhoneGapDispatchAsync.class).in(Singleton.class);
    }
}
