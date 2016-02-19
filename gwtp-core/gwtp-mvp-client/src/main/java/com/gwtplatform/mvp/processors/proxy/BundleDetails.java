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

import java.util.Collection;
import java.util.Collections;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor7;

import com.gwtplatform.mvp.client.annotations.ProxyCodeSplitBundle.NoOpProviderBundle;
import com.gwtplatform.processors.tools.domain.HasImports;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;

import static com.google.auto.common.AnnotationMirrors.getAnnotationValue;
import static com.google.auto.common.MoreTypes.asTypeElement;

public class BundleDetails implements HasImports {
    private final Logger logger;
    private final TypeElement proxyElement;
    private final AnnotationMirror codeSplitBundleMirror;

    private boolean valid = true;
    private boolean initialized;
    private String bundleName;
    private Type bundleType;
    private int id;

    public BundleDetails(
            Logger logger,
            TypeElement proxyElement,
            AnnotationMirror codeSplitBundleMirror) {
        this.logger = logger;
        this.proxyElement = proxyElement;
        this.codeSplitBundleMirror = codeSplitBundleMirror;
    }

    public boolean isManualBundle() {
        initialize();
        return valid && bundleName.isEmpty();
    }

    public boolean isValid() {
        initialize();
        return valid;
    }

    public int getId() {
        initialize();
        return id;
    }

    public Type getBundleType() {
        initialize();
        return bundleType;
    }

    public String getBundleName() {
        initialize();
        return bundleName;
    }

    private void initialize() {
        if (initialized) {
            return;
        }

        bundleName = extractBundleValue();
        bundleType = extractBundleClass();
        id = extractBundleId();
        initialized = true;

        validateBundleConfiguration();
    }

    private String extractBundleValue() {
        return getAnnotationValue(codeSplitBundleMirror, "value")
                .accept(new SimpleAnnotationValueVisitor7<String, Void>("") {
                    @Override
                    public String visitString(String value, Void v) {
                        return value.trim();
                    }
                }, null);
    }

    private int extractBundleId() {
        return getAnnotationValue(codeSplitBundleMirror, "id")
                .accept(new SimpleAnnotationValueVisitor7<Integer, Void>(-1) {
                    @Override
                    public Integer visitInt(int id, Void v) {
                        return id;
                    }
                }, null);
    }

    private Type extractBundleClass() {
        return getAnnotationValue(codeSplitBundleMirror, "bundleClass")
                .accept(new SimpleAnnotationValueVisitor7<Type, Void>() {
                    @Override
                    public Type visitType(TypeMirror bundleClass, Void v) {
                        boolean isNotSpecified = asTypeElement(bundleClass).getQualifiedName()
                                .contentEquals(NoOpProviderBundle.class.getCanonicalName());
                        return isNotSpecified ? null : new Type(bundleClass);
                    }
                }, null);
    }

    private void validateBundleConfiguration() {
        if (!bundleName.isEmpty() && (id > -1 || bundleType != null)) {
            logger.mandatoryWarning()
                    .context(proxyElement, codeSplitBundleMirror)
                    .log("@ProxyCodeSplitBundle used with both a bundle name and a bundle class. "
                            + "Only one or the other can be used at once. "
                            + "Defaulting to @ProxyStandard.");
            valid = false;
        } else if (bundleName.isEmpty() && id < 0 && bundleType == null) {
            logger.mandatoryWarning()
                    .context(proxyElement, codeSplitBundleMirror)
                    .log("@ProxyCodeSplitBundle used with no configurations. "
                            + "Either specify a bundle name or a Bundle Class with an ID. "
                            + "Defaulting to @ProxyStandard.");
            valid = false;
        } else if ((id < 0 && bundleType != null) || (id > -1 && bundleType == null)) {
            logger.mandatoryWarning()
                    .context(proxyElement, codeSplitBundleMirror)
                    .log("@ProxyCodeSplitBundle used with an invalid Bundle Class configuration. "
                            + "Both a Bundle Class and an ID must be specified. "
                            + "Defaulting to @ProxyStandard.");
            valid = false;
        } else if (!bundleName.isEmpty()) {
            // TODO: Remove this check and handle generated bundles.
            logger.error()
                    .context(proxyElement, codeSplitBundleMirror)
                    .log("Generated bundles are not implemented yet.");
            throw new UnableToProcessException();
        }

        valid = true;
    }

    @Override
    public Collection<String> getImports() {
        if (bundleType != null) {
            return bundleType.getImports();
        }
        return Collections.emptyList();
    }
}
