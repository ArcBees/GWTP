package com.gwtplatform.carstore.server.dispatch;

import java.util.List;

import javax.inject.Inject;

import com.gwtplatform.carstore.server.dao.CarDao;
import com.gwtplatform.carstore.shared.dispatch.GetCarsAction;
import com.gwtplatform.carstore.shared.dispatch.GetResults;
import com.gwtplatform.carstore.shared.domain.Car;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class GetCarsHandler extends AbstractActionHandler<GetCarsAction, GetResults<Car>> {
    private final CarDao carDao;

    @Inject
    public GetCarsHandler(final CarDao carDao) {
        super(GetCarsAction.class);

        this.carDao = carDao;
    }

    @Override
    public GetResults<Car> execute(GetCarsAction action, ExecutionContext context) throws ActionException {
        List<Car> cars = null;
        if (action.getOffset() != null && action.getLimit() != null) {
            carDao.getSome(action.getOffset(), action.getLimit());
        } else {
            cars = carDao.getAll();
        }

        return new GetResults<Car>(cars);
    }
}
