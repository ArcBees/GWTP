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

package com.gwtplatform.processors.tools;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.Outputter;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.gwtplatform.processors.tools.GwtSourceFilter.GWTP_MODULE_OPTION;
import static com.gwtplatform.processors.tools.logger.Logger.DEBUG_OPTION;

public abstract class AbstractProcessor extends javax.annotation.processing.AbstractProcessor {
    private static final String UNRESOLVABLE_EXCEPTION = "Unresolvable exception.";

    protected Logger logger;
    protected Utils utils;
    protected Outputter outputter;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_11;
    }

    @Override
    public Set<String> getSupportedOptions() {
        Set<String> supportedOptions = new HashSet<>(super.getSupportedOptions());
        supportedOptions.add(DEBUG_OPTION);
        supportedOptions.add(GWTP_MODULE_OPTION);
        return supportedOptions;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        SupportedAnnotationTypes supportedTypes = getClass().getAnnotation(SupportedAnnotationTypes.class);
        SupportedAnnotationClasses supportedClasses = getClass().getAnnotation(SupportedAnnotationClasses.class);

        Set<String> supportedAnnotations = new HashSet<>();

        if (supportedTypes != null) {
            Collections.addAll(supportedAnnotations, supportedTypes.value());
        }
        if (supportedClasses != null) {
            for (Class<? extends Annotation> classy : supportedClasses.value()) {
                supportedAnnotations.add(classy.getCanonicalName());
            }
        }

        return Collections.unmodifiableSet(supportedAnnotations);
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        try {
            Map<String, String> options = processingEnv.getOptions();
            logger = new Logger(processingEnv.getMessager(), options);
            utils = new Utils(logger, processingEnv.getTypeUtils(), processingEnv.getElementUtils(), options);
            outputter = new Outputter(logger, this, processingEnv.getFiler(), getMacroFiles());

            initSafe();
        } catch (UnableToProcessException ignore) {
        } catch (Exception e) {
            logger.error().throwable(e).log(UNRESOLVABLE_EXCEPTION);
        }
    }

    protected Set<String> getMacroFiles() {
        return Collections.emptySet();
    }

    protected void initSafe() {
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.errorRaised()) {
            return false;
        }

        try {
            utils.incrementRoundNumber();

            processSafe(annotations, roundEnv);
        } catch (UnableToProcessException ignore) {
        } catch (Exception e) {
            logger.error().throwable(e).log(UNRESOLVABLE_EXCEPTION);
        }

        return false;
    }

    protected abstract void processSafe(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv);
}
