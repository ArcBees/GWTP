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

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JGenericType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.gwtplatform.dispatch.client.rest.NoResultSerializer;
import com.gwtplatform.dispatch.client.rest.Serializer;
import com.gwtplatform.dispatch.shared.MultipleResult;
import com.gwtplatform.dispatch.shared.NoResult;
import com.gwtplatform.dispatch.shared.SimpleResult;

import name.pehl.piriti.json.client.JsonReader;
import name.pehl.piriti.json.client.JsonWriter;

public class SerializerGenerator extends AbstractGenerator {
    private static enum ResultType {
        NO_RESULT("NoResultSerializer"),
        SIMPLE_RESULT("SimpleResultJsonSerializer"),
        MULTIPLE_RESULT("MultipleResultJsonSerializer"),
        CUSTOM("JsonSerializer");

        private final String superclassName;

        private ResultType(String superclassName) {
            this.superclassName = superclassName;
        }

        public String getSuperclassName() {
            return superclassName;
        }
    }

    private static final String PARAMETERIZED_CLASS = "%s<%s>";
    private static final String INTERFACE_READER = "interface Reader extends JsonReader<%s> {}";
    private static final String INTERFACE_WRITER = "interface Writer extends JsonWriter<%s> {}";
    private static final String INTERFACE_READER_LIST = "interface Reader extends JsonReader<List<%s>> {}";
    private static final String INTERFACE_WRITER_LIST = "interface Writer extends JsonWriter<List<%s>> {}";
    private static final String CONSTRUCTOR = "public %s() {";
    private static final String SUPER_CALL = "super(GWT.create(Reader.class), GWT.create(Writer.class));";
    private static final String CLOSE_BLOCK = ")";

    private final JClassType serializedType;
    private JClassType wrappedSerializedType;
    private ResultType resultType;
    private String serializerId;

    public SerializerGenerator(JClassType serializedType) {
        this.serializedType = serializedType;
    }

    public String getSerializerId() {
        return serializerId;
    }

    public String generate(TreeLogger treeLogger, GeneratorContext generatorContext) throws UnableToCompleteException {
        return generate(treeLogger, generatorContext, Serializer.class.getName());
    }

    @Override
    public String generate(TreeLogger treeLogger, GeneratorContext generatorContext, String typeName)
            throws UnableToCompleteException {
        setGeneratorContext(generatorContext);
        setTypeOracle(generatorContext.getTypeOracle());

        setTreeLogger(treeLogger.branch(Type.DEBUG,
                "Generating serializer for " + serializedType.getParameterizedQualifiedSourceName()));

        setTypeClass(getType(typeName));

        resultType = resolveResultType();
        serializerId = generateSerializerId();
        wrappedSerializedType = resolvedWrappedSerializedType();

        if (resultType == ResultType.NO_RESULT) {
            getTreeLogger().log(Type.DEBUG, "No Result Serializer required.");

            getEventBus().post(new RegisterSerializerEvent(serializerId, NoResultSerializer.class.getName()));
            return NoResultSerializer.class.getName();
        }

        PrintWriter printWriter = tryCreatePrintWriter(serializerId, "");

        if (printWriter != null) {
            writeClass(printWriter);

            getEventBus().post(new RegisterSerializerEvent(serializerId, getQualifiedClassName()));
        } else {
            getTreeLogger().log(Type.DEBUG, "Serializer already generated. Returning.");
        }

        return getQualifiedClassName();
    }

    private ResultType resolveResultType() throws UnableToCompleteException {
        String name = serializedType.getName();

        if (name.equals(NoResult.class.getName())) {
            return ResultType.NO_RESULT;
        } else if (name.equals(SimpleResult.class.getName())) {
            return ResultType.SIMPLE_RESULT;
        } else if (name.equals(MultipleResult.class.getName())) {
            return ResultType.MULTIPLE_RESULT;
        }

        return ResultType.CUSTOM;
    }

    private String generateSerializerId() {
        String qualifiedName;

        if (resultType == ResultType.NO_RESULT) {
            qualifiedName = NoResultSerializer.class.getName();
        } else {
            qualifiedName = serializedType.getParameterizedQualifiedSourceName();
        }

        qualifiedName = qualifiedName.replace(" ", "").replaceAll("[.,<>]", "_");

        return qualifiedName;
    }

    private JClassType resolvedWrappedSerializedType() throws UnableToCompleteException {
        if (resultType != ResultType.SIMPLE_RESULT && resultType != ResultType.MULTIPLE_RESULT) {
            return null;
        }

        JGenericType generic = serializedType.isGenericType();

        if (generic == null) {
            getTreeLogger().log(Type.ERROR, "SimpleResult or MultipleResult is used without a type parameter.");
            throw new UnableToCompleteException();
        }

        return generic.getTypeParameters()[0];
    }

    private void writeClass(PrintWriter printWriter) throws UnableToCompleteException {
        ClassSourceFileComposerFactory composer = initComposer();
        SourceWriter sourceWriter = composer.createSourceWriter(getGeneratorContext(), printWriter);

        String readerInterface = INTERFACE_READER;
        String writerInterface = INTERFACE_WRITER;
        String realSerializedType = serializedType.getParameterizedQualifiedSourceName();

        if (resultType == ResultType.SIMPLE_RESULT) {
            realSerializedType = wrappedSerializedType.getParameterizedQualifiedSourceName();
        } else if (resultType == ResultType.MULTIPLE_RESULT) {
            realSerializedType = wrappedSerializedType.getParameterizedQualifiedSourceName();
            readerInterface = INTERFACE_READER_LIST;
            writerInterface = INTERFACE_WRITER_LIST;
        }

        sourceWriter.println(readerInterface, realSerializedType);
        sourceWriter.println();

        sourceWriter.println(writerInterface, realSerializedType);
        sourceWriter.println();

        sourceWriter.println(CONSTRUCTOR, getClassName());

        sourceWriter.indent();
        sourceWriter.println(SUPER_CALL);
        sourceWriter.outdent();

        sourceWriter.println(CLOSE_BLOCK);

        closeDefinition(sourceWriter);
    }

    private ClassSourceFileComposerFactory initComposer() throws UnableToCompleteException {
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(getPackageName(), getClassName());

        JClassType serializer = getType(Serializer.class.getName());
        JClassType jsonSerializer = getType(serializer.getPackage().getName(), resultType.getSuperclassName());

        JClassType jsonReader = getType(JsonReader.class.getName());
        JClassType jsonWriter = getType(JsonWriter.class.getName());

        composer.addImport(jsonSerializer.getPackage().getName());
        composer.addImport(jsonReader.getPackage().getName());
        composer.addImport(jsonWriter.getPackage().getName());

        composer.setSuperclass(String.format(PARAMETERIZED_CLASS, jsonSerializer.getSimpleSourceName(),
                serializedType.getParameterizedQualifiedSourceName()));

        return composer;
    }
}
