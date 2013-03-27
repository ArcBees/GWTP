package com.arcbees.carsample.client.application.rating.ui;

import com.arcbees.carsample.shared.domain.Rating;
import com.gwtplatform.mvp.client.UiHandlers;

public interface EditRatingUiHandlers extends UiHandlers {
    void createNew();

    void onSave(Rating rating);

    void onCancel();
}
