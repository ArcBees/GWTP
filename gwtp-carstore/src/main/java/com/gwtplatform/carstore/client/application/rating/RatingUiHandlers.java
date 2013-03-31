package com.gwtplatform.carstore.client.application.rating;

import com.gwtplatform.carstore.shared.domain.Rating;
import com.gwtplatform.mvp.client.UiHandlers;

public interface RatingUiHandlers extends UiHandlers {
    void onDelete(Rating rating);

    void onCreate();
}
