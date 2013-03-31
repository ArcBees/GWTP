package com.gwtplatform.carstore.server.dispatch;

import javax.inject.Inject;

import com.gwtplatform.carstore.server.dao.RatingDao;
import com.gwtplatform.carstore.shared.dispatch.GetResult;
import com.gwtplatform.carstore.shared.dispatch.SaveRatingAction;
import com.gwtplatform.carstore.shared.domain.Rating;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class SaveRatingHandler extends AbstractActionHandler<SaveRatingAction, GetResult<Rating>> {
    private final RatingDao ratingDao;

    @Inject
    public SaveRatingHandler(final RatingDao ratingDao) {
        super(SaveRatingAction.class);

        this.ratingDao = ratingDao;
    }

    @Override
    public GetResult<Rating> execute(SaveRatingAction action, ExecutionContext context) throws ActionException {
        Rating rating = ratingDao.put(action.getRating());

        return new GetResult<Rating>(rating);
    }
}
