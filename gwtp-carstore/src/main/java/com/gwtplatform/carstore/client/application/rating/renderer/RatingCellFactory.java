package com.gwtplatform.carstore.client.application.rating.renderer;

import com.google.gwt.cell.client.ActionCell.Delegate;
import com.gwtplatform.carstore.shared.domain.Rating;

public interface RatingCellFactory {
    RatingCell create(Delegate<Rating> delegate);
}
