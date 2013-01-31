package com.gwtplatform.dispatch.rebind;

import java.io.PrintWriter;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.gwtplatform.dispatch.client.rest.Serializer;

public class SerializerGenerator extends AbstractGenerator {
    private static final String SUFFIX = "Impl";

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

            //TODO: Generate implementation of AbstractJsonSerializer

            writeClass(printWriter);

            // TODO: Must be parameterized
            getEventBus().post(new RegisterSerializerEvent(serializerId, getQualifiedClassName()));
        }

        return getQualifiedClassName();
    }

    private void writeClass(PrintWriter printWriter) {
        ClassSourceFileComposerFactory composer = initComposer();
        SourceWriter sourceWriter = composer.createSourceWriter(getGeneratorContext(), printWriter);

        closeDefinition(sourceWriter);
    }

    private ClassSourceFileComposerFactory initComposer() {
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(getPackageName(), getClassName());

        composer.addImport(getTypeClass().getQualifiedSourceName());

        composer.addImplementedInterface(getTypeClass().getName());

        return composer;
    }
}
