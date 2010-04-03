package com.philbeaudoin.gwtp.dispatch.client.gin;

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



import com.philbeaudoin.gwtp.dispatch.client.appengine.AppEngineSecureSessionAccessor;
import com.philbeaudoin.gwtp.dispatch.client.secure.SecureSessionAccessor;

import com.google.gwt.inject.client.AbstractGinModule;

/**
 * Configures the application to use Google App Engine security for
 * authentication.
 * 
 * @author David Peterson
 */
public class AppEngineSecurityModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind( SecureSessionAccessor.class ).to( AppEngineSecureSessionAccessor.class );
    }
}
