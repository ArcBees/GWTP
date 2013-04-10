package com.gwtplatform.carstore.client.application.rating;

import com.gwtplatform.carstore.shared.dto.RatingDto;
import com.gwtplatform.mvp.client.UiHandlers;

public interface RatingUiHandlers extends UiHandlers {
    void onDelete(RatingDto ratingDto);

    void onCreate();
}
