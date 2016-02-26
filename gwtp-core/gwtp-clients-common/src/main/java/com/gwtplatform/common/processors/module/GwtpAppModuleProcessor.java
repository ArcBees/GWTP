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

package com.gwtplatform.common.processors.module;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;
import com.google.common.collect.Sets;
import com.gwtplatform.common.client.annotations.GwtpApp;
import com.gwtplatform.common.client.annotations.GwtpModule;
import com.gwtplatform.processors.tools.GwtSourceFilter;
import com.gwtplatform.processors.tools.bindings.BindingsProcessors;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.Outputter;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.google.auto.common.MoreElements.asType;
import static com.gwtplatform.processors.tools.bindings.BindingContext.newModule;
import static com.gwtplatform.processors.tools.bindings.BindingContext.newSubModule;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions({Logger.DEBUG_OPTION, GwtSourceFilter.GWTP_MODULE_OPTION})
public class GwtpAppModuleProcessor extends AbstractProcessor {
    public static final Type MAIN_MODULE_TYPE = new Type("com.gwtplatform.common.client.GeneratedGwtpModule");

    private Logger logger;
    private Utils utils;

    private BindingsProcessors bindingsProcessors;
    private MetaInfModuleHandler metaInfModuleHandler;

    private boolean isGwtpApp;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        Map<String, String> options = processingEnv.getOptions();

        logger = new Logger(processingEnv.getMessager(), options);
        utils = new Utils(logger, processingEnv.getTypeUtils(), processingEnv.getElementUtils(), options);
        Outputter outputter = new Outputter(logger, this, processingEnv.getFiler());
        bindingsProcessors = new BindingsProcessors(logger, utils, outputter);
        metaInfModuleHandler = new MetaInfModuleHandler(logger, outputter);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Sets.newHashSet(GwtpApp.class.getCanonicalName(), GwtpModule.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        utils.incrementRoundNumber();

        try {
            if (!roundEnv.processingOver()) {
                process(roundEnv);
            } else {
                processLast();
            }
        } catch (UnableToProcessException e) {
            // More details should be logged before thrown
        } catch (Exception e) {
            logger.error().throwable(e).log("Unexpected error. See stack trace.");
        }

        return false;
    }

    private void process(RoundEnvironment environment) throws Exception {
        Set<? extends Element> moduleElements = environment.getElementsAnnotatedWith(GwtpModule.class);
        moduleElements = utils.getSourceFilter().filterElements(moduleElements);

        if (isGwtpApp(environment)) {
            addModulesToGwtpApp(moduleElements);
        } else {
            addModulesToMetaInf(moduleElements);
        }
    }

    private boolean isGwtpApp(RoundEnvironment environment) {
        if (!environment.getElementsAnnotatedWith(GwtpApp.class).isEmpty()) {
            isGwtpApp = true;
        }

        return isGwtpApp;
    }

    private void addModulesToGwtpApp(Set<? extends Element> moduleElements) throws Exception {
        logger.debug("Processing GWTP main module.");

        ensureModuleIsCreated();

        installModules(metaInfModuleHandler.readAll());
        installModules(moduleElements);
    }

    private void ensureModuleIsCreated() {
        bindingsProcessors.process(newModule(MAIN_MODULE_TYPE));
    }

    private void installModules(Set<? extends Element> moduleElements) {
        for (Element moduleElement : moduleElements) {
            process(new Type(moduleElement.asType()));
        }
    }

    private void installModules(List<String> modules) {
        for (String module : modules) {
            process(new Type(module));
        }
    }

    private void process(Type moduleType) {
        bindingsProcessors.process(newSubModule(MAIN_MODULE_TYPE, moduleType));
    }

    private void addModulesToMetaInf(Set<? extends Element> moduleElements) throws IOException {
        logger.debug("Processing GWTP modules meta data.");

        for (Element moduleElement : moduleElements) {
            String moduleType = asType(moduleElement).getQualifiedName().toString();

            metaInfModuleHandler.writeLine(moduleType);
        }
    }

    private void processLast() throws IOException {
        metaInfModuleHandler.closeWriter();

        if (isGwtpApp) {
            bindingsProcessors.processLast();
        }
    }
}
