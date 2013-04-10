package com.gwtplatform.carstore.server.dispatch;

import javax.inject.Inject;

import com.gwtplatform.carstore.server.dao.RatingDao;
import com.gwtplatform.carstore.server.dao.domain.Rating;
import com.gwtplatform.carstore.shared.dispatch.GetRatingsAction;
import com.gwtplatform.carstore.shared.dispatch.GetResults;
import com.gwtplatform.carstore.shared.dto.RatingDto;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class GetRatingsHandler extends AbstractActionHandler<GetRatingsAction, GetResults<RatingDto>> {
    private final RatingDao ratingDao;

    @Inject
    public GetRatingsHandler(final RatingDao ratingDao) {
        super(GetRatingsAction.class);

        this.ratingDao = ratingDao;
    }

    @Override
    public GetResults<RatingDto> execute(GetRatingsAction action, ExecutionContext context)
            throws ActionException {
        return new GetResults<RatingDto>(Rating.createDto(ratingDao.getAll()));
    }
}
