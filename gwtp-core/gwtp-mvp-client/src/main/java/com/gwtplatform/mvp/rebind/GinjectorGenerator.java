/*
 * Copyright 2011 ArcBees Inc.
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

package com.gwtplatform.mvp.rebind;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.common.rebind.Logger;
import com.gwtplatform.mvp.client.annotations.DefaultGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplitBundle;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplitBundle.NoOpProviderBundle;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.rebind.velocity.GenerateFormFactorGinjectors;
import com.gwtplatform.mvp.rebind.velocity.RebindModule;

import jakarta.inject.Provider;

/**
 * Will generate a Ginjector from Ginjector.
 */
public class GinjectorGenerator extends AbstractGenerator {
    static final String GIN_GINJECTOR_ANNOTATION = "gin.ginjector.annotation";

    static final String DEFAULT_NAME = "ClientGinjector";
    static final String DEFAULT_FQ_NAME = DEFAULT_PACKAGE + "." + DEFAULT_NAME;

    private static final String SINGLETON_DECLARATION
            = "static ClientGinjector SINGLETON = ((GinjectorProvider) GWT.create(GinjectorProvider.class)).get();";
    private static final String GETTER_METHOD = "%s get%s();";
    private static final String GETTER_PROVIDER_METHOD = "%s<%s> get%s();";
    private static final String DEFAULT_GATEKEEPER = "@%s";

    private final ProviderBundleGenerator providerBundleGenerator = new ProviderBundleGenerator();

    private final JClassType boostrapper;

    public GinjectorGenerator(JClassType bootstrapper) {
        this.boostrapper = bootstrapper;
    }

    @Override
    public String generate(TreeLogger treeLogger, GeneratorContext generatorContext, String typeName)
            throws UnableToCompleteException {

        setTypeOracle(generatorContext.getTypeOracle());
        setTreeLogger(treeLogger);
        setPropertyOracle(generatorContext.getPropertyOracle());

        PrintWriter printWriter = tryCreatePrintWriter(generatorContext);

        if (printWriter == null) {
            return typeName;
        }
        try {
            PresenterDefinitions presenterDefinitions = new PresenterDefinitions();
            findAllPresenters(presenterDefinitions);

            ClassSourceFileComposerFactory composer = initComposer();
            writeMandatoryGetterImports(composer);
            writePresenterImports(composer, presenterDefinitions);

            SourceWriter sourceWriter = composer.createSourceWriter(generatorContext, printWriter);
            writeMandatoryGetter(sourceWriter);
            writePresentersGetter(sourceWriter, presenterDefinitions);
            writeBundleGetters(sourceWriter, presenterDefinitions.getCodeSplitBundlePresenters(), generatorContext);

            Injector injector = Guice.createInjector(new RebindModule(new Logger(treeLogger), generatorContext));
            writeFormFactors(injector);

            closeDefinition(sourceWriter);

            return DEFAULT_FQ_NAME;
        } finally {
            printWriter.close();
        }
    }

    private PrintWriter tryCreatePrintWriter(GeneratorContext generatorContext) throws UnableToCompleteException {
        setClassName(DEFAULT_NAME);
        setPackageName(DEFAULT_PACKAGE);

        return generatorContext.tryCreate(getTreeLogger(), getPackageName(), getClassName());
    }

    private void findAllPresenters(PresenterDefinitions presenterDefinitions) throws UnableToCompleteException {
        List<Class<? extends Annotation>> annotations = findGinjectorAnnotations();

        for (JClassType type : getTypeOracle().getTypes()) {
            if (type.isAnnotationPresent(ProxyStandard.class)) {
                presenterDefinitions.addStandardPresenter(type.getEnclosingType());
            } else if (type.isAnnotationPresent(ProxyCodeSplit.class)) {
                presenterDefinitions.addCodeSplitPresenter(type.getEnclosingType());
            } else if (type.isAnnotationPresent(ProxyCodeSplitBundle.class)) {
                ProxyCodeSplitBundle annotation = type.getAnnotation(ProxyCodeSplitBundle.class);
                verifyCodeSplitBundleConfiguration(type.getName(), annotation);
                presenterDefinitions.addCodeSplitBundlePresenter(annotation.value(), type.getEnclosingType());
            }

            if (type.isAnnotationPresent(UseGatekeeper.class)) {
                presenterDefinitions.addGatekeeper(getType(type.getAnnotation(UseGatekeeper.class).value().getName()));
            } else if (type.isAnnotationPresent(DefaultGatekeeper.class)) {
                presenterDefinitions.addGatekeeper(type);
            }

            for (Class<? extends Annotation> annotation : annotations) {
                if (type.isAnnotationPresent(annotation)) {
                    presenterDefinitions.addStandardPresenter(type);
                    break;
                }
            }
        }
    }

    private List<Class<? extends Annotation>> findGinjectorAnnotations() throws UnableToCompleteException {
        List<Class<? extends Annotation>> annotations = new ArrayList<Class<? extends Annotation>>();

        List<String> values = findConfigurationProperty(GIN_GINJECTOR_ANNOTATION).getValues();
        for (String value : values) {
            String annotationClassName = value.trim();

            if (!annotationClassName.isEmpty()) {
                try {
                    annotations.add(Class.forName(annotationClassName).asSubclass(Annotation.class));
                } catch (ClassNotFoundException e) {
                    getTreeLogger().log(TreeLogger.ERROR, "Cannot find the class '" + annotationClassName
                                + "' used in '" + GIN_GINJECTOR_ANNOTATION + "' property.");
                    throw new UnableToCompleteException();
                } catch (ClassCastException e) {
                    getTreeLogger().log(TreeLogger.ERROR, "Class '" + annotationClassName + "' used in '"
                                + GIN_GINJECTOR_ANNOTATION + "' property is not an Annotation.");
                    throw new UnableToCompleteException();
                }
            }
        }

        return annotations;
    }

    private void verifyCodeSplitBundleConfiguration(String presenter, ProxyCodeSplitBundle annotation)
            throws UnableToCompleteException {
        if (annotation.value().isEmpty()) {
            getTreeLogger().log(TreeLogger.ERROR, "Cannot find the bundle value used with @"
                    + ProxyCodeSplitBundle.class.getSimpleName() + " on presenter '" + presenter + "'.");
            throw new UnableToCompleteException();
        }
        if (annotation.id() != -1 || !annotation.bundleClass().equals(NoOpProviderBundle.class)) {
            getTreeLogger().log(TreeLogger.WARN, "ID and bundleClass used with @" + ProxyCodeSplitBundle.class
                    .getSimpleName()
                    + " on presenter '" + presenter + "' are ignored since bundles are automatically generated");
        }
    }

    private ClassSourceFileComposerFactory initComposer() throws UnableToCompleteException {
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(getPackageName(), getClassName());
        composer.addImport(Ginjector.class.getCanonicalName());
        composer.makeInterface();
        composer.addImplementedInterface(Ginjector.class.getSimpleName());

        addExtensionInterfaces(composer);

        return composer;
    }

    private void addExtensionInterfaces(ClassSourceFileComposerFactory composer) throws UnableToCompleteException {
        List<String> values = findConfigurationProperty(GIN_GINJECTOR_EXTENSION).getValues();
        for (String extension : values) {
            if (!extension.isEmpty()) {
                final JClassType extensionType = getType(extension.trim());
                composer.addImport(extensionType.getQualifiedSourceName());
                composer.addImplementedInterface(extensionType.getName());
            }
        }
    }

    private void writeMandatoryGetterImports(ClassSourceFileComposerFactory composer) {
        composer.addImport(GWT.class.getCanonicalName());
        composer.addImport(EventBus.class.getCanonicalName());
        composer.addImport(PlaceManager.class.getCanonicalName());
        composer.addImport(boostrapper.getQualifiedSourceName());
    }

    private void writePresenterImports(ClassSourceFileComposerFactory composer,
            PresenterDefinitions presenterDefinitions) {
        if (presenterDefinitions.getStandardPresenters().size() > 0) {
            composer.addImport(Provider.class.getCanonicalName());
        }

        if (presenterDefinitions.getCodeSplitPresenters().size() > 0 ||
                presenterDefinitions.getCodeSplitBundlePresenters().size() > 0) {
            composer.addImport(AsyncProvider.class.getCanonicalName());
        }
    }

    private void writeMandatoryGetter(SourceWriter sourceWriter) {
        sourceWriter.println(SINGLETON_DECLARATION);
        sourceWriter.println();

        String eventBusName = EventBus.class.getSimpleName();
        sourceWriter.println(String.format(GETTER_METHOD, eventBusName, eventBusName));
        sourceWriter.println();

        String placeManagerName = PlaceManager.class.getSimpleName();
        sourceWriter.println(String.format(GETTER_METHOD, placeManagerName, placeManagerName));

        sourceWriter.println();
        String bootstrapperName = boostrapper.getSimpleSourceName();
        sourceWriter.println(String.format(GETTER_METHOD, bootstrapperName, bootstrapperName));
    }

    private void writePresentersGetter(SourceWriter sourceWriter, PresenterDefinitions presenterDefinitions) {
        writeGatekeeperGetterFromList(sourceWriter, presenterDefinitions.getGatekeepers());

        writePresenterGettersFromList(sourceWriter, presenterDefinitions.getStandardPresenters(),
                Provider.class.getSimpleName());
        writePresenterGettersFromList(sourceWriter, presenterDefinitions.getCodeSplitPresenters(),
                AsyncProvider.class.getSimpleName());
    }

    private void writeBundleGetters(SourceWriter sourceWriter, Map<String, List<JClassType>> bundles,
            GeneratorContext generatorContext) throws UnableToCompleteException {
        for (Entry<String, List<JClassType>> entry : bundles.entrySet()) {
            providerBundleGenerator.setPresenters(entry.getValue());
            providerBundleGenerator.setPackageName(getPackageName());
            String bundleName = providerBundleGenerator.generate(getTreeLogger(), generatorContext, entry.getKey());
            sourceWriter.println();
            sourceWriter.println(String.format(GETTER_PROVIDER_METHOD, AsyncProvider.class.getSimpleName(), bundleName,
                    getSimpleNameFromTypeName(bundleName)));
        }
    }

    private void writeGatekeeperGetterFromList(SourceWriter sourceWriter, Collection<JClassType> gatekeepers) {
        for (JClassType gatekeeper : gatekeepers) {
            String name = gatekeeper.getQualifiedSourceName();

            sourceWriter.println();
            if (gatekeeper.isAnnotationPresent(DefaultGatekeeper.class)) {
                sourceWriter.println(String.format(DEFAULT_GATEKEEPER, DefaultGatekeeper.class.getCanonicalName()));
            }

            sourceWriter.println(String.format(GETTER_METHOD, name, name.replaceAll("\\.", "")));
        }
    }

    private void writePresenterGettersFromList(SourceWriter sourceWriter, Collection<JClassType> presenters,
            String providerTypeName) {
        for (JClassType presenter : presenters) {
            String name = presenter.getQualifiedSourceName();

            sourceWriter.println();
            sourceWriter.println(String.format(GETTER_PROVIDER_METHOD, providerTypeName, name,
                    name.replaceAll("\\.", "")));
        }
    }

    private void writeFormFactors(Injector injector) throws UnableToCompleteException {
        GenerateFormFactorGinjectors generateFormFactorGinjectors
                = injector.getInstance(GenerateFormFactorGinjectors.class);

        try {
            generateFormFactorGinjectors.generate();
        } catch (Exception e) {
            throw new UnableToCompleteException();
        }
    }
}
