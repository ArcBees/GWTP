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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.gwtplatform.common.client.annotations.GwtpApp;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.Outputter;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.gwtplatform.processors.tools.GwtSourceFilter.GWTP_MODULE_OPTION;
import static com.gwtplatform.processors.tools.logger.Logger.DEBUG_OPTION;

public abstract class AbstractGwtpAppProcessor extends AbstractProcessor {
    protected Logger logger;
    protected Utils utils;
    protected Outputter outputter;

    private boolean gwtpAppPresent;

    @Override
    public Set<String> getSupportedOptions() {
        Set<String> supportedOptions = new HashSet<>(super.getSupportedOptions());
        supportedOptions.add(DEBUG_OPTION);
        supportedOptions.add(GWTP_MODULE_OPTION);
        return supportedOptions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        Map<String, String> options = processingEnv.getOptions();
        logger = new Logger(processingEnv.getMessager(), options);
        utils = new Utils(logger, processingEnv.getTypeUtils(), processingEnv.getElementUtils(), options);
        outputter = new Outputter(logger, this, processingEnv.getFiler());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.errorRaised()) {
            return false;
        }

        try {
            loadGwtpApp(roundEnv);

            if (gwtpAppPresent) {
                processAsApp(annotations, roundEnv);
            } else {
                processAsModule(annotations, roundEnv);
            }
        } catch (UnableToProcessException ignore) {
        } catch (Exception e) {
            logger.error().throwable(e).log("Unresolvable exception.");
        }

        return false;
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
