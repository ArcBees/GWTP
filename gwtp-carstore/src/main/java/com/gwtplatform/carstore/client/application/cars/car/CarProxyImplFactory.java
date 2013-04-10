package com.gwtplatform.carstore.client.application.cars.car;

public interface CarProxyImplFactory {
    CarProxyImpl create(CarPresenterProvider carPresenterProvider, String nameToken);
}
