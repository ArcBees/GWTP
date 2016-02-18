/*
 * Copyright 2016 ArcBees Inc.
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

package com.gwtplatform.mvp.processors.proxy;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.processors.proxy.ProxyDetails.Factory;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.google.auto.common.MoreElements.asType;
import static com.google.auto.common.MoreElements.hasModifiers;
import static com.google.auto.common.MoreTypes.asTypeElement;

public class ProxyDetailsFactory implements Factory {
    private final Logger logger;
    private final Utils utils;

    public ProxyDetailsFactory(
            Logger logger,
            Utils utils) {
        this.logger = logger;
        this.utils = utils;
    }

    @Override
    public ProxyDetails create(Element element) {
        validateModifiers(element);

        TypeElement typeElement = asType(element);
        List<? extends TypeMirror> parents = typeElement.getInterfaces();
        TypeMirror proxyPlaceType = null;
        TypeMirror proxyType = null;

        for (TypeMirror parentMirror : parents) {
            TypeElement parentElement = asTypeElement(parentMirror);
            Name parentName = parentElement.getQualifiedName();

            if (parentName.contentEquals(ProxyPlace.class.getCanonicalName())) {
                proxyPlaceType = parentMirror;
            } else if (parentName.contentEquals(Proxy.class.getCanonicalName())) {
                proxyType = parentMirror;
            }
        }

        if (proxyPlaceType != null) {
            return new ProxyPlaceDetails(logger, utils, typeElement, proxyPlaceType);
        } else if (proxyType != null) {
            return new SimpleProxyDetails(logger, utils, typeElement, proxyType);
        }

        logger.error().context(element).log("Proxies must extend 'Proxy<?>' or 'ProxyPlace<?>'.");
        throw new UnableToProcessException();
    }

    private void validateModifiers(Element element) {
        if (element.getKind() != ElementKind.INTERFACE) {
            logger.error().context(element).log("Proxies must be interfaces.");
            throw new UnableToProcessException();
        } else if (hasModifiers(Modifier.PRIVATE).apply(element)) {
            logger.error().context(element).log("Proxies must not be private. Package-local is recommended.");
            throw new UnableToProcessException();
        }
    }
}
