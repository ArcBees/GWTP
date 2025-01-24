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

package com.gwtplatform.mvp.processors;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;

import com.google.auto.service.AutoService;
import com.gwtplatform.common.processors.AbstractGwtpAppProcessor;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.shared.proxy.PlaceTokenRegistry;
import com.gwtplatform.processors.tools.SupportedAnnotationClasses;
import com.gwtplatform.processors.tools.bindings.BindingsProcessors;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.utils.MetaInfResource;

import jakarta.inject.Singleton;

import static com.gwtplatform.common.processors.module.GwtpAppModuleProcessor.MAIN_MODULE_TYPE;
import static com.gwtplatform.processors.tools.bindings.BindingContext.newBinding;

@AutoService(Processor.class)
@SupportedAnnotationClasses(NameToken.class)
public class PlaceTokenRegistryProcessor extends AbstractGwtpAppProcessor {
    private static final String META_INF_FILE_NAME = "gwtp/placeTokens";
    private static final Type REGISTRY_TYPE = new Type(
            PlaceTokenRegistry.class.getPackage().getName(),
            "Generated" + PlaceTokenRegistry.class.getSimpleName()
    );
    private static final String TEMPLATE = "com/gwtplatform/mvp/processors/PlaceTokenRegistry.vm";

    private BindingsProcessors bindingsProcessors;
    private MetaInfResource metaInfResource;
    private Set<String> tokens;
    private FileObject registryFile;

    @Override
    protected void initSafe() {
        bindingsProcessors = new BindingsProcessors(logger, utils, outputter);
        metaInfResource = new MetaInfResource(logger, outputter, META_INF_FILE_NAME);
        tokens = new HashSet<>();
    }

    @Override
    protected void processAsApp(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ensureAppDataIsInitialized();
        extractNameTokens(roundEnv);

        if (roundEnv.processingOver()) {
            outputter.configure(TEMPLATE)
                    .withParam("tokens", tokens)
                    .writeTo(REGISTRY_TYPE, registryFile);
        }
    }

    @Override
    protected void processAsModule(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        extractNameTokens(roundEnv);
        flushTokensToMetaInf();

        if (roundEnv.processingOver()) {
            metaInfResource.closeWriter();
        }
    }

    private void ensureAppDataIsInitialized() {
        if (registryFile == null) {
            registryFile = outputter.prepareSourceFile(REGISTRY_TYPE);
            bindingsProcessors.process(newBinding(
                    MAIN_MODULE_TYPE,
                    new Type(PlaceTokenRegistry.class),
                    REGISTRY_TYPE,
                    Singleton.class));

            tokens.addAll(metaInfResource.readAll());
        }
    }

    private void extractNameTokens(RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(NameToken.class);
        elements = utils.getSourceFilter().filterElements(elements);

        for (Element element : elements) {
            String[] elementTokens = element.getAnnotation(NameToken.class).value();
            Collections.addAll(tokens, elementTokens);
        }
    }

    private void flushTokensToMetaInf() {
        for (String token : tokens) {
            metaInfResource.writeLine(token);
        }
        tokens.clear();
    }
}
