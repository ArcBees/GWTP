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
