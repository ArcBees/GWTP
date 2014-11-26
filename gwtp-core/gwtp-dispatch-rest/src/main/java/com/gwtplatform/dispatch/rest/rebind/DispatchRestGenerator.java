/**
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.rebind;

import java.io.PrintWriter;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.gwtplatform.dispatch.rest.client.AbstractDispatchRestEntryPoint;
import com.gwtplatform.dispatch.rest.rebind.type.ServiceDefinitions;
import com.gwtplatform.dispatch.rest.rebind2.serialization.ActionMetadataProviderGenerator;
import com.gwtplatform.dispatch.rest.rebind2.serialization.JacksonMapperProviderGenerator;

public class DispatchRestGenerator extends Generator {
    private static final String SUFFIX = "Impl";
    private static final String SHARED = "shared";
    private static final String CLIENT = "client";

    private String packageName;
    private String className;
    private Logger logger;
    private TypeOracle typeOracle;
    private JClassType type;
    private Injector injector;
    private GeneratorFactory generatorFactory;
    private ActionMetadataProviderGenerator actionMetadataProviderGenerator;
    private JacksonMapperProviderGenerator jacksonMapperProviderGenerator;

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName)
            throws UnableToCompleteException {
        resetFields(logger, context, typeName);

        PrintWriter printWriter = tryCreatePrintWriter(context);
        String resultType = typeName + SUFFIX;

        if (printWriter == null) {
            return resultType;
        }

        generateClasses();
        generateEntryPoint(logger, context, printWriter);

        return resultType;
    }

    private void resetFields(TreeLogger treeLogger, GeneratorContext generatorContext, String typeName)
            throws UnableToCompleteException {
        logger = new Logger(treeLogger);
        typeOracle = generatorContext.getTypeOracle();
        type = getType(typeName);

        injector = Guice.createInjector(new RebindModule(logger, generatorContext));
        actionMetadataProviderGenerator = injector.getInstance(ActionMetadataProviderGenerator.class);
        jacksonMapperProviderGenerator = injector.getInstance(JacksonMapperProviderGenerator.class);
        generatorFactory = injector.getInstance(GeneratorFactory.class);
    }

    private PrintWriter tryCreatePrintWriter(GeneratorContext generatorContext) throws UnableToCompleteException {
        packageName = type.getPackage().getName().replace(SHARED, CLIENT);
        className = type.getName() + SUFFIX;

        return generatorContext.tryCreate(logger.getTreeLogger(), packageName, className);
    }

    private JClassType getType(String typeName) throws UnableToCompleteException {
        try {
            return typeOracle.getType(typeName);
        } catch (NotFoundException e) {
            logger.die("Cannot find " + typeName);
        }

        return null;
    }

    private void generateClasses() throws UnableToCompleteException {
        generateResources();
        generateGinModule();
        generateMetadataProvider();
        generateJacksonMapperProvider();
    }

    private void generateResources() throws UnableToCompleteException {
        ServiceDefinitions serviceDefinitions = injector.getInstance(ServiceDefinitions.class);

        for (JClassType service : serviceDefinitions.getServices()) {
            ServiceGenerator serviceGenerator = generatorFactory.createServiceGenerator(service);
            serviceGenerator.generate();
        }
    }

    private void generateGinModule() throws UnableToCompleteException {
        GinModuleGenerator moduleGenerator = injector.getInstance(GinModuleGenerator.class);
        moduleGenerator.generate();
    }

    private void generateMetadataProvider() throws UnableToCompleteException {
        actionMetadataProviderGenerator.generate();
    }

    private void generateJacksonMapperProvider() throws UnableToCompleteException {
        jacksonMapperProviderGenerator.generate();
    }

    private void generateEntryPoint(TreeLogger treeLogger, GeneratorContext generatorContext, PrintWriter printWriter) {
        ClassSourceFileComposerFactory composer = initComposer();
        SourceWriter sourceWriter = composer.createSourceWriter(generatorContext, printWriter);
        sourceWriter.commit(treeLogger);
    }

    private ClassSourceFileComposerFactory initComposer() {
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, className);
        composer.setSuperclass(AbstractDispatchRestEntryPoint.class.getSimpleName());

        return composer;
    }
}
