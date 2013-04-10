package com.gwtplatform.carstore.client.application.cars;

import com.gwtplatform.carstore.client.application.cars.car.CarMobileView;
import com.gwtplatform.carstore.client.application.cars.car.CarPresenter;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class CarsMobileModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        install(new CarsModule());

        bindPresenter(CarsPresenter.class, CarsPresenter.MyView.class, CarsMobileView.class,
                CarsPresenter.MyProxy.class);

        bind(CarPresenter.MyView.class).to(CarMobileView.class);
    }
}
