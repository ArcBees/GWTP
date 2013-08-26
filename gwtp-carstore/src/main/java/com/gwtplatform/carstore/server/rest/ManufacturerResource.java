/**
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.carstore.server.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.gwtplatform.carstore.server.dao.ManufacturerDao;
import com.gwtplatform.carstore.server.dao.RatingDao;
import com.gwtplatform.carstore.server.dao.domain.Manufacturer;
import com.gwtplatform.carstore.server.dao.domain.Rating;
import com.gwtplatform.carstore.server.service.ReportService;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.carstore.shared.dto.ManufacturerRatingDto;
import com.gwtplatform.carstore.shared.dto.RatingDto;
import com.gwtplatform.carstore.shared.rest.PathParameter;
import com.gwtplatform.carstore.shared.rest.ResourcesPath;
import com.gwtplatform.carstore.shared.rest.RestParameter;

@Path(ResourcesPath.MANUFACTURER)
@Produces(MediaType.APPLICATION_JSON)
public class ManufacturerResource {
    private final ManufacturerDao manufacturerDao;
    private final RatingDao ratingDao;
    private final ReportService reportService;

    @Inject
    ManufacturerResource(ManufacturerDao manufacturerDao,
                         RatingDao ratingDao,
                         ReportService reportService) {
        this.manufacturerDao = manufacturerDao;
        this.ratingDao = ratingDao;
        this.reportService = reportService;
    }

    @GET
    public Response getManufacturers() {
        List<ManufacturerDto> manufacturerDtos = Manufacturer.createDto(manufacturerDao.getAll());

        return Response.ok(manufacturerDtos).build();
    }

    @Path(PathParameter.ID)
    @GET
    public Response get(@PathParam(RestParameter.ID) Long id) {
        ManufacturerDto manufacturerDto = Manufacturer.createDto(manufacturerDao.get(id));

        return Response.ok(manufacturerDto).build();
    }

    @POST
    public Response saveOrCreate(ManufacturerDto manufacturerDto) {
        Manufacturer manufacturer = manufacturerDao.put(Manufacturer.create(manufacturerDto));

        return Response.ok(Manufacturer.createDto(manufacturer)).build();
    }

    @Path(PathParameter.ID)
    @DELETE
    public Response delete(@PathParam(RestParameter.ID) Long id) {
        manufacturerDao.delete(id);

        return Response.ok().build();
    }

    @Path(ResourcesPath.RATING)
    @GET
    public Response getAverageRatings() {
        List<RatingDto> ratingDtos = Rating.createDto(ratingDao.getAll());
        List<ManufacturerRatingDto> averageCarRatings = reportService.getAverageCarRatings(ratingDtos);

        return Response.ok(averageCarRatings).build();
    }
}
