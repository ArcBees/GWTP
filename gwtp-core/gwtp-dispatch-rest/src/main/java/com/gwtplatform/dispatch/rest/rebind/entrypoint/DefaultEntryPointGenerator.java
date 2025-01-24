/*
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.rebind.entrypoint;

import java.io.PrintWriter;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.gwtplatform.dispatch.rest.client.DispatchRestEntryPoint;
import com.gwtplatform.dispatch.rest.client.codegen.AbstractDispatchRestEntryPoint;
import com.gwtplatform.dispatch.rest.rebind.AbstractGenerator;
import com.gwtplatform.dispatch.rest.rebind.utils.ClassDefinition;
import com.gwtplatform.dispatch.rest.rebind.utils.Logger;

import jakarta.inject.Inject;

public class DefaultEntryPointGenerator extends AbstractGenerator implements EntryPointGenerator {
    private String packageName;
    private String className;
    private PrintWriter printWriter;

    @Inject
    DefaultEntryPointGenerator(
            Logger logger,
            GeneratorContext context) {
        super(logger, context);
    }

    @Override
    public boolean canGenerate(String typeName) {
        JClassType type = findType(typeName);
        JClassType entryPointType = findType(DispatchRestEntryPoint.class);

        return type != null
                && entryPointType != null
                && type.isAssignableTo(entryPointType);
    }

    @Override
    public ClassDefinition generate(String typeName) throws UnableToCompleteException {
        JClassType type = getType(typeName);
        packageName = type.getPackage().getName();
        className = type.getName() + IMPL;
        printWriter = tryCreate(packageName, className);

        if (printWriter != null) {
            try {
                generateFile();
            } finally {
                printWriter.close();
            }
        }

        return new ClassDefinition(packageName, className);
    }

    private void generateFile() {
        ClassSourceFileComposerFactory composer = initComposer();
        SourceWriter sourceWriter = composer.createSourceWriter(getContext(), printWriter);
        sourceWriter.commit(getLogger());
    }

    private ClassSourceFileComposerFactory initComposer() {
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, className);
        composer.addImport(AbstractDispatchRestEntryPoint.class.getName());
        composer.setSuperclass(AbstractDispatchRestEntryPoint.class.getSimpleName());

        return composer;
    }
}
