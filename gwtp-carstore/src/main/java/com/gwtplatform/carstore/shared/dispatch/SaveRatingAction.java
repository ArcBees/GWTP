package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.carstore.shared.domain.Rating;

public class SaveRatingAction extends ActionImpl<GetResult<Rating>> {
    private Rating rating;

    protected SaveRatingAction() {
    }

    public SaveRatingAction(Rating rating) {
        this.rating = rating;
    }

    public Rating getRating() {
        return rating;
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
