/**
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

package com.gwtplatform.dispatch.rebind;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.eventbus.Subscribe;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.gwtplatform.dispatch.client.rest.AbstractSerializerProvider;
import com.gwtplatform.dispatch.shared.rest.RestService;

public class SerializerProviderGenerator extends AbstractGenerator {
    private static final String SUFFIX = "Impl";
    public static final String CONSTRUCTOR = "public %s() {";
    public static final String CLOSE_BLOCK = "}";

    private final Map<String, String> serializers = new HashMap<String, String>();
    private JClassType abstractSerializerProvider;

    public SerializerProviderGenerator() {
        getEventBus().register(this);
    }

    @Override
    public String generate(TreeLogger treeLogger, GeneratorContext generatorContext, String typeName)
            throws UnableToCompleteException {
        setGeneratorContext(generatorContext);
        setTypeOracle(generatorContext.getTypeOracle());
        setTreeLogger(treeLogger);
        setTypeClass(getType(typeName));

        PrintWriter printWriter = tryCreatePrintWriter("", SUFFIX);

        if (printWriter != null) {
            generateRestServices();

            abstractSerializerProvider = getType(AbstractSerializerProvider.class.getName());

            writeClass(printWriter);
        }

        return getQualifiedClassName();
    }

    @Subscribe
    public void handleRegisterSerializer(RegisterSerializerEvent event) {
        serializers.put(event.getSerializerId(), event.getSerializerClass());
    }

    private void generateRestServices() throws UnableToCompleteException {
        List<JClassType> services = getServices();

        for (JClassType service : services) {
            RestServiceGenerator serviceGenerator = new RestServiceGenerator();

            serviceGenerator.generate(getTreeLogger(), getGeneratorContext(), service.getQualifiedSourceName());
        }
    }

    private List<JClassType> getServices() throws UnableToCompleteException {
        JClassType serviceInterface = getType(RestService.class.getName());
        List<JClassType> services = new ArrayList<JClassType>();

        for (JClassType clazz : getTypeOracle().getTypes()) {
            if (clazz.isAssignableTo(serviceInterface)) {
                services.add(clazz);
            }
        }

        return services;
    }

    private void writeClass(PrintWriter printWriter) {
        ClassSourceFileComposerFactory composer = initComposer();
        SourceWriter sourceWriter = composer.createSourceWriter(getGeneratorContext(), printWriter);

        for (Map.Entry<String, String> serializer : serializers.entrySet()) {
            sourceWriter.println(CONSTRUCTOR, getClassName());

            sourceWriter.indent();
            {
                String packageName = extractPackage(serializer.getValue());
                String className = extractClassName(serializer.getValue());

                composer.addImport(packageName);
                sourceWriter.println("registerSerializer(\"%s\", new %s());", serializer.getKey(), className);
            }
            sourceWriter.outdent();

            sourceWriter.println(CLOSE_BLOCK);
        }

        closeDefinition(sourceWriter);
    }

    private String extractPackage(String fullClassName) {
        return fullClassName.substring(0, fullClassName.lastIndexOf('.'));
    }

    private String extractClassName(String fullClassName) {
        return fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
    }

    private ClassSourceFileComposerFactory initComposer() {
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(getPackageName(), getClassName());

        composer.addImport(abstractSerializerProvider.getPackage().getName());
        composer.setSuperclass(abstractSerializerProvider.getSimpleSourceName());

        return composer;
    }
}
