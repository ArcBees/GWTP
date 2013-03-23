package com.arcbees.carsample.shared.dispatch;

import com.arcbees.carsample.shared.domain.Car;

public class DeleteCarAction extends ActionImpl<NoResults> {
    private Car car;

    protected DeleteCarAction() {
    }

    public DeleteCarAction(Car car) {
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
