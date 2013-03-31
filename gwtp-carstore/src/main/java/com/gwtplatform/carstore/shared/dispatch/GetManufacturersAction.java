package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.carstore.shared.domain.Manufacturer;

public class GetManufacturersAction extends ActionImpl<GetResults<Manufacturer>> {
    public GetManufacturersAction() {
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
