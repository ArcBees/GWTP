package com.gwtplatform.carstore.server.dispatch;

import java.util.List;

import javax.inject.Inject;

import com.gwtplatform.carstore.server.dao.CarDao;
import com.gwtplatform.carstore.server.dao.domain.Car;
import com.gwtplatform.carstore.shared.dispatch.GetCarsAction;
import com.gwtplatform.carstore.shared.dispatch.GetResults;
import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class GetCarsHandler extends AbstractActionHandler<GetCarsAction, GetResults<CarDto>> {
    private final CarDao carDao;

    @Inject
    public GetCarsHandler(final CarDao carDao) {
        super(GetCarsAction.class);

        this.carDao = carDao;
    }

    @Override
    public GetResults<CarDto> execute(GetCarsAction action, ExecutionContext context) throws ActionException {
        List<CarDto> carDtos = null;
        if (action.getOffset() != null && action.getLimit() != null) {
            carDtos = getSome(action);
        } else {
            carDtos = Car.createDto(carDao.getAll());
        }

        return new GetResults<CarDto>(carDtos);
    }

    private List<CarDto> getSome(GetCarsAction action) {
        List<Car> cars = carDao.getSome(action.getOffset(), action.getLimit());
        
        return Car.createDto(cars);
    }
}
