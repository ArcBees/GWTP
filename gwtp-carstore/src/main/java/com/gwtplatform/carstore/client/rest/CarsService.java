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

package com.gwtplatform.carstore.client.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.rest.shared.RestService;

import static com.gwtplatform.carstore.shared.rest.PathParameter.PATH_ID;
import static com.gwtplatform.carstore.shared.rest.ResourcesPath.CARS;
import static com.gwtplatform.carstore.shared.rest.ResourcesPath.COUNT;
import static com.gwtplatform.carstore.shared.rest.RestParameter.ID;
import static com.gwtplatform.carstore.shared.rest.RestParameter.LIMIT;
import static com.gwtplatform.carstore.shared.rest.RestParameter.OFFSET;

@Path(CARS)
public interface CarsService extends RestService {
    @GET
    RestAction<List<CarDto>> getCars();

    @GET
    RestAction<List<CarDto>> getCars(@QueryParam(OFFSET) int offset, @QueryParam(LIMIT) int limit);

    @GET
    @Path(COUNT)
    RestAction<Integer> getCarsCount();

    @POST
    RestAction<CarDto> saveOrCreate(CarDto carDto);

    @Path(PATH_ID)
    CarService car(@PathParam(ID) Long carId);
}
