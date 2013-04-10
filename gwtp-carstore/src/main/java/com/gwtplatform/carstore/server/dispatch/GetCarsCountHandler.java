package com.gwtplatform.carstore.server.dispatch;

import javax.inject.Inject;

import com.gwtplatform.carstore.server.dao.CarDao;
import com.gwtplatform.carstore.shared.dispatch.GetCarsCountAction;
import com.gwtplatform.carstore.shared.dispatch.GetResult;
import com.gwtplatform.carstore.shared.dto.NumberDto;
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
