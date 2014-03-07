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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.gwtplatform.common.rebind.Logger;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.rebind.velocity.RebindModule;
import com.gwtplatform.mvp.rebind.velocity.proxy.VelocityPlacetokenGenerator;
import com.gwtplatform.mvp.shared.proxy.PlaceTokenRegistry;

/**
 * Generates an implementation of {@link PlaceTokenRegistry} based on GWTP's {@link NameToken} annotation.
 */
public class PlaceTokenRegistryGenerator extends Generator {
    @Override
    public String generate(final TreeLogger treeLogger, GeneratorContext generatorContext, String requestedClass)
            throws UnableToCompleteException {
        Map<String, JClassType> placeTokens = findPlaceTokens(generatorContext);
        checkPlaces(placeTokens);

        Injector injector = Guice.createInjector(new RebindModule(new Logger(treeLogger), generatorContext));
        VelocityPlacetokenGenerator.Factory factory = injector.getInstance(VelocityPlacetokenGenerator.Factory.class);
        VelocityPlacetokenGenerator generator = factory.create(placeTokens.keySet());

        try {
            return generator.generate();
        } catch (Exception e) {
            treeLogger.log(Type.ERROR, e.getMessage(), e);
            throw new UnableToCompleteException();
        }
    }

    /**
     * Finds all place tokens.
     */
    private static Map<String, JClassType> findPlaceTokens(final GeneratorContext generatorContext) {
        Map<String, JClassType> placeTokens = new HashMap<String, JClassType>();

        for (JClassType type : generatorContext.getTypeOracle().getTypes()) {
            NameToken nameTokenAnnotation = type.getAnnotation(NameToken.class);
            if (nameTokenAnnotation != null) {
                for (String nameToken : nameTokenAnnotation.value()) {
                    placeTokens.put(nameToken, type);
                }
            }
        }

        return placeTokens;
    }

    /**
     * Checks if the given place tokens are valid.
     */
    private static void checkPlaces(final Map<String, JClassType> placeTokens) {
        for (Map.Entry<String, JClassType> entry : placeTokens.entrySet()) {
            if (!entry.getKey().startsWith("/") && !entry.getKey().startsWith("!/")) {
                throw new InvocationException("The token '" + entry.getKey() + "' of '"
                        + entry.getValue().getQualifiedSourceName() + "' should start with a '/' or '!/'!");
            }
        }
    }
}
