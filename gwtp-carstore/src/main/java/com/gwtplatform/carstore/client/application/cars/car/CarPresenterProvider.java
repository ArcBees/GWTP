package com.gwtplatform.carstore.client.application.cars.car;

import javax.inject.Inject;

import com.google.inject.Provider;
import com.gwtplatform.carstore.shared.dto.CarDto;

public class CarPresenterProvider implements Provider<CarPresenter> {
    private final CarPresenterFactory carPresenterFactory;

    private CarPresenter.MyProxy proxy;
    private CarPresenter carPresenter;
    private CarDto carDto;

    @Inject
    public CarPresenterProvider(final CarPresenterFactory carPresenterFactory) {
        this.carPresenterFactory = carPresenterFactory;
    }

    @Override
    public CarPresenter get() {
        assert proxy != null : "You must call setProxy first";

        if (carPresenter == null) {
            carPresenter = carPresenterFactory.create(proxy, carDto);
        }

        return carPresenter;
    }

    public void setProxy(CarPresenter.MyProxy proxy) {
        this.proxy = proxy;
    }

    public void setCar(CarDto carDto) {
        this.carDto = carDto;
    }
}
