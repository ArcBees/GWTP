package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.carstore.shared.dto.NumberDto;

public class GetCarsCountAction extends ActionImpl<GetResult<NumberDto<Integer>>> {
    public GetCarsCountAction() {
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
