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

package com.gwtplatform.dispatch.client.rest.actionhandler;

import com.gwtplatform.common.client.IndirectProvider;
import com.gwtplatform.dispatch.shared.rest.RestAction;

/**
 * Implementations of this interface will be used by
 * {@link com.gwtplatform.dispatch.shared.rest.RestDispatch RestDispatch}
 * implementation to find client-side action handlers.
 */
public interface ClientRestActionHandlerRegistry {
    /**
     * Gets the client-side action handler that supports the specific action.
     *
     * @return The the client-side action handler , or {@code null} if no
     *         appropriate client-side action handler could be found.
     */
    <A extends RestAction<?>> IndirectProvider<ClientRestActionHandler<?, ?>> find(Class<A> actionClass);
}
