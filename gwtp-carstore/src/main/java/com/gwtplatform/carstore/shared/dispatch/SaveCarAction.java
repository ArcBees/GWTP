package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.carstore.shared.domain.Car;

public class SaveCarAction extends ActionImpl<GetResult<Car>> {
    private Car car;

    protected SaveCarAction() {
    }

    public SaveCarAction(Car car) {
        this.car = car;
    }

    public Car getCar() {
        return car;
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
