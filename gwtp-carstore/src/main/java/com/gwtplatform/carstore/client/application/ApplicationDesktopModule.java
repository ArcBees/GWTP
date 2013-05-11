/*
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.carstore.client.application;

import com.gwtplatform.carstore.client.application.cars.CarsDesktopModule;
import com.gwtplatform.carstore.client.application.login.LoginModule;
import com.gwtplatform.carstore.client.application.manufacturer.ManufacturerModule;
import com.gwtplatform.carstore.client.application.rating.RatingModule;
import com.gwtplatform.carstore.client.application.report.ReportModule;
import com.gwtplatform.carstore.client.application.widget.WidgetModule;
import com.gwtplatform.carstore.client.application.widget.message.MessagesModule;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class ApplicationDesktopModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        install(new LoginModule());
        install(new ManufacturerModule());
        install(new CarsDesktopModule());
        install(new RatingModule());
        install(new WidgetModule());
        install(new MessagesModule());
        install(new ReportModule());

        bindPresenter(ApplicationPresenter.class, ApplicationPresenter.MyView.class, ApplicationView.class,
                ApplicationPresenter.MyProxy.class);
    }
}
