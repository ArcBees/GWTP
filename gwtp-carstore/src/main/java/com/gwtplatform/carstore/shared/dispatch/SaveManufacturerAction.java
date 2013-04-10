package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.carstore.shared.dto.ManufacturerDto;

public class SaveManufacturerAction extends ActionImpl<GetResult<ManufacturerDto>> {
    private ManufacturerDto manufacturerDto;

    protected SaveManufacturerAction() {
    }

    public SaveManufacturerAction(ManufacturerDto manufacturerDto) {
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
