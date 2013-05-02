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

package com.gwtplatform.carstore.rebind;

import java.io.PrintWriter;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;

public class VelocityColumnsInitializerGenerator extends AbstractVelocityGenerator {
    private static final String VELOCITY_TEMPLATE = "com/gwtplatform/carstore/rebind/ColumnsInitializer.vm";

    @Inject
    protected VelocityColumnsInitializerGenerator(
            Provider<VelocityContext> velocityContextProvider,
            VelocityEngine velocityEngine,
            GeneratorUtil generatorUtil) {
        super(velocityContextProvider, velocityEngine, generatorUtil);
    }

    @Override
    protected void populateVelocityContext(
            VelocityContext velocityContext,
            JClassType type) throws UnableToCompleteException {
        ColumnsInitializerDefinitions columnsInitializerDefinitions = ColumnsInitializerDefinitions.createFrom(type);

        velocityContext.put("columnsInitializerDefinitions", columnsInitializerDefinitions);
    }

    public String generate(JClassType type) throws Exception {
        String packageName = type.getPackage().getName();
        String implName = type.getName() + "Impl";

        PrintWriter printWriter = getGeneratorUtil().tryCreatePrintWriter(packageName, implName);
        if (printWriter != null) {
            mergeTemplate(printWriter, VELOCITY_TEMPLATE, type, type.getName(), packageName);
        }

        return packageName + "." + implName;
    }
}
