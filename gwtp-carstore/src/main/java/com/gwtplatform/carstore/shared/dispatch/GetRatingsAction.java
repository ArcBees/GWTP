package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.carstore.shared.dto.RatingDto;

public class GetRatingsAction extends ActionImpl<GetResults<RatingDto>> {
    public GetRatingsAction() {
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
