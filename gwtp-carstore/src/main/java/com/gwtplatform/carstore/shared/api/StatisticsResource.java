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

package com.gwtplatform.carstore.shared.api;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.gwtplatform.dispatch.rest.shared.DateFormat;
import com.gwtplatform.dispatch.rest.shared.RestAction;

import static com.gwtplatform.carstore.shared.api.ApiPaths.STATS;
import static com.gwtplatform.carstore.shared.api.ApiParameters.DATE;
import static com.gwtplatform.carstore.shared.api.ApiParameters.DATE_FORMAT;

@Path(STATS)
public interface StatisticsResource {
    // This method is intentionally left out as a RestAction to ensure it's properly handled.
    @GET
    RestAction<Integer> extractYearFromDate(@QueryParam(DATE) @DateFormat(DATE_FORMAT) Date date);
}
