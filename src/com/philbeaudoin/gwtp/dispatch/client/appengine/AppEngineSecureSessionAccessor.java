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

package com.philbeaudoin.gwtp.dispatch.client.appengine;

import com.philbeaudoin.gwtp.dispatch.client.secure.CookieSecureSessionAccessor;
import com.philbeaudoin.gwtp.dispatch.client.secure.SecureSessionAccessor;

/**
 * An implementation of {@link SecureSessionAccessor} for Google App Engine
 * authentication.
 * 
 * @author David Peterson
 * @author David Chandler
 */
public class AppEngineSecureSessionAccessor extends CookieSecureSessionAccessor {

    private final static String COOKIE_NAME = "ACSID";

    public AppEngineSecureSessionAccessor() {
        super( COOKIE_NAME );
    }
}
