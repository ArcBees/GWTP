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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor7;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.google.auto.common.AnnotationMirrors.getAnnotationValue;
import static com.google.auto.common.MoreElements.getAnnotationMirror;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class ProxyPlaceDetails extends AbstractProxyDetails {
    private Set<String> nameTokens;
    private Optional<Type> gatekeeperType;

    public ProxyPlaceDetails(
            Logger logger,
            Utils utils,
            TypeElement element,
            TypeMirror proxyMirror) {
        super(logger, utils, element, proxyMirror);
    }

    public Set<String> getNameTokens() {
        if (nameTokens == null) {
            NameToken annotation = element.getAnnotation(NameToken.class);
            if (annotation == null) {
                nameTokens = Collections.emptySet();
            } else {
                nameTokens = Sets.newHashSet(annotation.value());
            }

            if (nameTokens.isEmpty()) {
                warnNoNameTokens();
            }
        }

        return nameTokens;
    }

    private void warnNoNameTokens() {
        logger.mandatoryWarning()
                .context(element)
                .log("You must annotate and specify at least one name token on a ProxyPlace<>.");
    }

    public Type getGatekeeperType() {
        if (gatekeeperType == null) {
            extractGatekeeper();
        }

        // TODO: Handle @NoGatekeeper when the @DefaultGatekeeper is handled.

        return gatekeeperType.orNull();
    }

    private void extractGatekeeper() {
        Optional<AnnotationMirror> annotationMirror = getAnnotationMirror(element, UseGatekeeper.class);
        gatekeeperType = absent();

        if (annotationMirror.isPresent()) {
            AnnotationValue value = getAnnotationValue(annotationMirror.get(), "value");

            gatekeeperType = value.accept(new SimpleAnnotationValueVisitor7<Optional<Type>, Object>(gatekeeperType) {
                @Override
                public Optional<Type> visitType(TypeMirror typeMirror, Object o) {
                    return of(new Type(typeMirror));
                }
            }, null);
        }
    }

    public List<String> getGatekeeperParams() {
        // TODO: Handle gatekeeper with params
        return new ArrayList<>();
    }
}
