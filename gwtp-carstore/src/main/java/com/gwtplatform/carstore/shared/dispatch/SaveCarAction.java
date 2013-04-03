package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.carstore.shared.dto.CarDto;

public class SaveCarAction extends ActionImpl<GetResult<CarDto>> {
    private CarDto carDto;

    protected SaveCarAction() {
    }

    public SaveCarAction(CarDto carDto) {
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
