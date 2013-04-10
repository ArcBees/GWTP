package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.carstore.shared.dto.RatingDto;

public class DeleteRatingAction extends ActionImpl<NoResults> {
    private RatingDto ratingDto;

    protected DeleteRatingAction() {
    }

    public DeleteRatingAction(RatingDto ratingDto) {
        this.ratingDto = ratingDto;
    }

    public RatingDto getRating() {
        return ratingDto;
    }

    @Override
    public boolean isSecured() {
        return false;
    }
}
