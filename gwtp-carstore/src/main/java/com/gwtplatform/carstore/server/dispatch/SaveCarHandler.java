package com.gwtplatform.carstore.server.dispatch;

import javax.inject.Inject;

import com.gwtplatform.carstore.server.dao.CarDao;
import com.gwtplatform.carstore.server.dao.domain.Car;
import com.gwtplatform.carstore.shared.dispatch.GetResult;
import com.gwtplatform.carstore.shared.dispatch.SaveCarAction;
import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class SaveCarHandler extends AbstractActionHandler<SaveCarAction, GetResult<CarDto>> {
    private final CarDao carDao;

    @Inject
    public SaveCarHandler(final CarDao carDao) {
        super(SaveCarAction.class);

        this.carDao = carDao;
    }

    @Override
    public GetResult<CarDto> execute(SaveCarAction action, ExecutionContext context)
            throws ActionException {
        CarDto carDto = Car.createDto(carDao.put(Car.create(action.getCar())));

        return new GetResult<CarDto>(carDto);
    }
}
