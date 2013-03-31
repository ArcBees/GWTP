package com.gwtplatform.carstore.server.dispatch;

import javax.inject.Inject;

import com.gwtplatform.carstore.server.dao.CarDao;
import com.gwtplatform.carstore.shared.dispatch.GetResult;
import com.gwtplatform.carstore.shared.dispatch.SaveCarAction;
import com.gwtplatform.carstore.shared.domain.Car;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class SaveCarHandler extends AbstractActionHandler<SaveCarAction, GetResult<Car>> {
    private final CarDao carDao;

    @Inject
    public SaveCarHandler(final CarDao carDao) {
        super(SaveCarAction.class);

        this.carDao = carDao;
    }

    @Override
    public GetResult<Car> execute(SaveCarAction action, ExecutionContext context)
            throws ActionException {
        Car car = carDao.put(action.getCar());

        return new GetResult<Car>(car);
    }
}
