package com.arcbees.carsample.client.application;

import com.arcbees.carsample.client.application.cars.CarsDesktopModule;
import com.arcbees.carsample.client.application.login.LoginModule;
import com.arcbees.carsample.client.application.manufacturer.ManufacturerModule;
import com.arcbees.carsample.client.application.rating.RatingModule;
import com.arcbees.carsample.client.application.report.ReportModule;
import com.arcbees.carsample.client.application.widget.WidgetModule;
import com.arcbees.carsample.client.application.widget.message.MessagesModule;
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
