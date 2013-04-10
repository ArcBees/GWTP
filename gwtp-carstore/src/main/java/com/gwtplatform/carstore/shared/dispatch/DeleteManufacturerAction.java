package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.carstore.shared.dto.ManufacturerDto;

public class DeleteManufacturerAction extends ActionImpl<NoResults> {
    private ManufacturerDto manufacturerDto;

    protected DeleteManufacturerAction() {
    }

    public DeleteManufacturerAction(ManufacturerDto manufacturerDto) {
        this.manufacturerDto = manufacturerDto;
    }

    public ManufacturerDto getManufacturer() {
        return manufacturerDto;
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
