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

package com.gwtplatform.common.processors.bundles;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import com.gwtplatform.common.client.NamedProviderBundleCollection;
import com.gwtplatform.common.client.annotations.GwtpApp;
import com.gwtplatform.common.client.annotations.ProviderBundle;
import com.gwtplatform.processors.tools.GwtSourceFilter;
import com.gwtplatform.processors.tools.bindings.BindingsProcessors;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.Outputter;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.google.auto.common.MoreElements.asType;
import static com.gwtplatform.common.processors.module.GwtpAppModuleProcessor.MAIN_MODULE_TYPE;
import static com.gwtplatform.processors.tools.bindings.BindingContext.newBinding;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions({Logger.DEBUG_OPTION, GwtSourceFilter.GWTP_MODULE_OPTION})
public class NamedProviderBundleCollectionProcessor extends AbstractProcessor {

    private Logger logger;
    private Outputter outputter;
    private Utils utils;
    private BindingsProcessors bindingsProcessors;
    private ProviderBundleMetaInfReader metaInfReader;
    private ProviderBundleMetaInfWriter metaInfWriter;

    private boolean isGwtpApp;
    private ProviderBundleCollection collection;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Sets.newHashSet(
                GwtpApp.class.getCanonicalName(),
                ProviderBundle.class.getCanonicalName());
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        Map<String, String> options = processingEnv.getOptions();

        logger = new Logger(processingEnv.getMessager(), options);
        utils = new Utils(logger, processingEnv.getTypeUtils(), processingEnv.getElementUtils(), options);
        outputter = new Outputter(logger, this, processingEnv.getFiler());

        bindingsProcessors = new BindingsProcessors(logger, utils, outputter);
        metaInfReader = new ProviderBundleMetaInfReader(logger);
        metaInfWriter = new ProviderBundleMetaInfWriter(logger, outputter);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            process(roundEnv);
        } catch (UnableToProcessException e) {
            metaInfWriter.close();
        } catch (Exception e) {
            logger.error().throwable(e).log("Unexpected error. See stack trace.");
            metaInfWriter.close();
        }

        return false;
    }

    private void process(RoundEnvironment roundEnv) {
        Set<? extends Element> bundles = roundEnv.getElementsAnnotatedWith(ProviderBundle.class);
        bundles = utils.getSourceFilter().filterElements(bundles);

        if (isGwtpApp(roundEnv)) {
            addBundlesToCollection(bundles);
        } else {
            writeBundlesToMetaInf(bundles);
        }

        if (roundEnv.processingOver()) {
            processLast();
        }
    }

    private boolean isGwtpApp(RoundEnvironment environment) {
        if (!isGwtpApp && !environment.getElementsAnnotatedWith(GwtpApp.class).isEmpty()) {
            isGwtpApp = true;
            initializeBundleCollection();
        }

        return isGwtpApp;
    }

    private void initializeBundleCollection() {
        collection = new ProviderBundleCollection(logger, outputter);
        loadBundlesFromMetaInf();

        bindingsProcessors.process(newBinding(
                MAIN_MODULE_TYPE,
                new Type(NamedProviderBundleCollection.class),
                collection.getType(),
                Singleton.class));
    }

    private void loadBundlesFromMetaInf() {
        for (Entry<String, List<Type>> bundle : metaInfReader.getBundles().entrySet()) {
            for (Type type : bundle.getValue()) {
                collection.addBundle(bundle.getKey(), type);
            }
        }
    }

    private void addBundlesToCollection(Set<? extends Element> bundles) {
        for (Element bundle : bundles) {
            String name = bundle.getAnnotation(ProviderBundle.class).value();
            Type type = new Type(asType(bundle));

            collection.addBundle(name, type);
        }
    }

    private void writeBundlesToMetaInf(Set<? extends Element> bundles) {
        for (Element bundle : bundles) {
            String bundleName = bundle.getAnnotation(ProviderBundle.class).value();
            String type = asType(bundle).getQualifiedName().toString();

            metaInfWriter.addBundle(bundleName, type);
        }
    }

    private void processLast() {
        metaInfWriter.close();

        if (isGwtpApp) {
            collection.write();
        }
    }
}
