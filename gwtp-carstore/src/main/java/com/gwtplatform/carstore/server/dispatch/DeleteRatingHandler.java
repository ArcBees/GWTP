package com.gwtplatform.carstore.server.dispatch;

import javax.inject.Inject;

import com.gwtplatform.carstore.server.dao.RatingDao;
import com.gwtplatform.carstore.shared.dispatch.DeleteRatingAction;
import com.gwtplatform.carstore.shared.dispatch.NoResults;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class DeleteRatingHandler extends AbstractActionHandler<DeleteRatingAction, NoResults> {
    private final RatingDao ratingDao;

    @Inject
    public DeleteRatingHandler(final RatingDao ratingDao) {
        super(DeleteRatingAction.class);

        this.ratingDao = ratingDao;
    }

    @Override
    public NoResults execute(DeleteRatingAction action, ExecutionContext context) throws ActionException {
        ratingDao.delete(action.getRating().getId());

        return new NoResults();
    }
}
