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

package com.gwtplatform.carstore.shared.api;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.gwtplatform.carstore.shared.dto.CarDto;

import static com.gwtplatform.carstore.shared.api.ApiPaths.PATH_ID;
import static com.gwtplatform.carstore.shared.api.ApiPaths.CARS;
import static com.gwtplatform.carstore.shared.api.ApiPaths.COUNT;
import static com.gwtplatform.carstore.shared.api.ApiParameters.ID;
import static com.gwtplatform.carstore.shared.api.ApiParameters.LIMIT;
import static com.gwtplatform.carstore.shared.api.ApiParameters.OFFSET;

@Path(CARS)
@Produces(MediaType.APPLICATION_JSON)
public interface CarsResource {
    String DEFAULT_LIMIT = "1000";
    String DEFAULT_OFFSET = "0";

    @GET
    List<CarDto> getCars();

    @GET
    List<CarDto> getCars(@DefaultValue(DEFAULT_OFFSET) @QueryParam(OFFSET) int offset,
            @DefaultValue(DEFAULT_LIMIT) @QueryParam(LIMIT) int limit);

    @GET
    @Path(COUNT)
    int getCarsCount();

    @POST
    CarDto saveOrCreate(CarDto carDto);

    @Path(PATH_ID)
    CarResource car(@PathParam(ID) Long carId);
}
