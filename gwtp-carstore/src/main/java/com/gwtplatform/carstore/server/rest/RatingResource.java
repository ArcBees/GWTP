package com.gwtplatform.carstore.server.rest;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gwtplatform.carstore.server.dao.RatingDao;
import com.gwtplatform.carstore.server.dao.domain.Rating;
import com.gwtplatform.carstore.shared.dispatch.GetResult;
import com.gwtplatform.carstore.shared.dispatch.GetResults;
import com.gwtplatform.carstore.shared.dto.RatingDto;
import com.gwtplatform.carstore.shared.rest.PathParameter;
import com.gwtplatform.carstore.shared.rest.ResourcesPath;
import com.gwtplatform.carstore.shared.rest.RestParameter;
import com.gwtplatform.dispatch.shared.NoResult;

@Path(ResourcesPath.RATING)
@Produces(MediaType.APPLICATION_JSON)
public class RatingResource {
    private final RatingDao ratingDao;

    @Inject
    RatingResource(RatingDao ratingDao) {
        this.ratingDao = ratingDao;
    }

    @GET
    public GetResults<RatingDto> getRatings() {
        return new GetResults<RatingDto>(Rating.createDto(ratingDao.getAll()));
    }

    @Path(PathParameter.ID)
    @GET
    public GetResult<RatingDto> get(@PathParam(RestParameter.ID) Long id) {
        return new GetResult<RatingDto>(Rating.createDto(ratingDao.get(id)));
    }

    @POST
    public GetResult<RatingDto> saveOrCreate(RatingDto ratingDto) {
        ratingDto = Rating.createDto(ratingDao.put(Rating.create(ratingDto)));

        return new GetResult<RatingDto>(ratingDto);
    }

    @Path(PathParameter.ID)
    @DELETE
    public NoResult delete(@PathParam(RestParameter.ID) Long id) {
        ratingDao.delete(id);
        return new NoResult();
    }
}
