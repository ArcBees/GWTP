package com.arcbees.carsample.client.application.rating;

import com.arcbees.carsample.shared.domain.Rating;
import com.gwtplatform.mvp.client.UiHandlers;

public interface RatingDetailUiHandlers extends UiHandlers {
    void onSave(Rating rating);
}
