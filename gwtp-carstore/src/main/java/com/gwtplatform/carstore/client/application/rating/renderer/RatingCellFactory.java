package com.gwtplatform.carstore.client.application.rating.renderer;

import com.google.gwt.cell.client.ActionCell.Delegate;
import com.gwtplatform.carstore.shared.dto.RatingDto;

public interface RatingCellFactory {
    RatingCell create(Delegate<RatingDto> delegate);
}
