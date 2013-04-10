package com.gwtplatform.carstore.client.application;

import com.gwtplatform.carstore.client.application.cars.CarsMobileModule;
import com.gwtplatform.carstore.client.application.login.LoginMobileModule;
import com.gwtplatform.carstore.client.application.manufacturer.ManufacturerMobileModule;
import com.gwtplatform.carstore.client.application.rating.RatingMobileModule;
import com.gwtplatform.carstore.client.application.report.ReportMobileModule;
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

        bindPresenter(ApplicationPresenter.class, ApplicationPresenter.MyView.class, ApplicationMobileView.class,
                ApplicationPresenter.MyProxy.class);
    }
}
