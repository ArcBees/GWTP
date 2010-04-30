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

import com.philbeaudoin.gwtp.dispatch.client.DispatchService;
import com.philbeaudoin.gwtp.dispatch.server.AbstractDispatch;
import com.philbeaudoin.gwtp.dispatch.shared.Action;

/**
 * Implementors must provide an implementation of this interface and provide it
 * to the {@link DispatchService} implementation so that it can check for
 * valid session ids.
 * 
 * @author David Peterson
 * @author Christian Goudreau
 */
public interface SessionValidator {
    /**
     * Will tell the classes that implements {@link AbstractDispatch} if he can
     * or cannot execute the {@link Action}.
     * 
     * @param sessionId
     *            The users sessionId to compare with the server sessionId if
     *            desired
     * @return True or false depending if he can or cannot execute the
     *         {@link Action}
     */
    public boolean isValid(String clientSessionId);
}