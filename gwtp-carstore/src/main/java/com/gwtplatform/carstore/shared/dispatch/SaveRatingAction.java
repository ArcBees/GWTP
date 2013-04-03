package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.carstore.shared.dto.RatingDto;

public class SaveRatingAction extends ActionImpl<GetResult<RatingDto>> {
    private RatingDto ratingDto;

    protected SaveRatingAction() {
    }

    public SaveRatingAction(RatingDto ratingDto) {
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
