package com.gwtplatform.dispatch.rebind;

import java.io.PrintWriter;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class SerializerProviderGenerator extends AbstractGenerator {
    private static final String SUFFIX = "Impl";

    @Override
    public String generate(TreeLogger treeLogger, GeneratorContext generatorContext, String typeName)
            throws UnableToCompleteException {
        setGeneratorContext(generatorContext);
        setTypeOracle(generatorContext.getTypeOracle());
        setTreeLogger(treeLogger);
        setTypeClass(getType(typeName));

        PrintWriter printWriter = tryCreatePrintWriter("", SUFFIX);

        if (printWriter != null) {
            //TODO: Scan Services and generate them
            //TODO: Generate implementation of AbstractSerializerProvider

            writeClass(printWriter);
        }

        return getPackageName() + "." + getClassName();
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
