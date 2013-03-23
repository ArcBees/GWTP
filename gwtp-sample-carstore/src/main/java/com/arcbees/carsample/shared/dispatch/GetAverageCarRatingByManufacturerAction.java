package com.arcbees.carsample.shared.dispatch;

import com.arcbees.carsample.shared.dto.ManufacturerRatingDto;

public class GetAverageCarRatingByManufacturerAction extends ActionImpl<GetResults<ManufacturerRatingDto>> {
    public GetAverageCarRatingByManufacturerAction() {
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
