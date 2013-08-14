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

package com.gwtplatform.carstore.client.application.testutils;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.google.inject.TypeLiteral;
import com.gwtplatform.carstore.client.rest.CarService;
import com.gwtplatform.carstore.shared.dispatch.GetResult;
import com.gwtplatform.carstore.shared.dispatch.GetResults;
import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.carstore.shared.dto.NumberDto;
import com.gwtplatform.carstore.shared.rest.RestParameter;
import com.gwtplatform.dispatch.shared.NoResult;
import com.gwtplatform.dispatch.shared.rest.RestAction;

public class CarServiceImpl implements CarService {
    @Override
    public RestAction<GetResults<CarDto>> getCars() {
        return new ActionImpl<GetResults<CarDto>>(new TypeLiteral<RestAction<GetResults<CarDto>>>() {});
    }

    @Override
    public RestAction<GetResults<CarDto>> getCars(@QueryParam(RestParameter.OFFSET) int offset,
                                                  @QueryParam(RestParameter.LIMIT) int limit) {
        return new ActionImpl<GetResults<CarDto>>(new TypeLiteral<RestAction<GetResults<CarDto>>>() {});
    }

    @Override
    public RestAction<GetResult<NumberDto<Integer>>> getCarsCount() {
        return new ActionImpl<GetResult<NumberDto<Integer>>>(
                new TypeLiteral<RestAction<GetResult<NumberDto<Integer>>>>() {});
    }

    @Override
    public RestAction<GetResult<CarDto>> saveOrCreate(CarDto carDto) {
        return new ActionImpl<GetResult<CarDto>>(new TypeLiteral<RestAction<GetResult<CarDto>>>() {});
    }

    @Override
    public RestAction<NoResult> delete(@PathParam(RestParameter.ID) Long carId) {
        return new ActionImpl<NoResult>(new TypeLiteral<RestAction<NoResult>>() {});
    }
}
