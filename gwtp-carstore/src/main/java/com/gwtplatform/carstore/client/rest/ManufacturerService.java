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

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.carstore.shared.dto.ManufacturerRatingDto;
import com.gwtplatform.carstore.shared.rest.PathParameter;
import com.gwtplatform.carstore.shared.rest.ResourcesPath;
import com.gwtplatform.carstore.shared.rest.RestParameter;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.rest.shared.RestService;

@Path(ResourcesPath.MANUFACTURER)
public interface ManufacturerService extends RestService {
    @GET
    RestAction<List<ManufacturerDto>> getManufacturers();

    @GET
    @Path(PathParameter.ID)
    RestAction<ManufacturerDto> get(@PathParam(RestParameter.ID) Long id);

    @POST
    RestAction<ManufacturerDto> saveOrCreate(ManufacturerDto manufacturerDto);

    @DELETE
    @Path(PathParameter.ID)
    RestAction<Void> delete(@PathParam(RestParameter.ID) Long id);

    @GET
    @Path(ResourcesPath.RATING)
    RestAction<List<ManufacturerRatingDto>> getAverageRatings();
}
