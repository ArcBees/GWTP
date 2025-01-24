/*
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

package com.gwtplatform.dispatch.client.actionhandler;

import com.gwtplatform.common.client.IndirectProvider;

/**
 * Implementations of this interface will be used by
 * com.gwtplatform.dispatch.rpc.shared.DispatchAsync or
 * com.gwtplatform.dispatch.rest.shared.RestDispatch implementation to find client-side action
 * handlers.
 *
 * @deprecated Since 1.4. Use com.gwtplatform.dispatch.rpc.client.interceptor.RpcInterceptorRegistry
 */
@Deprecated
public interface ClientActionHandlerRegistry {
    /**
     * Gets the client-side action handler that supports the specific action.
     *
     * @return The the client-side action handler, or {@code null} if no appropriate client-side action handler could be
     * found.
     */
    <A> IndirectProvider<ClientActionHandler<?, ?>> find(Class<A> actionClass);
}
