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

package com.gwtplatform.mvp.processors.entrypoint;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.inject.Singleton;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;
import com.google.common.collect.Sets;
import com.gwtplatform.common.client.annotations.GwtpApp;
import com.gwtplatform.mvp.client.Bootstrapper;
import com.gwtplatform.processors.tools.bindings.BindingsProcessors;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.Outputter;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.google.auto.common.MoreElements.asType;
import static com.gwtplatform.common.processors.module.GwtpAppModuleProcessor.MAIN_MODULE_TYPE;
import static com.gwtplatform.processors.tools.GwtSourceFilter.GWTP_MODULE_OPTION;
import static com.gwtplatform.processors.tools.bindings.BindingContext.newBinding;
import static com.gwtplatform.processors.tools.logger.Logger.DEBUG_OPTION;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions({DEBUG_OPTION, GWTP_MODULE_OPTION})
public class EntryPointProcessor extends AbstractProcessor {
    private static final String MULTIPLE_GWTP_APP =
            "More that one class annotated with @GwtpApp. Only one can exists, unexpected results can be observed";
    private static final String APPLICATION_CONTROLLER_TEMPLATE =
            "com/gwtplatform/mvp/processors/entrypoint/EntryPoint.vm";

    private Logger logger;
    private Utils utils;

    private boolean generated;
    private Outputter outputter;
    private BindingsProcessors bindingsProcessors;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Sets.newHashSet(GwtpApp.class.getCanonicalName());
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        Map<String, String> options = processingEnv.getOptions();
        logger = new Logger(processingEnv.getMessager(), options);
        utils = new Utils(logger, processingEnv.getTypeUtils(), processingEnv.getElementUtils(), options);
        outputter = new Outputter(logger, this, processingEnv.getFiler());
        bindingsProcessors = new BindingsProcessors(logger, utils, outputter);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            process(roundEnv);
        } catch (UnableToProcessException ignore) {
        } catch (Exception e) {
            logger.error().throwable(e).log("Unresolvable exception.");
        }

        return false;
    }

    private void process(RoundEnvironment roundEnv) {
        TypeElement gwtpApp = findGwtpAppElement(roundEnv);

        if (gwtpApp != null) {
            EntryPoint entryPoint = new EntryPoint(logger, gwtpApp);

            outputter.configure(APPLICATION_CONTROLLER_TEMPLATE)
                    .withParam("entryPoint", entryPoint)
                    .writeTo(entryPoint.getType());
            bindingsProcessors.process(newBinding(
                    MAIN_MODULE_TYPE,
                    new Type(Bootstrapper.class),
                    entryPoint.getBootstrapper(),
                    Singleton.class));

            generated = true;
        }
    }

    private TypeElement findGwtpAppElement(RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(GwtpApp.class);
        elements = utils.getSourceFilter().filterElements(elements);

        if (!elements.isEmpty()) {
            Iterator<? extends Element> gwtpApps = elements.iterator();
            Element gwtpApp = gwtpApps.next();

            if (generated) {
                warnMultipleGwtpApp(gwtpApp);
            } else {
                if (gwtpApps.hasNext()) {
                    warnMultipleGwtpApp(gwtpApps.next());
                }

                return asType(gwtpApp);
            }
        }

        return null;
    }

    private void warnMultipleGwtpApp(Element element) {
        logger.mandatoryWarning().context(element).log(MULTIPLE_GWTP_APP);
    }
}
