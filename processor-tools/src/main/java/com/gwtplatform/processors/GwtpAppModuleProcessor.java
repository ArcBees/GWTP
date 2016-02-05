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

package com.gwtplatform.processors;

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

import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import com.google.common.collect.Sets;
import com.gwtplatform.common.client.GwtpApp;
import com.gwtplatform.common.client.GwtpModule;
import com.gwtplatform.processors.tools.bindings.BindingsProcessors;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.Outputter;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.gwtplatform.processors.tools.GwtSourceFilter.GWTP_MODULE_OPTION;
import static com.gwtplatform.processors.tools.bindings.BindingContext.newModule;
import static com.gwtplatform.processors.tools.bindings.BindingContext.newSubModule;
import static com.gwtplatform.processors.tools.logger.Logger.DEBUG_OPTION;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions({DEBUG_OPTION, GWTP_MODULE_OPTION})
public class GwtpAppModuleProcessor extends AbstractProcessor {
    private static final Type MAIN_MODULE_TYPE = new Type(GwtpApp.class.getPackage().getName(), "GeneratedGwtpModule");

    private Logger logger;

    private BindingsProcessors bindingsProcessors;
    private MetaInfModuleHandler metaInfModuleHandler;

    private boolean isGwtpApp;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        Map<String, String> options = processingEnv.getOptions();

        logger = new Logger(processingEnv.getMessager(), options);
        Utils utils = new Utils(logger, processingEnv.getTypeUtils(), processingEnv.getElementUtils(), options);
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
        try {
            if (!roundEnv.processingOver()) {
                doProcess(roundEnv);
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

    private void doProcess(RoundEnvironment environment) throws Exception {
        if (isGwtpApp(environment)) {
            logger.debug("Processing GWTP application.");

            ensureModuleIsCreated();
            installModules(metaInfModuleHandler.readAll());
        } else {
            logger.debug("Processing GWTP meta data.");

            addModulesToMetaInf(environment);
        }
    }

    private boolean isGwtpApp(RoundEnvironment environment) {
        if (!environment.getElementsAnnotatedWith(GwtpApp.class).isEmpty()) {
            isGwtpApp = true;
        }

        return isGwtpApp;
    }

    private void ensureModuleIsCreated() {
        bindingsProcessors.process(newModule(MAIN_MODULE_TYPE));
    }

    private void installModules(List<String> modules) {
        for (String module : modules) {
            bindingsProcessors.process(newSubModule(MAIN_MODULE_TYPE, new Type(module)));
        }
    }

    private void addModulesToMetaInf(RoundEnvironment environment) throws IOException {
        Set<? extends Element> moduleElements = environment.getElementsAnnotatedWith(GwtpModule.class);

        for (Element moduleElement : moduleElements) {
            String moduleType = MoreElements.asType(moduleElement).getQualifiedName().toString();

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
