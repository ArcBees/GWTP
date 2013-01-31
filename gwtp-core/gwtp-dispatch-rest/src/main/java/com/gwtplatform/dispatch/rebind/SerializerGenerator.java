package com.gwtplatform.dispatch.rebind;

import java.io.PrintWriter;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.gwtplatform.dispatch.client.rest.AbstractJsonSerializer;
import com.gwtplatform.dispatch.client.rest.Serializer;

import name.pehl.piriti.json.client.JsonReader;
import name.pehl.piriti.json.client.JsonWriter;

public class SerializerGenerator extends AbstractGenerator {
    private static final String SUFFIX = "Impl";
    private static final String PARAMETERIZED_CLASS = "%s<%s>";
    private static final String INTERFACE_READER = "interface Reader extends JsonReader<%s> {}";
    private static final String INTERFACE_WRITER = "interface Writer extends JsonWriter<%s> {}";
    private static final String CONSTRUCTOR = "public %s() {";
    private static final String SUPER_CALL = "super(GWT.create(Reader.class), GWT.create(Writer.class));";
    private static final String CLOSE_BLOCK = ")";

    private final JType serializedType;
    private String serializerId;

    public SerializerGenerator(JType serializedType) {
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
        setTreeLogger(treeLogger);
        setTypeClass(getType(typeName));

        PrintWriter printWriter = tryCreatePrintWriter("", SUFFIX);

        if (printWriter != null) {
            setTreeLogger(treeLogger.branch(Type.DEBUG,
                    "Generating serializer for " + serializedType.getParameterizedQualifiedSourceName()));

            //TODO: Add support for wrapper types (eg: Noresult, SimpleResult, ...)
            writeClass(printWriter);

            getEventBus().post(new RegisterSerializerEvent(serializerId, getParameterizedQualifiedClassName()));
        }

        return getQualifiedClassName();
    }

    public String getParameterizedQualifiedClassName() {
        return String.format(PARAMETERIZED_CLASS, getQualifiedClassName(),
                serializedType.getParameterizedQualifiedSourceName());
    }

    private void writeClass(PrintWriter printWriter) throws UnableToCompleteException {
        ClassSourceFileComposerFactory composer = initComposer();
        SourceWriter sourceWriter = composer.createSourceWriter(getGeneratorContext(), printWriter);

        sourceWriter.println(INTERFACE_READER, serializedType.getParameterizedQualifiedSourceName());
        sourceWriter.println();

        sourceWriter.println(INTERFACE_WRITER, serializedType.getParameterizedQualifiedSourceName());
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

        JClassType superClass = getType(AbstractJsonSerializer.class.getName());
        JClassType jsonReader = getType(JsonReader.class.getName());
        JClassType jsonWriter = getType(JsonWriter.class.getName());

        composer.addImport(superClass.getPackage().getName());
        composer.addImport(jsonReader.getPackage().getName());
        composer.addImport(jsonWriter.getPackage().getName());

        composer.setSuperclass(String.format(PARAMETERIZED_CLASS, superClass.getSimpleSourceName(),
                serializedType.getParameterizedQualifiedSourceName()));

        return composer;
    }
}
