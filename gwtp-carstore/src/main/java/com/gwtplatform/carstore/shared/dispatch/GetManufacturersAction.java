package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.carstore.shared.dto.ManufacturerDto;

public class GetManufacturersAction extends ActionImpl<GetResults<ManufacturerDto>> {
    public GetManufacturersAction() {
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
