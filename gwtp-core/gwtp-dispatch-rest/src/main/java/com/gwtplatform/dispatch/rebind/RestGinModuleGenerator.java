/*
 * Copyright (c) 2012 by Zafin Labs, All rights reserved.
 * This source code, and resulting software, is the confidential and proprietary information
 * ("Proprietary Information") and is the intellectual property ("Intellectual Property")
 * of Zafin Labs ("The Company"). You shall not disclose such Proprietary Information and
 * shall use it only in accordance with the terms and conditions of any and all license
 * agreements you have entered into with The Company.
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
