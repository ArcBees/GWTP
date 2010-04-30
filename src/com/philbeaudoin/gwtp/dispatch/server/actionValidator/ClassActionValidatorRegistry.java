/**
 * Copyright 2010 Philippe Beaudoin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philbeaudoin.gwtp.dispatch.server.actionValidator;

import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

/**
 * Instances of this interface allow {@link ActionValidator} classes to
 * be registered for specific {@link Action} types. This is typically to allow
 * lazy-loading of validators.
 * 
 * @see LazyActionValidatorRegistry
 * 
 * @author Christian Goudreau
 */
public interface ClassActionValidatorRegistry extends ActionValidatorRegistry {
    /**
     * Registers the specified {@link ActionValidator} class with the
     * registry.
     * 
     * @param <A>
     *            Type of associated {@link Action}
     * @param <R>
     *            Type of associated {@link Result}
     * @param actionClass
     *            The {@link Action} class
     * @param actionValidatorClass
     *            The {@link ActionValidator} class
     */
    public <A extends Action<R>, R extends Result> void addActionValidatorClass(Class<A> actionClass, Class<? extends ActionValidator> actionValidatorClass);

    /**
     * Removes any registration of specified class, as well as any instances
     * which have been created.
     * 
     * @param <A>
     *            Type of associated {@link Action}
     * @param <R>
     *            Type of associated {@link Result}
     * @param actionClass
     *            The {@link Action} class
     * @param actionValidatorClass
     *            The {@link ActionValidator} class
     */
    public <A extends Action<R>, R extends Result> void removeActionValidatorClass(Class<A> actionClass, Class<? extends ActionValidator> actionValidatorClass);
}