package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.carstore.shared.domain.Rating;

public class GetRatingsAction extends ActionImpl<GetResults<Rating>> {
    public GetRatingsAction() {
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
