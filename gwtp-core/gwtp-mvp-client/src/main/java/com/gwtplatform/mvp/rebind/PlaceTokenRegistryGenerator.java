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

package com.gwtplatform.mvp.rebind;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.proxy.PlaceTokenRegistry;

/**
 * Generates an implementation of {@link PlaceTokenRegistry} based on GWTP's {@link NameToken} annotation.
 */
public class PlaceTokenRegistryGenerator extends Generator {
    private static final String PACKAGE_NAME = PlaceTokenRegistry.class.getPackage().getName();

    private static final String SIMPLE_NAME = PlaceTokenRegistry.class.getSimpleName() + "Impl";

    private static final String NAME = PACKAGE_NAME + "." + SIMPLE_NAME;

    @Override
    public String generate(final TreeLogger logger, GeneratorContext generatorContext, String requestedClass)
            throws UnableToCompleteException {
        PrintWriter printWriter = generatorContext.tryCreate(logger, PACKAGE_NAME, SIMPLE_NAME);

        /*
         * printWriter is null when the type has already been generated
         */
        if (printWriter == null) {
            return NAME;
        }

        Map<String, JClassType> placeTokens = findPlaceTokens(generatorContext);
        checkPlaces(placeTokens);

        SourceWriter sourceWriter = setupType(generatorContext, printWriter);

        sourceWriter.println();
        sourceWriter.println("public Set<String> getAllPlaceTokens() {");
        sourceWriter.println("  Set<String> placeTokens = new HashSet<String>();");
        sourceWriter.println();
        for (String placeToken : placeTokens.keySet()) {
            sourceWriter.println("  placeTokens.add(\"" + placeToken + "\");");
        }
        sourceWriter.println();
        sourceWriter.println("  return placeTokens;");
        sourceWriter.println("}");
        sourceWriter.println();
        sourceWriter.commit(logger);

        return NAME;
    }

    /**
     * Initializes the new type and returns a {@link SourceWriter}.
     */
    private static SourceWriter setupType(final GeneratorContext generatorContext, final PrintWriter printWriter) {
        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(PACKAGE_NAME, SIMPLE_NAME);

        composerFactory.addImport("java.util.Set");
        composerFactory.addImport("java.util.HashSet");
        composerFactory.addImplementedInterface(PlaceTokenRegistry.class.getName());

        return composerFactory.createSourceWriter(generatorContext, printWriter);
    }

    /**
     * Finds all place tokens.
     */
    private static Map<String, JClassType> findPlaceTokens(final GeneratorContext generatorContext) {
        Map<String, JClassType> placeTokens = new HashMap<String, JClassType>();

        for (JClassType type : generatorContext.getTypeOracle().getTypes()) {
            NameToken nameTokenAnnotation = type.getAnnotation(NameToken.class);
            if (nameTokenAnnotation != null) {
                placeTokens.put(nameTokenAnnotation.value(), type);
            }
        }

        return placeTokens;
    }

    /**
     * Checks if the given place tokens are valid.
     */
    private static void checkPlaces(final Map<String, JClassType> placeTokens) {
        for (Map.Entry<String, JClassType> entry : placeTokens.entrySet()) {
            if (!entry.getKey().startsWith("/")) {
                throw new InvocationException("The token '" + entry.getKey() + "' of '"
                        + entry.getValue().getQualifiedSourceName() + "' should start with a '/'!");
            }
        }
    }
}
