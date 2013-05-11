/*
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.carstore.rebind;

import java.io.PrintWriter;

import javax.inject.Provider;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;

public abstract class AbstractVelocityGenerator {
    private final static String IMPL_NAME = "implName";
    private final static String PACKAGE_NAME = "packageName";
    private final static String ENCODING = "UTF-8";

    private final Provider<VelocityContext> velocityContextProvider;
    private final VelocityEngine velocityEngine;
    private final GeneratorUtil generatorUtil;

    protected AbstractVelocityGenerator(Provider<VelocityContext> velocityContextProvider,
                                        VelocityEngine velocityEngine,
                                        GeneratorUtil generatorUtil) {

        this.velocityContextProvider = velocityContextProvider;
        this.velocityEngine = velocityEngine;
        this.generatorUtil = generatorUtil;
    }

    public GeneratorUtil getGeneratorUtil() {
        return generatorUtil;
    }

    protected void mergeTemplate(PrintWriter printWriter,
                                 String velocityTemplate,
                                 JClassType descriptor,
                                 String implName,
                                 String packageName) throws Exception {

        VelocityContext velocityContext = velocityContextProvider.get();
        velocityContext.put(IMPL_NAME, implName);
        velocityContext.put(PACKAGE_NAME, packageName);

        populateVelocityContext(velocityContext, descriptor);

        velocityEngine.mergeTemplate(velocityTemplate, ENCODING, velocityContext, printWriter);
        generatorUtil.closeDefinition(printWriter);
    }

    protected abstract void populateVelocityContext(VelocityContext velocityContext,
                                                    JClassType descriptor) throws UnableToCompleteException;
}
