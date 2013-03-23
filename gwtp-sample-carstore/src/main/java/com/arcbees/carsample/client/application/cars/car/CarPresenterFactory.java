package com.arcbees.carsample.client.application.cars.car;

import com.arcbees.carsample.shared.domain.Car;

public interface CarPresenterFactory {
    CarPresenter create(CarPresenter.MyProxy proxy, Car car);
}
