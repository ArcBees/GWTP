/*
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

package com.gwtplatform.mvp.rebind.velocity.proxy;

import java.io.PrintWriter;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.gwtplatform.mvp.rebind.velocity.AbstractVelocityGenerator;
import com.gwtplatform.mvp.rebind.velocity.GeneratorUtil;
import com.gwtplatform.mvp.shared.proxy.PlaceTokenRegistry;

import jakarta.inject.Provider;

/**
 * Velocity powered generator of an {@link PlaceTokenRegistry}.
 */
public class VelocityPlacetokenGenerator extends AbstractVelocityGenerator {
    public interface Factory {
        VelocityPlacetokenGenerator create(Set<String> placeTokens);
    }

    private static final String PACKAGE_NAME = PlaceTokenRegistry.class.getPackage().getName()
            .replace(".shared.", ".client.");
    private static final String SIMPLE_NAME = PlaceTokenRegistry.class.getSimpleName() + "Impl";
    private static final String FULL_NAME = PACKAGE_NAME + "." + SIMPLE_NAME;
    private static final String TEMPLATE = "com/gwtplatform/mvp/rebind/PlaceTokenRegistryImpl.vm";

    private final Set<String> placeTokens;

    @AssistedInject
    VelocityPlacetokenGenerator(
            Provider<VelocityContext> velocityContextProvider,
            VelocityEngine velocityEngine,
            GeneratorUtil generatorUtil,
            @Assisted Set<String> placeTokens) throws UnableToCompleteException {
        super(velocityContextProvider, velocityEngine, generatorUtil);

        this.placeTokens = placeTokens;
    }

    public String generate() throws Exception {
        PrintWriter printWriter = getGeneratorUtil().tryCreatePrintWriter(PACKAGE_NAME, SIMPLE_NAME);
        if (printWriter != null) {
            try {
                mergeTemplate(printWriter, TEMPLATE);
            } finally {
                printWriter.close();
            }
        }

        return FULL_NAME;
    }

    @Override
    protected void populateVelocityContext(VelocityContext velocityContext) throws UnableToCompleteException {
        velocityContext.put("packageName", PACKAGE_NAME);
        velocityContext.put("className", SIMPLE_NAME);
        velocityContext.put("placeTokens", placeTokens);
    }
}
