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

import com.google.common.eventbus.Subscribe;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.gwtplatform.dispatch.client.rest.AbstractSerializerProvider;
import com.gwtplatform.dispatch.client.rest.SerializedType;
import com.gwtplatform.dispatch.shared.rest.RestService;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SerializerProviderGenerator extends AbstractGenerator {
    private static final String SUFFIX = "Impl";
    private static final String SERIALIZED_TYPE_NAME = SerializedType.class.getSimpleName();
    public static final String CONSTRUCTOR = "public %s() {";
    private static final String REGISTER_SERIALIZER = "registerSerializer(%s.class, " + SERIALIZED_TYPE_NAME + ".%s, new %s());";
    public static final String CLOSE_BLOCK = "}";

    private final List<String> registeredSerializers = new ArrayList<String>();

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

            writeClass(printWriter);
        }

        return getQualifiedClassName();
    }

    @Subscribe
    public void handleRegisterSerializer(RegisterSerializerEvent event) {
        String serializerClass = event.getSerializerClass();
        String serializedType = event.getSerializedType().name();
        String actionClass = event.getActionClass();

        getTreeLogger().log(Type.DEBUG, "Serializer " + serializerClass + " registered.");

        String registerSerializer = String.format(REGISTER_SERIALIZER, actionClass, serializedType, serializerClass);

        registeredSerializers.add(registerSerializer);
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

    private void writeClass(PrintWriter printWriter) throws UnableToCompleteException {
        ClassSourceFileComposerFactory composer = initComposer();
        SourceWriter sourceWriter = composer.createSourceWriter(getGeneratorContext(), printWriter);

        sourceWriter.println(CONSTRUCTOR, getClassName());
        sourceWriter.indent();

        for (String serializer : registeredSerializers) {
            sourceWriter.println(serializer);
        }

        sourceWriter.outdent();
        sourceWriter.println(CLOSE_BLOCK);

        closeDefinition(sourceWriter);
    }

    private ClassSourceFileComposerFactory initComposer() throws UnableToCompleteException {
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(getPackageName(), getClassName());

        JClassType abstractSerializerProvider = getType(AbstractSerializerProvider.class.getName());

        composer.addImport(abstractSerializerProvider.getQualifiedSourceName());
        composer.addImport(SerializedType.class.getName());

        composer.setSuperclass(abstractSerializerProvider.getSimpleSourceName());

        return composer;
    }
}
