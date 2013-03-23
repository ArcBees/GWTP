package com.arcbees.carsample.shared.dispatch;

import com.arcbees.carsample.shared.domain.Car;

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
