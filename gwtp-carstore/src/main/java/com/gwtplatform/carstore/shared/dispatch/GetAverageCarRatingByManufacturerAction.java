package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.carstore.shared.dto.ManufacturerRatingDto;

public class GetAverageCarRatingByManufacturerAction extends ActionImpl<GetResults<ManufacturerRatingDto>> {
    public GetAverageCarRatingByManufacturerAction() {
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
