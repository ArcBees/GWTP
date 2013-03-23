package com.arcbees.carsample.shared.dispatch;

import com.arcbees.carsample.shared.domain.Manufacturer;

public class DeleteManufacturerAction extends ActionImpl<NoResults> {
    private Manufacturer manufacturer;

    protected DeleteManufacturerAction() {
    }

    public DeleteManufacturerAction(Manufacturer manufacturer) {
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
