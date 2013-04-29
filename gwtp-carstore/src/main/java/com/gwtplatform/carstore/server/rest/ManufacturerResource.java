package com.gwtplatform.carstore.server.rest;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gwtplatform.carstore.server.dao.ManufacturerDao;
import com.gwtplatform.carstore.server.dao.domain.Manufacturer;
import com.gwtplatform.carstore.server.service.ReportService;
import com.gwtplatform.carstore.shared.dispatch.GetResult;
import com.gwtplatform.carstore.shared.dispatch.GetResults;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.carstore.shared.dto.ManufacturerRatingDto;
import com.gwtplatform.carstore.shared.rest.PathParameter;
import com.gwtplatform.carstore.shared.rest.ResourcesPath;
import com.gwtplatform.carstore.shared.rest.RestParameter;
import com.gwtplatform.dispatch.shared.NoResult;

@Path(ResourcesPath.MANUFACTURER)
@Produces(MediaType.APPLICATION_JSON)
public class ManufacturerResource {
    private final ManufacturerDao manufacturerDao;
    private final ReportService reportService;

    @Inject
    ManufacturerResource(
            ManufacturerDao manufacturerDao,
            ReportService reportService) {
        this.manufacturerDao = manufacturerDao;
        this.reportService = reportService;
    }

    @GET
    public GetResults<ManufacturerDto> getManufacturers() {
        return new GetResults<ManufacturerDto>(Manufacturer.createDto(manufacturerDao.getAll()));
    }

    @Path(PathParameter.ID)
    @GET
    public GetResult<ManufacturerDto> get(@PathParam(RestParameter.ID) Long id) {
        return new GetResult<ManufacturerDto>(Manufacturer.createDto(manufacturerDao.get(id)));
    }

    @POST
    public GetResult<ManufacturerDto> saveOrCreate(ManufacturerDto manufacturerDto) {
        manufacturerDto = Manufacturer.createDto(manufacturerDao.put(Manufacturer.create(manufacturerDto)));

        return new GetResult<ManufacturerDto>(manufacturerDto);
    }

    @Path(PathParameter.ID)
    @DELETE
    public NoResult delete(@PathParam(RestParameter.ID) Long id) {
        manufacturerDao.delete(id);
        return new NoResult();
    }

    @Path(ResourcesPath.RATING)
    @GET
    public GetResults<ManufacturerRatingDto> getAverageRatings() {
        return new GetResults<ManufacturerRatingDto>(reportService.getAverageCarRatingByManufacturer());
    }
}
