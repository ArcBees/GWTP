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

package com.gwtplatform.mvp.rebind.velocity;

import java.io.PrintWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.gwt.core.ext.UnableToCompleteException;

import jakarta.inject.Provider;

public abstract class AbstractVelocityGenerator {
    protected static final String IMPL_NAME = "implName";
    protected static final String PACKAGE = "com.gwtplatform.mvp.client";

    private final Provider<VelocityContext> velocityContextProvider;
    private final VelocityEngine velocityEngine;
    private final GeneratorUtil generatorUtil;

    protected AbstractVelocityGenerator(
            Provider<VelocityContext> velocityContextProvider,
            VelocityEngine velocityEngine,
            GeneratorUtil generatorUtil) {
        this.velocityContextProvider = velocityContextProvider;
        this.velocityEngine = velocityEngine;
        this.generatorUtil = generatorUtil;
    }

    public GeneratorUtil getGeneratorUtil() {
        return generatorUtil;
    }

    protected void mergeTemplate(PrintWriter printWriter, String velocityTemplate) throws Exception {

        VelocityContext velocityContext = velocityContextProvider.get();

        populateVelocityContext(velocityContext);

        velocityEngine.mergeTemplate(velocityTemplate, "UTF-8", velocityContext, printWriter);
        generatorUtil.closeDefinition(printWriter);
    }

    protected abstract void populateVelocityContext(VelocityContext velocityContext) throws UnableToCompleteException;
}
