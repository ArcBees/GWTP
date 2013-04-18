package com.gwtplatform.carstore.client.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.gwtplatform.carstore.shared.dispatch.GetResult;
import com.gwtplatform.carstore.shared.dispatch.GetResults;
import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.carstore.shared.dto.NumberDto;
import com.gwtplatform.carstore.shared.rest.PathParameter;
import com.gwtplatform.carstore.shared.rest.ResourcesPath;
import com.gwtplatform.carstore.shared.rest.RestParameter;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.NoResult;
import com.gwtplatform.dispatch.shared.rest.RestService;

@Path(ResourcesPath.CAR)
public interface CarService extends RestService {
    @GET
    Action<GetResults<CarDto>> getCars();

    @GET
    Action<GetResults<CarDto>> getCars(@QueryParam(RestParameter.OFFSET) int offset,
            @QueryParam(RestParameter.LIMIT) int limit);

    @GET
    @Path(ResourcesPath.COUNT)
    Action<GetResult<NumberDto<Integer>>> getCarsCount();

    @POST
    Action<GetResult<CarDto>> create(CarDto carDto);

    @PUT
    @Path(PathParameter.ID)
    Action<GetResult<CarDto>> save(@PathParam(RestParameter.ID) Long carId, CarDto carDto);

    @DELETE
    @Path(PathParameter.ID)
    Action<NoResult> delete(@PathParam(RestParameter.ID) Long carId);
}
