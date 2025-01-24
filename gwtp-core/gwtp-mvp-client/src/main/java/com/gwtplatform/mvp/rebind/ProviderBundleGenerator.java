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
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.inject.Provider;
import com.gwtplatform.common.client.ProviderBundle;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * Will generate a ProviderBundle.
 */
public class ProviderBundleGenerator extends AbstractGenerator {
    static final String SUFFIX = "Bundle";
    private static final String PUBLIC_STATIC_INT = "public static final int %s = %s;";
    private static final String CTOR_PARAM = "Provider<%s> %s";
    private static final String ARRAY_SETTER = "providers[%s] = %s;";

    private List<JClassType> presenters;

    @Override
    public String generate(TreeLogger treeLogger, GeneratorContext generatorContext, String typeName)
            throws UnableToCompleteException {
        setClassName(typeName + SUFFIX);

        setTypeOracle(generatorContext.getTypeOracle());
        setTreeLogger(treeLogger);
        setPropertyOracle(generatorContext.getPropertyOracle());

        PrintWriter printWriter;
        printWriter = tryCreatePrintWriter(generatorContext);

        if (printWriter == null) {
            return typeName;
        }

        try {
            ClassSourceFileComposerFactory composer = initComposer();
            writePresenterImports(composer, presenters);

            SourceWriter sourceWriter = composer.createSourceWriter(generatorContext, printWriter);
            writeStaticFields(sourceWriter, presenters);
            writeConstructor(sourceWriter, presenters);

            closeDefinition(sourceWriter);

            return getPackageName() + "." + getClassName();
        } finally {
            printWriter.close();
        }
    }

    private PrintWriter tryCreatePrintWriter(GeneratorContext generatorContext) throws UnableToCompleteException {
        return generatorContext.tryCreate(getTreeLogger(), getPackageName(), getClassName());
    }

    private ClassSourceFileComposerFactory initComposer() throws UnableToCompleteException {
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(getPackageName(), getClassName());
        composer.setSuperclass(ProviderBundle.class.getSimpleName());
        composer.addImport(ProviderBundle.class.getCanonicalName());
        composer.addImport(Provider.class.getCanonicalName());
        composer.addImport(Singleton.class.getCanonicalName());
        composer.addImport(Inject.class.getCanonicalName());

        composer.addAnnotationDeclaration("@" + Singleton.class.getSimpleName());

        return composer;
    }

    private void writePresenterImports(ClassSourceFileComposerFactory composer, List<JClassType> presenters) {
        for (JClassType presenter : presenters) {
            composer.addImport(presenter.getQualifiedSourceName());
        }
    }

    private void writeStaticFields(SourceWriter sourceWriter, List<JClassType> presenters) {
        int i = 0;
        for (JClassType presenter : presenters) {
            sourceWriter.println(String.format(PUBLIC_STATIC_INT, presenter.getSimpleSourceName().toUpperCase(), i));
            sourceWriter.println();
            i++;
        }
    }

    private void writeConstructor(SourceWriter sourceWriter, List<JClassType> presenters) {
        sourceWriter.print("@" + Inject.class.getSimpleName());
        sourceWriter.println();
        sourceWriter.print("public " + getClassName() + "(");
        sourceWriter.println();
        sourceWriter.indent();
        sourceWriter.indent();

        int i = 0;
        for (JClassType presenter : presenters) {
            String name = presenter.getSimpleSourceName();
            sourceWriter.print(String.format(CTOR_PARAM, name, name.toLowerCase()));
            if (i == presenters.size() - 1) {
                sourceWriter.print(") {");
            } else {
                sourceWriter.print(",");
            }
            sourceWriter.println();
            i++;
        }

        sourceWriter.outdent();
        sourceWriter.print("super(" + presenters.size() + ");");
        sourceWriter.println();
        for (JClassType presenter : presenters) {
            String name = presenter.getSimpleSourceName();
            sourceWriter.print(String.format(ARRAY_SETTER, name.toUpperCase(), name.toLowerCase()));
            sourceWriter.println();
        }
        sourceWriter.outdent();
        sourceWriter.print("}");
        sourceWriter.println();
        sourceWriter.outdent();
    }

    public void setPresenters(List<JClassType> presenters) {
        this.presenters = presenters;
    }
}
