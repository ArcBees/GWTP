package com.gwtplatform.carstore.client.application.rating.ui;

import com.gwtplatform.carstore.shared.domain.Rating;
import com.gwtplatform.mvp.client.UiHandlers;

public interface EditRatingUiHandlers extends UiHandlers {
    void createNew();

    void onSave(Rating rating);

    void onCancel();
}
