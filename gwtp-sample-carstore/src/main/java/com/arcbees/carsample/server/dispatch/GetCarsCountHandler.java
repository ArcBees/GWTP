package com.arcbees.carsample.server.dispatch;

import javax.inject.Inject;

import com.arcbees.carsample.server.dao.CarDao;
import com.arcbees.carsample.shared.dispatch.GetCarsCountAction;
import com.arcbees.carsample.shared.dispatch.GetResult;
import com.arcbees.carsample.shared.dto.NumberDto;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class GetCarsCountHandler extends AbstractActionHandler<GetCarsCountAction, GetResult<NumberDto<Integer>>> {
    private final CarDao carDao;

    @Inject
    public GetCarsCountHandler(final CarDao carDao) {
        super(GetCarsCountAction.class);

        this.carDao = carDao;
    }

    @Override
    public GetResult<NumberDto<Integer>> execute(GetCarsCountAction action, ExecutionContext context)
            throws ActionException {
        return new GetResult<NumberDto<Integer>>(new NumberDto<Integer>(carDao.countAll()));
    }
}
