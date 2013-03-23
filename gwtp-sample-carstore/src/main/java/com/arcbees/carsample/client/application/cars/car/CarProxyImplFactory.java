package com.arcbees.carsample.client.application.cars.car;

public interface CarProxyImplFactory {
    CarProxyImpl create(CarPresenterProvider carPresenterProvider, String nameToken);
}
