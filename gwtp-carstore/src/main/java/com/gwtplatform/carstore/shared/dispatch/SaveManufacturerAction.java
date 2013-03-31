package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.carstore.shared.domain.Manufacturer;

public class SaveManufacturerAction extends ActionImpl<GetResult<Manufacturer>> {
    private Manufacturer manufacturer;

    protected SaveManufacturerAction() {
    }

    public SaveManufacturerAction(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
