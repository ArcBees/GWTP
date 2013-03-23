package com.arcbees.carsample.shared.dispatch;

import com.arcbees.carsample.shared.domain.Rating;

public class GetRatingsAction extends ActionImpl<GetResults<Rating>> {
    public GetRatingsAction() {
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
