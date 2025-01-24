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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;
import com.gwtplatform.mvp.client.annotations.GatekeeperParams;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.GatekeeperWithParams;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.google.auto.common.AnnotationMirrors.getAnnotationValue;
import static com.google.auto.common.MoreElements.getAnnotationMirror;
import static com.google.common.base.Optional.of;

public class ProxyPlaceDetails extends AbstractProxyDetails {
    private Set<String> nameTokens;
    private Boolean defaultGatekeeper;
    private Optional<Type> gatekeeperType;
    private List<String> gatekeeperParams;

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

    public boolean usesDefaultGatekeeper() {
        if (defaultGatekeeper == null) {
            NoGatekeeper noGatekeeper = element.getAnnotation(NoGatekeeper.class);
            defaultGatekeeper = noGatekeeper == null && getGatekeeperType() == null;
        }

        return defaultGatekeeper;
    }

    public Type getGatekeeperType() {
        if (gatekeeperType == null) {
            TypeMirror mirror = extractGatekeeperMirror();
            gatekeeperType = mirror == null ? Optional.<Type>absent() : of(new Type(mirror));
        }

        return gatekeeperType.orNull();
    }

    private TypeMirror extractGatekeeperMirror() {
        Optional<AnnotationMirror> annotationMirror = getAnnotationMirror(element, UseGatekeeper.class);

        if (annotationMirror.isPresent()) {
            AnnotationValue value = getAnnotationValue(annotationMirror.get(), "value");

            return value.accept(new SimpleAnnotationValueVisitor8<TypeMirror, Void>(null) {
                @Override
                public TypeMirror visitType(TypeMirror typeMirror, Void o) {
                    return typeMirror;
                }
            }, null);
        }

        return null;
    }

    public List<String> getGatekeeperParams() {
        if (gatekeeperParams == null) {
            extractGatekeeperParams();
        }

        return gatekeeperParams;
    }

    private void extractGatekeeperParams() {
        GatekeeperParams annotation = element.getAnnotation(GatekeeperParams.class);
        gatekeeperParams = new ArrayList<>();

        if (annotation != null && verifyIsGatekeeperWithParams()) {
            FluentIterable.from(annotation.value())
                    .transform(param -> param
                            .replace("\\", "\\\\")
                            .replace("\"", "\\\""))
                    .copyInto(gatekeeperParams);
        }
    }

    private boolean verifyIsGatekeeperWithParams() {
        TypeMirror gatekeeperMirror = extractGatekeeperMirror();
        TypeMirror expectedParentMirror = utils.getElements()
                .getTypeElement(GatekeeperWithParams.class.getCanonicalName())
                .asType();

        if (gatekeeperMirror == null
                || !utils.getTypes().isSubtype(gatekeeperMirror, expectedParentMirror)) {
            logger.mandatoryWarning()
                    .context(element)
                    .log("Proxy annotated with @GatekeeperParams with missing or invalid gatekeeper argument. "
                            + "A gatekeeper that implements GatekeeperWithParams must be provided to @UseGatekeeper.");
            return false;
        }

        return true;
    }

    @Override
    public Collection<String> getImports() {
        Collection<String> imports = new ArrayList<>(super.getImports());
        if (!usesDefaultGatekeeper() && getGatekeeperType() != null) {
            imports.add(getGatekeeperType().getQualifiedName());
        }

        return imports;
    }
}
