package com.arcbees.carsample.shared.dispatch;

import com.arcbees.carsample.shared.domain.Manufacturer;

public class GetManufacturersAction extends ActionImpl<GetResults<Manufacturer>> {
    public GetManufacturersAction() {
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
