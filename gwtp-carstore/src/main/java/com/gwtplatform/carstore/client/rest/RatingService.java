package com.gwtplatform.carstore.client.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.gwtplatform.carstore.shared.dispatch.GetResult;
import com.gwtplatform.carstore.shared.dispatch.GetResults;
import com.gwtplatform.carstore.shared.dto.RatingDto;
import com.gwtplatform.carstore.shared.rest.PathParameter;
import com.gwtplatform.carstore.shared.rest.ResourcesPath;
import com.gwtplatform.carstore.shared.rest.RestParameter;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.NoResult;
import com.gwtplatform.dispatch.shared.rest.RestService;

@Path(ResourcesPath.RATING)
public interface RatingService extends RestService {
    @GET
    Action<GetResults<RatingDto>> getRatings();

    @GET
    @Path(PathParameter.ID)
    Action<GetResult<RatingDto>> get(@PathParam(RestParameter.ID) Long id);

    @POST
    Action<GetResult<RatingDto>> saveOrCreate(RatingDto RatingDto);

    @DELETE
    @Path(PathParameter.ID)
    Action<NoResult> delete(@PathParam(RestParameter.ID) Long id);
}
