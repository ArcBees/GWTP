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

package com.gwtplatform.dispatch.rebind;

import java.io.PrintWriter;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.gwtplatform.dispatch.client.rest.RestDispatchAsync;
import com.gwtplatform.dispatch.rebind.type.ServiceDefinitions;

/**
 * TODO: Documentation.
 */
public class RestGinModuleGenerator extends AbstractVelocityGenerator {
    private static final String PACKAGE = RestDispatchAsync.class.getPackage().getName();
    private static final String DEFAULT_GIN_MODULE = "RestGinModule";
    private static final String VELOCITY_TEMPLATE = "com/gwtplatform/dispatch/rebind/RestGinModule.vm";

    private final ServiceDefinitions serviceDefinitions;

    @Inject
    public RestGinModuleGenerator(
            TypeOracle typeOracle,
            Logger logger,
            Provider<VelocityContext> velocityContextProvider,
            VelocityEngine velocityEngine,
            GeneratorUtil generatorUtil,
            ServiceDefinitions serviceDefinitions) {
        super(typeOracle, logger, velocityContextProvider, velocityEngine, generatorUtil);

        this.serviceDefinitions = serviceDefinitions;
    }

    public String generate() throws Exception {
        String implName = DEFAULT_GIN_MODULE;

        PrintWriter printWriter = getGeneratorUtil().tryCreatePrintWriter(PACKAGE, implName);
        if (printWriter != null) {
            mergeTemplate(printWriter, VELOCITY_TEMPLATE, implName);
        }

        return PACKAGE + "." + implName;
    }

    @Override
    protected String getPackage() {
        return PACKAGE;
    }

    @Override
    protected void populateVelocityContext(VelocityContext velocityContext) {
        velocityContext.put("serviceDefinitions", serviceDefinitions);
        velocityContext.put("package", PACKAGE);
        velocityContext.put("suffix", SUFFIX);
    }
}
