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

package com.gwtplatform.dispatch.rest.processors.resource;

import java.util.ServiceLoader;

import javax.lang.model.element.ExecutableElement;

import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

public class ResourceMethodFactories {
    private static final String NO_FACTORIES_FOUND = "Can not find a factory to handle the resource method.";

    private static ServiceLoader<ResourceMethodFactory> factories;

    private final Logger logger;
    private final Utils utils;

    public ResourceMethodFactories(
            Logger logger,
            Utils utils) {
        this.logger = logger;
        this.utils = utils;

        if (factories == null) {
            factories = ServiceLoader.load(ResourceMethodFactory.class, getClass().getClassLoader());
        }
    }

    public ResourceMethod resolve(Resource resourceType, ExecutableElement element) {
        for (ResourceMethodFactory factory : factories) {
            if (factory.canHandle(element)) {
                return factory.resolve(logger, utils, resourceType, element);
            }
        }

        logger.error().context(element).log(NO_FACTORIES_FOUND);
        throw new UnableToProcessException();
    }
}
