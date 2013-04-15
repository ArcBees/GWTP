package com.gwtplatform.carstore.client.application.rating;

import com.gwtplatform.carstore.shared.dto.RatingDto;
import com.gwtplatform.mvp.client.UiHandlers;

public interface RatingDetailUiHandlers extends UiHandlers {
    void onSave(RatingDto ratingDto);
}
