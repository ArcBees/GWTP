package com.arcbees.carsample.client.application.rating;

import com.arcbees.carsample.shared.domain.Rating;
import com.gwtplatform.mvp.client.UiHandlers;

public interface RatingUiHandlers extends UiHandlers {
    void onDelete(Rating rating);

    void onCreate();
}
