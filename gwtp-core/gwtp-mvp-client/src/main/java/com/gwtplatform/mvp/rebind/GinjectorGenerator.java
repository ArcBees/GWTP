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
import java.util.List;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.common.rebind.Logger;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.rebind.velocity.GenerateFormFactorGinjectors;
import com.gwtplatform.mvp.rebind.velocity.RebindModule;

/**
 * Will generate a Ginjector from Ginjector.
 */
public class GinjectorGenerator extends AbstractGenerator {
    static final String DEFAULT_NAME = "ClientGinjector";
    static final String DEFAULT_FQ_NAME = DEFAULT_PACKAGE + "." + DEFAULT_NAME;

    private static final String SINGLETON_DECLARATION
            = "static ClientGinjector SINGLETON = ((GinjectorProvider) GWT.create(GinjectorProvider.class)).get();";
    private static final String GETTER_METHOD = "%s get%s();";

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
            ClassSourceFileComposerFactory composer = initComposer();
            writeMandatoryGetterImports(composer);

            SourceWriter sourceWriter = composer.createSourceWriter(generatorContext, printWriter);
            writeMandatoryGetter(sourceWriter);

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
                JClassType extensionType = getType(extension.trim());
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
