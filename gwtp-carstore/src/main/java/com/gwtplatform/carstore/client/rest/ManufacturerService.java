package com.gwtplatform.carstore.client.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.gwtplatform.carstore.shared.dispatch.GetResult;
import com.gwtplatform.carstore.shared.dispatch.GetResults;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.carstore.shared.dto.ManufacturerRatingDto;
import com.gwtplatform.carstore.shared.rest.PathParameter;
import com.gwtplatform.carstore.shared.rest.ResourcesPath;
import com.gwtplatform.carstore.shared.rest.RestParameter;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.NoResult;
import com.gwtplatform.dispatch.shared.rest.RestService;

@Path(ResourcesPath.MANUFACTURER)
public interface ManufacturerService extends RestService {
    @GET
    Action<GetResults<ManufacturerDto>> getManufacturers();

    @GET
    @Path(PathParameter.ID)
    Action<GetResult<ManufacturerDto>> get(@PathParam(RestParameter.ID) Long id);

    @POST
    Action<GetResult<ManufacturerDto>> create(ManufacturerDto manufacturerDto);

    @PUT
    @Path(PathParameter.ID)
    Action<GetResult<ManufacturerDto>> save(@PathParam(RestParameter.ID) Long id, ManufacturerDto manufacturerDto);

    @DELETE
    @Path(PathParameter.ID)
    Action<NoResult> delete(@PathParam(RestParameter.ID) Long id);

    @GET
    @Path(ResourcesPath.RATING)
    Action<GetResults<ManufacturerRatingDto>> getAverageRatings();
}
