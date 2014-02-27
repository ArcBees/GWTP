/**
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.carstore.client.application;

import com.gwtplatform.carstore.client.application.cars.CarsMobileModule;
import com.gwtplatform.carstore.client.application.login.LoginMobileModule;
import com.gwtplatform.carstore.client.application.manufacturer.ManufacturerMobileModule;
import com.gwtplatform.carstore.client.application.rating.RatingMobileModule;
import com.gwtplatform.carstore.client.application.report.ReportMobileModule;
import com.gwtplatform.carstore.client.application.stats.StatisticsModule;
import com.gwtplatform.carstore.client.application.widget.WidgetMobileModule;
import com.gwtplatform.carstore.client.application.widget.message.MessagesModule;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class ApplicationTabletModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        install(new LoginMobileModule());
        install(new ManufacturerMobileModule());
        install(new CarsMobileModule());
        install(new RatingMobileModule());
        install(new WidgetMobileModule());
        install(new ReportMobileModule());

        // TODO should we make a messaging module for mobile
        install(new MessagesModule());

        install(new StatisticsModule());

        bindPresenter(ApplicationPresenter.class, ApplicationPresenter.MyView.class, ApplicationMobileView.class,
                ApplicationPresenter.MyProxy.class);
    }
}
