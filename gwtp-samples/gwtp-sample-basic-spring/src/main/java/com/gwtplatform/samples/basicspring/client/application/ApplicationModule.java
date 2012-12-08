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

package com.gwtplatform.samples.basicspring.client.application;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.samples.basicspring.client.application.response.ResponsePresenter;
import com.gwtplatform.samples.basicspring.client.application.response.ResponseView;

/**
 * @author Philippe Beaudoin
 */
public class ApplicationModule extends AbstractPresenterModule {
  @Override
  protected void configure() {
    // Application Presenters
    bindPresenter(ApplicationPresenter.class, ApplicationPresenter.MyView.class,
        ApplicationView.class, ApplicationPresenter.MyProxy.class);

    bindPresenter(ResponsePresenter.class, ResponsePresenter.MyView.class,
        ResponseView.class, ResponsePresenter.MyProxy.class);
  }
}
