package com.gwtplatform.carstore.client.application.cars;

import com.gwtplatform.carstore.client.application.cars.car.CarPresenter;
import com.gwtplatform.carstore.client.application.cars.car.CarView;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class CarsDesktopModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        install(new CarsModule());

        bindPresenter(CarsPresenter.class, CarsPresenter.MyView.class, CarsView.class,
                CarsPresenter.MyProxy.class);

        bind(CarPresenter.MyView.class).to(CarView.class);
    }
}
