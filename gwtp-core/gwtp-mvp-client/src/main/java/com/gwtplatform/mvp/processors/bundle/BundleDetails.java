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

package com.gwtplatform.mvp.processors.bundle;

import java.util.Collection;
import java.util.Collections;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;

import com.google.common.base.CaseFormat;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplitBundle.NoOpProviderBundle;
import com.gwtplatform.processors.tools.domain.HasImports;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.google.auto.common.AnnotationMirrors.getAnnotationValue;
import static com.google.auto.common.MoreTypes.asTypeElement;

public class BundleDetails implements HasImports {
    private static final String BUNDLE_NAME = "GeneratedBundle";

    private final Logger logger;
    private final Utils utils;
    private final Type targetType;
    private final TypeElement proxyElement;
    private final AnnotationMirror codeSplitBundleMirror;

    private boolean initialized;
    private boolean valid = true;
    private String bundleName;
    private Type bundleType;
    private int id;

    public BundleDetails(
            Logger logger,
            Utils utils,
            Type targetType,
            TypeElement proxyElement,
            AnnotationMirror codeSplitBundleMirror) {
        this.logger = logger;
        this.utils = utils;
        this.targetType = targetType;
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

    public Type getTargetType() {
        return targetType;
    }

    public String getBundleName() {
        return bundleName;
    }

    public int getId() {
        initialize();
        return id;
    }

    public Type getBundleType() {
        initialize();
        return bundleType;
    }

    private void initialize() {
        if (initialized) {
            return;
        }

        initialized = true;
        bundleName = extractBundleValue();
        bundleType = extractBundleClass();
        id = extractBundleId();

        validateBundleConfiguration();

        if (valid && !bundleName.isEmpty()) {
            bundleType = new Type(
                    utils.getSourceFilter().getApplicationPackage(),
                    BUNDLE_NAME + "$$" + bundleName);
        }
    }

    private String extractBundleValue() {
        return getAnnotationValue(codeSplitBundleMirror, "value")
                .accept(new SimpleAnnotationValueVisitor8<String, Void>("") {
                    @Override
                    public String visitString(String value, Void v) {
                        return sanitizeBundleName(value);
                    }
                }, null);
    }

    private String sanitizeBundleName(String name) {
        String sanitized = name.trim().replaceAll("[^\\w\\d]", "_");
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, sanitized);
    }

    private int extractBundleId() {
        return getAnnotationValue(codeSplitBundleMirror, "id")
                .accept(new SimpleAnnotationValueVisitor8<Integer, Void>(-1) {
                    @Override
                    public Integer visitInt(int id, Void v) {
                        return id;
                    }
                }, null);
    }

    private Type extractBundleClass() {
        return getAnnotationValue(codeSplitBundleMirror, "bundleClass")
                .accept(new SimpleAnnotationValueVisitor8<Type, Void>() {
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
        } else if (id < 0 && bundleType != null || id > -1 && bundleType == null) {
            logger.mandatoryWarning()
                    .context(proxyElement, codeSplitBundleMirror)
                    .log("@ProxyCodeSplitBundle used with an invalid Bundle Class configuration. "
                            + "Both a Bundle Class and an ID must be specified. "
                            + "Defaulting to @ProxyStandard.");
            valid = false;
        } else {
            valid = true;
        }
    }

    @Override
    public Collection<String> getImports() {
        initialize();

        if (isValid() && isManualBundle()) {
            return bundleType.getImports();
        }
        return Collections.emptyList();
    }
}
