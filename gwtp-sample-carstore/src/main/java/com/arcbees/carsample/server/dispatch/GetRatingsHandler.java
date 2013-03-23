package com.arcbees.carsample.server.dispatch;

import javax.inject.Inject;

import com.arcbees.carsample.server.dao.RatingDao;
import com.arcbees.carsample.shared.dispatch.GetRatingsAction;
import com.arcbees.carsample.shared.dispatch.GetResults;
import com.arcbees.carsample.shared.domain.Rating;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class GetRatingsHandler extends AbstractActionHandler<GetRatingsAction, GetResults<Rating>> {
    private final RatingDao ratingDao;

    @Inject
    public GetRatingsHandler(final RatingDao ratingDao) {
        super(GetRatingsAction.class);

        this.ratingDao = ratingDao;
    }

    @Override
    public GetResults<Rating> execute(GetRatingsAction action, ExecutionContext context)
            throws ActionException {
        return new GetResults<Rating>(ratingDao.getAll());
    }
}
