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

package com.philbeaudoin.gwtp.dispatch.server.sessionValidator;

import com.philbeaudoin.gwtp.dispatch.shared.Action;
import com.philbeaudoin.gwtp.dispatch.shared.Result;

/**
 * This is a subclass of {@link SessionValidatorRegistry} wich allows
 * registration of validators by passing in the handler instance directly.
 * 
 * @author Christian Goudreau
 */
public interface InstanceSessionValidatorRegistry extends SessionValidatorRegistry {
    /**
     * @param <A>
     *            Type of associated {@link Action}
     * @param <R>
     *            Type of associated {@link Result}
     * @param action
     *            The {@link Action}
     * @param secureSessionValidator
     *            The {@link ActionValidator}
     */
    public <A extends Action<R>, R extends Result> void addSecureSessionValidator(Class<A> actionClass, ActionValidator secureSessionValidator);

    /**
     * @param <A>
     *            Type of associated {@link Action}
     * @param <R>
     *            Type of associated {@link Result}
     * @param action
     *            The {@link Action}
     * @return <code>true</code> if the handler was previously registered and
     *         was successfully removed.
     */
    public <A extends Action<R>, R extends Result> boolean removeSecureSessionValidator(Class<A> actionClass);
}
