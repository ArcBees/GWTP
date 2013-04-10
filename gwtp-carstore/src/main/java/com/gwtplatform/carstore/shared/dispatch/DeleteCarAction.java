package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.carstore.shared.dto.CarDto;

public class DeleteCarAction extends ActionImpl<NoResults> {
    private CarDto carDto;

    protected DeleteCarAction() {
    }

    public DeleteCarAction(CarDto carDto) {
        this.carDto = carDto;
    }

    public CarDto getCar() {
        return carDto;
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
