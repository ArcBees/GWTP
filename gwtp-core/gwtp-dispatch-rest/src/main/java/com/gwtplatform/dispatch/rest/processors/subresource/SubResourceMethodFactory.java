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

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;

import com.google.auto.service.AutoService;
import com.gwtplatform.dispatch.rest.processors.details.EndPointDetails;
import com.gwtplatform.dispatch.rest.processors.details.EndPointDetailsFactory;
import com.gwtplatform.dispatch.rest.processors.resolvers.HttpVerbResolver;
import com.gwtplatform.dispatch.rest.processors.resource.Resource;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethodFactory;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethodUtils;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.google.auto.common.MoreTypes.asTypeElement;

@AutoService(ResourceMethodFactory.class)
public class SubResourceMethodFactory implements ResourceMethodFactory<SubResourceMethod> {
    private ResourceMethodUtils resourceMethodUtils;
    private EndPointDetails.Factory endPointDetailsFactory;
    private SubResource.Factory subResourceFactory;

    /**
     * Explicit constructor required by the Service Loader.
     */
    public SubResourceMethodFactory() {
    }

    @Override
    public void init(Logger logger, Utils utils) {
        resourceMethodUtils = new ResourceMethodUtils();
        endPointDetailsFactory = new EndPointDetailsFactory(logger, utils);
        subResourceFactory = new SubResourceFactory(logger, utils);
    }

    @Override
    public boolean canCreate(ExecutableElement element) {
        return !HttpVerbResolver.isPresent(element)
                && asTypeElement(element.getReturnType()).getKind() == ElementKind.INTERFACE;
    }

    @Override
    public SubResourceMethod create(Resource parentResource, ExecutableElement element) {
        return new SubResourceMethod(resourceMethodUtils, endPointDetailsFactory, subResourceFactory, parentResource,
                element);
    }
}
