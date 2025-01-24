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
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;
import com.gwtplatform.common.client.NamedProviderBundleCollection;
import com.gwtplatform.common.client.annotations.ProviderBundle;
import com.gwtplatform.common.processors.AbstractGwtpAppProcessor;
import com.gwtplatform.processors.tools.SupportedAnnotationClasses;
import com.gwtplatform.processors.tools.bindings.BindingsProcessors;
import com.gwtplatform.processors.tools.domain.Type;

import jakarta.inject.Singleton;

import static com.google.auto.common.MoreElements.asType;
import static com.gwtplatform.common.processors.module.GwtpAppModuleProcessor.MAIN_MODULE_TYPE;
import static com.gwtplatform.processors.tools.bindings.BindingContext.newBinding;

@AutoService(Processor.class)
@SupportedAnnotationClasses(ProviderBundle.class)
public class NamedProviderBundleCollectionProcessor extends AbstractGwtpAppProcessor {
    private BindingsProcessors bindingsProcessors;
    private ProviderBundleMetaInfReader metaInfReader;
    private ProviderBundleMetaInfWriter metaInfWriter;
    private ProviderBundleCollection collection;

    @Override
    protected void initSafe() {
        bindingsProcessors = new BindingsProcessors(logger, utils, outputter);
        metaInfReader = new ProviderBundleMetaInfReader(logger);
        metaInfWriter = new ProviderBundleMetaInfWriter(logger, outputter);
    }

    @Override
    protected void processSafe(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            super.processSafe(annotations, roundEnv);

            if (roundEnv.processingOver()) {
                metaInfWriter.close();
            }
        } catch (Throwable e) {
            // We don't do it in a finally block because we want to close the file at the last round only
            metaInfWriter.close();
            throw e;
        }
    }

    @Override
    protected void processAsApp(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ensureBundleCollectionInitialized();
        addBundlesToCollection(extractBundles(roundEnv));

        if (roundEnv.processingOver()) {
            collection.write();
        }
    }

    private void ensureBundleCollectionInitialized() {
        if (collection != null) {
            return;
        }

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

    @Override
    protected void processAsModule(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        writeBundlesToMetaInf(extractBundles(roundEnv));
    }

    private Set<? extends Element> extractBundles(RoundEnvironment roundEnv) {
        Set<? extends Element> bundles = roundEnv.getElementsAnnotatedWith(ProviderBundle.class);
        return utils.getSourceFilter().filterElements(bundles);
    }

    private void writeBundlesToMetaInf(Set<? extends Element> bundles) {
        for (Element bundle : bundles) {
            String bundleName = bundle.getAnnotation(ProviderBundle.class).value();
            String type = asType(bundle).getQualifiedName().toString();

            metaInfWriter.addBundle(bundleName, type);
        }
    }
}
