package com.arcbees.carsample.shared.dispatch;

import com.arcbees.carsample.shared.domain.Manufacturer;

public class GetManufacturerAction extends ActionImpl<GetResult<Manufacturer>> {
    private Integer id;

    public GetManufacturerAction() {
    }

    public GetManufacturerAction(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
