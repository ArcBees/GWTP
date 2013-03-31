package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.carstore.shared.domain.Manufacturer;

public class GetManufacturerAction extends ActionImpl<GetResult<Manufacturer>> {
    private Long id;

    public GetManufacturerAction() {
    }

    public GetManufacturerAction(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
