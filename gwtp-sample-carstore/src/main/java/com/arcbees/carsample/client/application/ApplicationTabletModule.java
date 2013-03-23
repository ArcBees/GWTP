package com.arcbees.carsample.client.application;

import com.arcbees.carsample.client.application.cars.CarsMobileModule;
import com.arcbees.carsample.client.application.login.LoginMobileModule;
import com.arcbees.carsample.client.application.manufacturer.ManufacturerMobileModule;
import com.arcbees.carsample.client.application.rating.RatingMobileModule;
import com.arcbees.carsample.client.application.report.ReportMobileModule;
import com.arcbees.carsample.client.application.widget.WidgetMobileModule;
import com.arcbees.carsample.client.application.widget.message.MessagesModule;
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

        bindPresenter(ApplicationPresenter.class, ApplicationPresenter.MyView.class, ApplicationMobileView.class,
                ApplicationPresenter.MyProxy.class);
    }
}
