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

package com.gwtplatform.common.processors;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.gwtplatform.common.client.annotations.GwtpApp;
import com.gwtplatform.processors.tools.AbstractProcessor;

public abstract class AbstractGwtpAppProcessor extends AbstractProcessor {
    private boolean gwtpAppPresent;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>(super.getSupportedAnnotationTypes());
        types.add(GwtpApp.class.getCanonicalName());
        return Collections.unmodifiableSet(types);
    }

    @Override
    protected void processSafe(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        loadGwtpApp(roundEnv);

        if (gwtpAppPresent) {
            processAsApp(annotations, roundEnv);
        } else {
            processAsModule(annotations, roundEnv);
        }
    }

    private void loadGwtpApp(RoundEnvironment roundEnv) {
        if (!gwtpAppPresent) {
            Set<? extends Element> annotations = roundEnv.getElementsAnnotatedWith(GwtpApp.class);
            annotations = utils.getSourceFilter().filterElements(annotations);

            gwtpAppPresent = !annotations.isEmpty();
        }
    }

    protected abstract void processAsApp(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv);

    protected abstract void processAsModule(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv);
}
