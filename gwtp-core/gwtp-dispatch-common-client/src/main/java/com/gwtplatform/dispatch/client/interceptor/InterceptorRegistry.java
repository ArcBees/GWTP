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

package com.gwtplatform.dispatch.client.interceptor;

import com.gwtplatform.common.client.IndirectProvider;

/**
 * Base interface for interceptor registry implementations.
 */
public interface InterceptorRegistry {
    /**
     * Gets the client-side interceptor that supports the specific action.
     *
     * @return The the client-side interceptor, or {@code null} if no appropriate client-side interceptor
     * could be found.
     */
    <A> IndirectProvider<? extends Interceptor<?, ?>> find(A action);
}
