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
 * This is the interface that define the map of {@link ActionValidator}.
 * 
 * @param <A>
 *            Type of the associated {@link Action}
 * @param <R>
 *            Type of the associated {@link Result}
 * 
 * @author Christian Goudreau
 */
public interface SessionValidatorMap<A extends Action<R>, R extends Result> {
    /**
     * @return the {@link Action} class associated
     */
    public Class<A> getActionClass();

    /**
     * @return the {@link ActionValidator} class associated
     */
    public Class<? extends ActionValidator> getSecureSessionValidatorClass();
}