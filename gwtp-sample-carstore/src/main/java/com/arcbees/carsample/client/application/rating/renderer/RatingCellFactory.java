package com.arcbees.carsample.client.application.rating.renderer;

import com.arcbees.carsample.shared.domain.Rating;
import com.google.gwt.cell.client.ActionCell.Delegate;

public interface RatingCellFactory {
    RatingCell create(Delegate<Rating> delegate);
}
