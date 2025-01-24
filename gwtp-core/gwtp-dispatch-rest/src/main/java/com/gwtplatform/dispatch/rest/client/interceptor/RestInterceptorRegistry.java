/*
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.client.interceptor;

import com.gwtplatform.common.client.IndirectProvider;
import com.gwtplatform.dispatch.client.interceptor.InterceptorRegistry;

/**
 * Implementations of this interface will be used by
 * {@code com.gwtplatform.dispatch.rest.shared.RestDispatch} implementation to find
 * client-side interceptor.
 */
public interface RestInterceptorRegistry extends InterceptorRegistry {
    /**
     * Gets the client-side interceptor that supports the specific action.
     *
     * @return The the client-side interceptor, or {@code null} if no appropriate client-side interceptor
     * could be found.
     */
    <A> IndirectProvider<RestInterceptor> find(A action);
}
