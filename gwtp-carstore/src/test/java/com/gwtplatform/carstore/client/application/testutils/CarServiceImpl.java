package com.gwtplatform.carstore.client.application.testutils;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.google.inject.TypeLiteral;
import com.gwtplatform.carstore.client.rest.CarService;
import com.gwtplatform.carstore.shared.dispatch.GetResult;
import com.gwtplatform.carstore.shared.dispatch.GetResults;
import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.carstore.shared.dto.NumberDto;
import com.gwtplatform.carstore.shared.rest.RestParameter;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.NoResult;

public class CarServiceImpl implements CarService {
    @Override
    public Action<GetResults<CarDto>> getCars() {
        return new ActionImpl<GetResults<CarDto>>(new TypeLiteral<Action<GetResults<CarDto>>>() {
        });
    }

    @Override
    public Action<GetResults<CarDto>> getCars(@QueryParam(RestParameter.OFFSET) int offset,
            @QueryParam(RestParameter.LIMIT) int limit) {
        return new ActionImpl<GetResults<CarDto>>(new TypeLiteral<Action<GetResults<CarDto>>>() {
        });
    }

    @Override
    public Action<GetResult<NumberDto<Integer>>> getCarsCount() {
        return new ActionImpl<GetResult<NumberDto<Integer>>>(new TypeLiteral<Action<GetResult<NumberDto<Integer>>>>() {
        });
    }

    @Override
    public Action<GetResult<CarDto>> saveOrCreate(CarDto carDto) {
        return new ActionImpl<GetResult<CarDto>>(new TypeLiteral<Action<GetResult<CarDto>>>() {
        });
    }

    @Override
    public Action<NoResult> delete(@PathParam(RestParameter.ID) Long carId) {
        return new ActionImpl<NoResult>(new TypeLiteral<Action<NoResult>>() {
        });
    }
}
