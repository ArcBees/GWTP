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

package com.gwtplatform.samples.mobile.client.gin.desktop;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import com.gwtplatform.samples.mobile.client.application.ApplicationDesktopModule;
import com.gwtplatform.samples.mobile.client.place.DefaultPlace;
import com.gwtplatform.samples.mobile.client.place.NameTokens;
import com.gwtplatform.samples.mobile.client.place.PlaceManager;

public class DesktopModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
      // Default implementation of standard resources
      install(new DefaultModule(PlaceManager.class));

      // Constants
      bindConstant().annotatedWith(DefaultPlace.class).to(NameTokens.homePage);

      install(new ApplicationDesktopModule());
    }
}
