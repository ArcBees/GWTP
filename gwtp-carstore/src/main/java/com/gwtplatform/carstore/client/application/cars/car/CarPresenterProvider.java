package com.gwtplatform.carstore.client.application.cars.car;

import javax.inject.Inject;

import com.google.inject.Provider;
import com.gwtplatform.carstore.shared.domain.Car;

public class CarPresenterProvider implements Provider<CarPresenter> {
    private final CarPresenterFactory carPresenterFactory;

    private CarPresenter.MyProxy proxy;
    private CarPresenter carPresenter;
    private Car car;

    @Inject
    public CarPresenterProvider(final CarPresenterFactory carPresenterFactory) {
        this.carPresenterFactory = carPresenterFactory;
    }

    @Override
    public CarPresenter get() {
        assert proxy != null : "You must call setProxy first";

        if (carPresenter == null) {
            carPresenter = carPresenterFactory.create(proxy, car);
        }

        return carPresenter;
    }

    public void setProxy(CarPresenter.MyProxy proxy) {
        this.proxy = proxy;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
