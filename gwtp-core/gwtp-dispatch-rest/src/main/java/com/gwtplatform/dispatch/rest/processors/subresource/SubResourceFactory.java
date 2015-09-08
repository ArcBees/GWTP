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

package com.gwtplatform.dispatch.rest.processors.subresource;

import javax.lang.model.element.TypeElement;

import com.gwtplatform.dispatch.rest.processors.details.EndPointDetails;
import com.gwtplatform.dispatch.rest.processors.details.EndPointDetailsFactory;
import com.gwtplatform.dispatch.rest.processors.endpoint.EndPointUtils;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceUtils;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

public class SubResourceFactory implements SubResource.Factory {
    private final ResourceUtils resourceUtils;
    private final EndPointUtils endPointUtils;
    private final EndPointDetails.Factory endPointDetailsFactory;

    public SubResourceFactory(
            Logger logger,
            Utils utils) {
        this.resourceUtils = new ResourceUtils(logger, utils);
        this.endPointUtils = new EndPointUtils();
        this.endPointDetailsFactory = new EndPointDetailsFactory(logger, utils);
    }

    @Override
    public SubResource create(SubResourceMethod method, TypeElement element) {
        return new SubResource(resourceUtils, endPointUtils, endPointDetailsFactory, method, element);
    }
}
