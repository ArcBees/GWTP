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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import com.gwtplatform.common.processors.AbstractGwtpAppProcessor;
import com.gwtplatform.mvp.client.annotations.DefaultGatekeeper;
import com.gwtplatform.mvp.client.proxy.AlwaysTrueGatekeeper;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;
import com.gwtplatform.processors.tools.SupportedAnnotationClasses;
import com.gwtplatform.processors.tools.bindings.BindingsProcessors;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.utils.MetaInfResource;

import jakarta.inject.Singleton;

import static com.google.auto.common.MoreElements.hasModifiers;
import static com.gwtplatform.common.processors.module.GwtpAppModuleProcessor.MAIN_MODULE_TYPE;
import static com.gwtplatform.processors.tools.bindings.BindingContext.newAnnotatedBinding;

@AutoService(Processor.class)
@SupportedAnnotationClasses(DefaultGatekeeper.class)
public class DefaultGatekeeperProcessor extends AbstractGwtpAppProcessor {
    private static final String META_INF_FILE_NAME = "gwtp/defaultGatekeeper";
    private static final Type DEFAULT_GATEKEEPER = new Type(AlwaysTrueGatekeeper.class);

    private BindingsProcessors bindingsProcessors;
    private MetaInfResource metaInfResource;

    private boolean generated;
    private boolean metaDataProcessed;

    @Override
    protected void initSafe() {
        bindingsProcessors = new BindingsProcessors(logger, utils, outputter);
        metaInfResource = new MetaInfResource(logger, outputter, META_INF_FILE_NAME);
    }

    @Override
    protected void processAsApp(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<TypeElement> gatekeepers = extractGatekeepers(roundEnv);

        if (!gatekeepers.isEmpty()) {
            Type gatekeeper = asGatekeeper(gatekeepers.iterator().next());
            createBinding(gatekeeper);

            generated = true;
        } else if (!generated) {
            if (!metaDataProcessed) {
                maybeGenerateFromMetaData();
                metaDataProcessed = true;
            }
            if (!generated && roundEnv.processingOver()) {
                createBinding(DEFAULT_GATEKEEPER);
            }
        }
    }

    @Override
    protected void processAsModule(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<TypeElement> gatekeepers = extractGatekeepers(roundEnv);

        if (!gatekeepers.isEmpty()) {
            Type gatekeeper = asGatekeeper(gatekeepers.iterator().next());
            createMetadataFile(gatekeeper);

            generated = true;
        }
    }

    private Set<TypeElement> extractGatekeepers(RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(DefaultGatekeeper.class);
        elements = utils.getSourceFilter().filterElements(elements);

        Set<TypeElement> typeElements = elements.stream()
                .filter(gatekeeper -> gatekeeper.getKind() == ElementKind.CLASS)
                .map(MoreElements::asType)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        validateGatekeepers(typeElements);

        return typeElements;
    }

    private void validateGatekeepers(Set<TypeElement> gatekeepers) {
        if (!generated && gatekeepers.size() > 1
                || generated && !gatekeepers.isEmpty()) {
            logger.error()
                    .context(gatekeepers.iterator().next())
                    .log("Multiple classes annotated with @DefaultGatekeeper. You may only have one or none.");
            throw new UnableToProcessException();
        }
    }

    private Type asGatekeeper(TypeElement element) {
        TypeMirror type = element.asType();
        TypeMirror gatekeeperType = utils.createWithWildcard(Gatekeeper.class);

        if (!hasModifiers(Modifier.ABSTRACT).apply(element)
                && hasModifiers(Modifier.PUBLIC).apply(element)
                && utils.getTypes().isSubtype(type, gatekeeperType)) {
            return new Type(type);
        }

        logger.error().context(element)
                .log("Element annotated with @DefaultGatekeeper is invalid. It must be a public, non-abstract class "
                        + "and implement Gatekeeper.");
        throw new UnableToProcessException();
    }

    private void createMetadataFile(Type gatekeeper) {
        metaInfResource.writeLine(gatekeeper.getQualifiedName());
        metaInfResource.closeWriter();

        logger.debug("Default gatekeeper `%s` written to meta data.", gatekeeper);
    }

    private void maybeGenerateFromMetaData() {
        List<String> defaultGatekeepers = metaInfResource.readAll();

        if (defaultGatekeepers.size() > 1) {
            logger.error().log("Multiple classes annotated with @DefaultGatekeeper found in classpath. "
                    + "To resolve the issue, create a new default Gatekeeper in the current source path.");
        } else if (!defaultGatekeepers.isEmpty()) {
            createBinding(new Type(defaultGatekeepers.get(0)));

            generated = true;
        }
    }

    private void createBinding(Type gatekeeper) {
        logger.debug("Default gatekeeper bound to `%s`.", gatekeeper);

        bindingsProcessors.process(newAnnotatedBinding(MAIN_MODULE_TYPE, new Type(Gatekeeper.class), gatekeeper,
                DefaultGatekeeper.class, Singleton.class));
    }
}
