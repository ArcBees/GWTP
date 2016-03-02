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
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import com.google.auto.service.AutoService;
import com.google.common.collect.Sets;
import com.gwtplatform.common.client.annotations.GwtpApp;
import com.gwtplatform.mvp.client.annotations.DefaultGatekeeper;
import com.gwtplatform.mvp.client.proxy.AlwaysTrueGatekeeper;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;
import com.gwtplatform.processors.tools.GwtSourceFilter;
import com.gwtplatform.processors.tools.bindings.BindingsProcessors;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.Outputter;
import com.gwtplatform.processors.tools.utils.MetaInfResource;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.google.auto.common.MoreElements.asType;
import static com.google.auto.common.MoreElements.hasModifiers;
import static com.gwtplatform.common.processors.module.GwtpAppModuleProcessor.MAIN_MODULE_TYPE;
import static com.gwtplatform.processors.tools.bindings.BindingContext.newAnnotatedBinding;

@AutoService(Processor.class)
@SupportedOptions({Logger.DEBUG_OPTION, GwtSourceFilter.GWTP_MODULE_OPTION})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class DefaultGatekeeperProcessor extends AbstractProcessor {
    private static final String META_INF_FILE_NAME = "gwtp/defaultGatekeeper";

    private Logger logger;
    private Utils utils;
    private BindingsProcessors bindingsProcessors;
    private MetaInfResource metaInfResource;

    private boolean gwtpAppPresent;
    private boolean generated;
    private boolean metaDataProcessed;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Sets.newHashSet(
                GwtpApp.class.getCanonicalName(),
                DefaultGatekeeper.class.getCanonicalName()
        );
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        Map<String, String> options = processingEnv.getOptions();
        logger = new Logger(processingEnv.getMessager(), options);
        utils = new Utils(logger, processingEnv.getTypeUtils(), processingEnv.getElementUtils(), options);
        Outputter outputter = new Outputter(logger, this, processingEnv.getFiler());
        bindingsProcessors = new BindingsProcessors(logger, utils, outputter);
        metaInfResource = new MetaInfResource(logger, outputter, META_INF_FILE_NAME);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.errorRaised()) {
            return false;
        }

        try {
            loadGwtpApp(roundEnv);
            maybeGenerate(roundEnv);
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

    private void maybeGenerate(RoundEnvironment roundEnv) {
        Set<TypeElement> gatekeepers = extractGatekeeperTypeElements(roundEnv);

        if (!gatekeepers.isEmpty()) {
            generateFromElement(gatekeepers);
        } else if (gwtpAppPresent && !generated) {
            if (!metaDataProcessed) {
                maybeGenerateFromMetaData();
                metaDataProcessed = true;
            }
            if (!generated && roundEnv.processingOver()) {
                createBinding(new Type(AlwaysTrueGatekeeper.class));
            }
        }
    }

    private Set<TypeElement> extractGatekeeperTypeElements(RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(DefaultGatekeeper.class);
        elements = utils.getSourceFilter().filterElements(elements);
        Set<TypeElement> typeElements = new LinkedHashSet<>();

        for (Element gatekeeper : elements) {
            if (gatekeeper.getKind() == ElementKind.CLASS) {
                typeElements.add(asType(gatekeeper));
            }
        }

        return typeElements;
    }

    private void generateFromElement(Set<TypeElement> elements) {
        if (generated || elements.size() != 1) {
            logger.error()
                    .context(elements.iterator().next())
                    .log("Multiple classes annotated with @DefaultGatekeeper. You may only have one or none.");
            throw new UnableToProcessException();
        } else {
            Type gatekeeper = asGatekeeper(elements.iterator().next());
            if (gwtpAppPresent) {
                createBinding(gatekeeper);
            } else {
                createMetadataFile(gatekeeper);
            }

            generated = true;
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
