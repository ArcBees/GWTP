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

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

import static javax.lang.model.util.ElementFilter.methodsIn;
import static com.google.auto.common.MoreElements.asType;

public class ResourceUtils {
    private final Logger logger;
    private final Utils utils;
    private final ResourceMethodFactories resourceMethodFactories;

    public ResourceUtils(
            Logger logger,
            Utils utils) {
        this.logger = logger;
        this.utils = utils;
        this.resourceMethodFactories = new ResourceMethodFactories(logger, utils);
    }

    public TypeElement asResourceTypeElement(Element element) {
        if (element.getKind() == ElementKind.INTERFACE) {
            return asType(element);
        }

        logger.mandatoryWarning().context(element).log("Element annotated with @Path must be an interface.");
        throw new UnableToProcessException();
    }

    public List<ResourceMethod> processMethods(TypeElement element, final Resource resourceType) {
        List<Element> members = utils.getAllMembers(element, Object.class);
        List<ExecutableElement> methods = methodsIn(members);

        return FluentIterable.from(methods)
                .transform(element1 -> resourceMethodFactories.create(resourceType, element1))
                .filter(Predicates.notNull())
                .toList();
    }
}
