package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.carstore.shared.domain.Rating;

public class DeleteRatingAction extends ActionImpl<NoResults> {
    private Rating rating;

    protected DeleteRatingAction() {
    }

    public DeleteRatingAction(Rating rating) {
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
