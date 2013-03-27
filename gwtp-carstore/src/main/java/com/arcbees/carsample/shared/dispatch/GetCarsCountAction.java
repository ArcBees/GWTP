package com.arcbees.carsample.shared.dispatch;

import com.arcbees.carsample.shared.dto.NumberDto;

public class GetCarsCountAction extends ActionImpl<GetResult<NumberDto<Integer>>> {
    public GetCarsCountAction() {
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
