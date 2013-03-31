package com.gwtplatform.carstore.client.application.cars.car;

import com.gwtplatform.carstore.shared.domain.Car;

public interface CarPresenterFactory {
    CarPresenter create(CarPresenter.MyProxy proxy, Car car);
}
