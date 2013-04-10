package com.gwtplatform.carstore.client.application.rating.ui;

import com.gwtplatform.carstore.shared.dto.RatingDto;
import com.gwtplatform.mvp.client.UiHandlers;

public interface EditRatingUiHandlers extends UiHandlers {
    void createNew();

    void onSave(RatingDto ratingDto);

    void onCancel();
}
