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

import com.gwtplatform.carstore.shared.dto.RatingDto;
import com.gwtplatform.dispatch.rest.shared.RestAction;

import static com.gwtplatform.carstore.shared.rest.PathParameter.PATH_ID;
import static com.gwtplatform.carstore.shared.rest.ResourcesPath.RATING;
import static com.gwtplatform.carstore.shared.rest.RestParameter.ID;

@Path(RATING)
public interface RatingService {
    @GET
    RestAction<List<RatingDto>> getRatings();

    @GET
    @Path(PATH_ID)
    RestAction<RatingDto> get(@PathParam(ID) Long id);

    @POST
    RestAction<RatingDto> saveOrCreate(RatingDto RatingDto);

    @DELETE
    @Path(PATH_ID)
    RestAction<Void> delete(@PathParam(ID) Long id);
}
