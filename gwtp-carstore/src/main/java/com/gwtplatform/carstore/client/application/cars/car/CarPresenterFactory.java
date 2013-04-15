package com.gwtplatform.carstore.client.application.cars.car;

import com.gwtplatform.carstore.shared.dto.CarDto;

public interface CarPresenterFactory {
    CarPresenter create(CarPresenter.MyProxy proxy, CarDto carDto);
}
