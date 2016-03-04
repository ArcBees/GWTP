/*
 * Copyright 2015 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.processors.details;

import javax.lang.model.element.TypeElement;

import com.gwtplatform.processors.tools.domain.Method;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

public class EndPointDetailsFactory implements EndPointDetails.Factory {
    private final Logger logger;
    private final Utils utils;

    public EndPointDetailsFactory(
            Logger logger,
            Utils utils) {
        this.logger = logger;
        this.utils = utils;
    }

    @Override
    public EndPointDetails create(TypeElement element) {
        EndPointDetails details = new EndPointDetails(logger, utils);
        details.processElement(element);

        return details;
    }

    @Override
    public EndPointDetails create(EndPointDetails parentDetails, TypeElement element) {
        EndPointDetails details = new EndPointDetails(logger, utils, parentDetails);
        details.processElement(element);

        return details;
    }

    @Override
    public EndPointDetails create(EndPointDetails parentDetails, Method method) {
        EndPointDetails details = new EndPointDetails(logger, utils, parentDetails);
        details.processMethod(method);

        return details;
    }
}
