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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.Lists;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.inject.assistedinject.Assisted;
import com.gwtplatform.dispatch.rest.client.NoXsrfHeader;
import com.gwtplatform.dispatch.rest.rebind.type.ChildServiceBinding;
import com.gwtplatform.dispatch.rest.rebind.type.ServiceBinding;
import com.gwtplatform.dispatch.rest.rebind.util.GeneratorUtil;

public class ChildServiceGenerator extends AbstractServiceGenerator {
    private final List<JParameter> parameters;
    private final JMethod serviceMethod;
    private final ServiceBinding parent;
    private final JClassType service;
    private final String path;

    private ServiceBinding serviceBinding;

    @Inject
    ChildServiceGenerator(
            TypeOracle typeOracle,
            Logger logger,
            Provider<VelocityContext> velocityContextProvider,
            VelocityEngine velocityEngine,
            GeneratorUtil generatorUtil,
            GeneratorFactory generatorFactory,
            @Assisted JMethod serviceMethod,
            @Assisted ServiceBinding parent) {
        super(typeOracle, logger, velocityContextProvider, velocityEngine, generatorUtil, generatorFactory,
                serviceMethod.getReturnType().isInterface());

        this.serviceMethod = serviceMethod;
        this.parent = parent;
        this.parameters = Lists.newArrayList(parent.getCtorParameters());
        service = serviceMethod.getReturnType().isInterface();
        path = concatenatePath(parent.getResourcePath(), extractPath(serviceMethod));
    }

    public ServiceBinding generate() throws UnableToCompleteException {
        String implName = getSuperTypeName() + SUFFIX;
        PrintWriter printWriter = getGeneratorUtil().tryCreatePrintWriter(getPackage(), implName);

        if (printWriter != null) {
            doGenerate(implName, printWriter);
        } else {
            getLogger().debug("Sub-service already generated. Returning.");
        }

        return getServiceBinding();
    }

    @Override
    protected ServiceBinding getServiceBinding() {
        if (serviceBinding == null) {
            String implName = getSuperTypeName() + SUFFIX;

            serviceBinding = new ChildServiceBinding(path, getPackage(), implName, service.getName(),
                    serviceMethod.getName(), parameters);
            serviceBinding.setSuperTypeName(getSuperTypeName());
            serviceBinding.setSecured(isSecured());
        }

        return serviceBinding;
    }

    @Override
    protected void populateVelocityContext(VelocityContext velocityContext) throws UnableToCompleteException {
        super.populateVelocityContext(velocityContext);

        velocityContext.put("injectable", false);
        velocityContext.put("ctorParams", parameters);
    }

    private String getSuperTypeName() {
        // The service may define overloads, in which case the method name is not unique.
        // The method index will ensure the generated class uniqueness.
        int methodIndex = Arrays.asList(serviceMethod.getEnclosingType().getMethods()).indexOf(serviceMethod);

        return parent.getSuperTypeName() + "_" + methodIndex + "_" + service.getName();
    }

    private void doGenerate(String implName, PrintWriter printWriter) throws UnableToCompleteException {
        Collections.addAll(parameters, serviceMethod.getParameters());

        generateMethods();

        mergeTemplate(printWriter, TEMPLATE, implName);
    }

    private boolean isSecured() {
        return parent.isSecured()
                && !serviceMethod.isAnnotationPresent(NoXsrfHeader.class)
                && !service.isAnnotationPresent(NoXsrfHeader.class);
    }
}
