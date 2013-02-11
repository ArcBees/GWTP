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

package com.gwtplatform.dispatch.rebind;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
import com.gwtplatform.dispatch.rebind.type.RegisterSerializerBinding;
import com.gwtplatform.dispatch.rebind.type.ServiceDefinitions;

public class VelocityGenerator extends Generator {
    private static final String SUFFIX = "Impl";
    private static final String SHARED = "shared";
    private static final String CLIENT = "client";

    private final List<RegisterSerializerBinding> registeredSerializers = new ArrayList<RegisterSerializerBinding>();

    private String packageName;
    private String className;
    private Logger logger;
    private TypeOracle typeOracle;
    private JClassType type;
    private Injector injector;
    private GeneratorFactory generatorFactory;

    @Override
    public String generate(TreeLogger treeLogger, GeneratorContext generatorContext, String typeName)
            throws UnableToCompleteException {
        logger = new Logger(treeLogger);
        typeOracle = generatorContext.getTypeOracle();
        type = getType(typeName);

        PrintWriter printWriter = tryCreatePrintWriter(generatorContext);
        if (printWriter == null) {
            return typeName + SUFFIX;
        }

        injector = Guice.createInjector(new RebindModule(logger, generatorContext));
        generatorFactory = injector.getInstance(GeneratorFactory.class);

        generateRestServices();
        generateRestGinModule();
        generateSerializerProvider();

        ClassSourceFileComposerFactory composer = initComposer();
        SourceWriter sourceWriter = composer.createSourceWriter(generatorContext, printWriter);
        sourceWriter.commit(treeLogger);

        return typeName + SUFFIX;
    }

    private void generateSerializerProvider() throws UnableToCompleteException {
        SerializerProviderGenerator generator = injector.getInstance(SerializerProviderGenerator.class);
        generator.generate();
    }

    private void generateRestGinModule() throws UnableToCompleteException {
        try {
            RestGinModuleGenerator moduleGenerator = injector.getInstance(RestGinModuleGenerator.class);
            moduleGenerator.generate();
        } catch (Exception e) {
            logger.die(e.getMessage());
        }
    }

    private void generateRestServices() throws UnableToCompleteException {
        ServiceDefinitions serviceDefinitions = injector.getInstance(ServiceDefinitions.class);

        for (JClassType service : serviceDefinitions.getServices()) {
            try {
                RestServiceGenerator serviceGenerator = generatorFactory.createServiceGenerator(service);
                serviceGenerator.generate();
            } catch (Exception e) {
                logger.die(e.getMessage());
            }
        }
    }

    private JClassType getType(String typeName) throws UnableToCompleteException {
        try {
            return typeOracle.getType(typeName);
        } catch (NotFoundException e) {
            logger.die("Cannot find " + typeName);
        }

        return null;
    }

    private PrintWriter tryCreatePrintWriter(GeneratorContext generatorContext) throws UnableToCompleteException {
        packageName = type.getPackage().getName().replace(SHARED, CLIENT);
        className = type.getName() + SUFFIX;

        return generatorContext.tryCreate(logger.getTreeLogger(), packageName, className);
    }

    private ClassSourceFileComposerFactory initComposer() {
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, className);
        composer.addImport(type.getQualifiedSourceName());
        composer.addImplementedInterface(type.getName());

        return composer;
    }
}
