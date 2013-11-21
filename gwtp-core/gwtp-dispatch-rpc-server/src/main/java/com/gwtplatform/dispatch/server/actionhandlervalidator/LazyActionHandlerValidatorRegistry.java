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

package com.gwtplatform.dispatch.server.actionhandlervalidator;

import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

/**
 * @author Christian Goudreau
 *
 * @deprecated Please use
 * {@link com.gwtplatform.dispatch.rpc.server.actionhandlervalidator.LazyActionHandlerValidatorRegistry}.
 */
@Deprecated
public interface LazyActionHandlerValidatorRegistry extends
        ActionHandlerValidatorRegistry {
    /**
     * Registers the specified {@link com.gwtplatform.dispatch.server.actionvalidator.ActionValidator}
     * class with the registry.
     *
     * @param <A>                         Type of associated {@link com.gwtplatform.dispatch.shared.Action}
     * @param <R>                         Type of associated {@link com.gwtplatform.dispatch.shared.Result}
     * @param actionClass                 The {@link com.gwtplatform.dispatch.shared.Action} class
     * @param actionHandlerValidatorClass The {@link ActionHandlerValidatorClass}
     */
    <A extends Action<R>, R extends Result> void addActionHandlerValidatorClass(
            Class<A> actionClass,
            ActionHandlerValidatorClass<A, R> actionHandlerValidatorClass);

    /**
     * Removes any registration of specified class, as well as any instances which
     * have been created.
     *
     * @param <A>                         Type of associated {@link com.gwtplatform.dispatch.shared.Action}
     * @param <R>                         Type of associated {@link com.gwtplatform.dispatch.shared.Result}
     * @param actionClass                 The {@link com.gwtplatform.dispatch.shared.Action} class
     * @param actionHandlerValidatorClass The {@link ActionHandlerValidatorClass} class
     */
    <A extends Action<R>, R extends Result> void removeActionHandlerValidatorClass(
            Class<A> actionClass,
            ActionHandlerValidatorClass<A, R> actionHandlerValidatorClass);
}
