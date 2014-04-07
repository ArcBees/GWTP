/**
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

package com.gwtplatform.dispatch.rest.rebind;

import java.io.PrintWriter;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.inject.assistedinject.Assisted;
import com.gwtplatform.dispatch.rest.client.NoXsrfHeader;
import com.gwtplatform.dispatch.rest.rebind.type.ServiceBinding;
import com.gwtplatform.dispatch.rest.rebind.util.GeneratorUtil;

public class ServiceGenerator extends AbstractServiceGenerator {
    private final JClassType service;
    private final String path;

    private ServiceBinding serviceBinding;

    @Inject
    ServiceGenerator(
            TypeOracle typeOracle,
            Logger logger,
            Provider<VelocityContext> velocityContextProvider,
            VelocityEngine velocityEngine,
            GeneratorUtil generatorUtil,
            GeneratorFactory generatorFactory,
            @Assisted JClassType service) {
        super(typeOracle, logger, velocityContextProvider, velocityEngine, generatorUtil, generatorFactory, service);

        this.service = service;
        path = extractPath(service);
    }

    public ServiceBinding generate() throws UnableToCompleteException {
        String implName = service.getName() + SUFFIX;
        PrintWriter printWriter = getGeneratorUtil().tryCreatePrintWriter(getPackage(), implName);

        if (printWriter != null) {
            doGenerate(implName, printWriter);
        } else {
            getLogger().debug("Service already generated. Returning.");
        }

        return getServiceBinding();
    }

    @Override
    protected void populateVelocityContext(VelocityContext velocityContext) throws UnableToCompleteException {
        super.populateVelocityContext(velocityContext);

        velocityContext.put("injectable", true);
        velocityContext.put("ctorParams", new ArrayList<JParameter>());
    }

    @Override
    protected ServiceBinding getServiceBinding() {
        if (serviceBinding == null) {
            String implName = service.getName() + SUFFIX;

            serviceBinding = new ServiceBinding(path, getPackage(), implName, service.getName());
            serviceBinding.setSuperTypeName(service.getName());
            serviceBinding.setSecured(!service.isAnnotationPresent(NoXsrfHeader.class));
        }

        return serviceBinding;
    }

    private void doGenerate(String implName, PrintWriter printWriter) throws UnableToCompleteException {
        generateMethods();

        mergeTemplate(printWriter, TEMPLATE, implName);
    }
}
