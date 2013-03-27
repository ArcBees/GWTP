package com.arcbees.carsample.client.application.cars;

import com.arcbees.carsample.client.application.cars.car.CarPresenterFactory;
import com.arcbees.carsample.client.application.cars.car.CarProxyImplFactory;
import com.arcbees.carsample.client.application.cars.car.RootCarPresenter;
import com.arcbees.carsample.client.application.cars.car.RootCarView;
import com.arcbees.carsample.client.application.cars.car.navigation.NavigationTabPresenter;
import com.arcbees.carsample.client.application.cars.car.navigation.NavigationTabView;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class CarsModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        install(new GinFactoryModuleBuilder().build(CarPresenterFactory.class));
        install(new GinFactoryModuleBuilder().build(CarProxyImplFactory.class));

        bindPresenter(RootCarPresenter.class, RootCarPresenter.MyView.class, RootCarView.class,
                RootCarPresenter.MyProxy.class);

        bindSingletonPresenterWidget(NavigationTabPresenter.class, NavigationTabPresenter.MyView.class,
                NavigationTabView.class);
    }
}
