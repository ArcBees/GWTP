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

import java.util.List;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;
import com.gwtplatform.common.client.annotations.GwtpModule;
import com.gwtplatform.common.processors.AbstractGwtpAppProcessor;
import com.gwtplatform.processors.tools.SupportedAnnotationClasses;
import com.gwtplatform.processors.tools.bindings.BindingsProcessors;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.utils.MetaInfResource;

import static com.google.auto.common.MoreElements.asType;
import static com.gwtplatform.processors.tools.bindings.BindingContext.flushModule;
import static com.gwtplatform.processors.tools.bindings.BindingContext.newModule;
import static com.gwtplatform.processors.tools.bindings.BindingContext.newSubModule;

@AutoService(Processor.class)
@SupportedAnnotationClasses(GwtpModule.class)
public class GwtpAppModuleProcessor extends AbstractGwtpAppProcessor {
    public static final Type MAIN_MODULE_TYPE = new Type("com.gwtplatform.common.client.GeneratedGwtpModule");
    private static final String GIN_MODULES_META_INF_NAME = "gwtp/ginModules";

    private BindingsProcessors bindingsProcessors;
    private MetaInfResource ginModulesMetaData;

    private boolean moduleCreated;

    @Override
    protected void initSafe() {
        bindingsProcessors = new BindingsProcessors(logger, utils, outputter);
        ginModulesMetaData = new MetaInfResource(logger, outputter, GIN_MODULES_META_INF_NAME);
    }

    @Override
    protected void processSafe(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        super.processSafe(annotations, roundEnv);

        if (roundEnv.processingOver()) {
            ginModulesMetaData.closeWriter();
        }
    }

    @Override
    protected void processAsApp(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        logger.debug("Processing GWTP main module.");

        ensureModuleIsCreated();
        installModules(extractModules(roundEnv));

        if (roundEnv.processingOver()) {
            bindingsProcessors.process(flushModule(MAIN_MODULE_TYPE));
        }
    }

    private void ensureModuleIsCreated() {
        if (!moduleCreated) {
            bindingsProcessors.process(newModule(MAIN_MODULE_TYPE));
            installModules(ginModulesMetaData.readAll());

            moduleCreated = true;
        }
    }

    private void installModules(List<String> modules) {
        for (String module : modules) {
            createBinding(new Type(module));
        }
    }

    private void installModules(Set<? extends Element> moduleElements) {
        for (Element moduleElement : moduleElements) {
            createBinding(new Type(moduleElement.asType()));
        }
    }

    private void createBinding(Type moduleType) {
        bindingsProcessors.process(newSubModule(MAIN_MODULE_TYPE, moduleType));
    }

    @Override
    protected void processAsModule(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        addModulesToMetaInf(extractModules(roundEnv));
    }

    private Set<? extends Element> extractModules(RoundEnvironment roundEnv) {
        Set<? extends Element> moduleElements = roundEnv.getElementsAnnotatedWith(GwtpModule.class);
        return utils.getSourceFilter().filterElements(moduleElements);
    }

    private void addModulesToMetaInf(Set<? extends Element> moduleElements) {
        logger.debug("Processing GWTP modules meta data.");

        for (Element moduleElement : moduleElements) {
            String moduleType = asType(moduleElement).getQualifiedName().toString();

            ginModulesMetaData.writeLine(moduleType);
        }
    }
}
