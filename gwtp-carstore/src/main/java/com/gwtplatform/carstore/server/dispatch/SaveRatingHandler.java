package com.gwtplatform.carstore.server.dispatch;

import javax.inject.Inject;

import com.gwtplatform.carstore.server.dao.RatingDao;
import com.gwtplatform.carstore.server.dao.domain.Rating;
import com.gwtplatform.carstore.shared.dispatch.GetResult;
import com.gwtplatform.carstore.shared.dispatch.SaveRatingAction;
import com.gwtplatform.carstore.shared.dto.RatingDto;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class SaveRatingHandler extends AbstractActionHandler<SaveRatingAction, GetResult<RatingDto>> {
    private final RatingDao ratingDao;

    @Inject
    public SaveRatingHandler(final RatingDao ratingDao) {
        super(SaveRatingAction.class);

        this.ratingDao = ratingDao;
    }

    @Override
    public GetResult<RatingDto> execute(SaveRatingAction action, ExecutionContext context) throws ActionException {
        RatingDto ratingDto = Rating.createDto(ratingDao.put(Rating.create(action.getRating())));

        return new GetResult<RatingDto>(ratingDto);
    }
}
