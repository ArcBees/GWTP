/**
 * Copyright 2014 ArcBees Inc.
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.google.common.base.Strings;
import com.gwtplatform.carstore.shared.rest.ResourcesPath;
import com.gwtplatform.carstore.shared.rest.RestParameter;

@Path(ResourcesPath.STATS)
public class StatisticsResource {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(RestParameter.DATE_FORMAT);

    @GET
    public Response extractYearFromDate(@QueryParam(RestParameter.DATE) String date) {
        ResponseBuilder responseBuilder;

        if (Strings.isNullOrEmpty(date)) {
            responseBuilder = Response.status(Status.BAD_REQUEST);
        } else {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateFormat.parse(date));

                responseBuilder = Response.ok(calendar.get(Calendar.YEAR));
            } catch (ParseException e) {
                responseBuilder = Response.status(Status.BAD_REQUEST);
            }
        }

        return responseBuilder.build();
    }
}
