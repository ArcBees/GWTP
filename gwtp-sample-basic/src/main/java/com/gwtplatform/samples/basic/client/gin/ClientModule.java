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

package com.gwtplatform.samples.basic.client.gin;

import com.gwtplatform.dispatch.client.gin.RestDispatchAsyncModule;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import com.gwtplatform.samples.basic.client.application.ApplicationModule;
import com.gwtplatform.samples.basic.client.place.ClientPlaceManager;

/**
 * @author Philippe Beaudoin
 */
public class ClientModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        // Default implementation of standard resources
        install(new DefaultModule(ClientPlaceManager.class));

        install(new RestDispatchAsyncModule());

        install(new ApplicationModule());
    }
}
