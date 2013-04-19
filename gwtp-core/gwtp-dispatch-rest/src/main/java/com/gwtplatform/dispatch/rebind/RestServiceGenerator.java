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
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Path;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.inject.assistedinject.Assisted;
import com.gwtplatform.dispatch.rebind.type.ActionBinding;

/**
 * TODO: Documentation.
 */
public class RestServiceGenerator extends AbstractVelocityGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rebind/RestService.vm";

    private List<ActionBinding> actionBindings;
    private GeneratorFactory generatorFactory;
    private JClassType service;

    @Inject
    public RestServiceGenerator(
            TypeOracle typeOracle,
            Logger logger,
            Provider<VelocityContext> velocityContextProvider,
            VelocityEngine velocityEngine,
            GeneratorUtil generatorUtil,
            GeneratorFactory generatorFactory,
            @Assisted JClassType service) {
        super(typeOracle, logger, velocityContextProvider, velocityEngine, generatorUtil);

        actionBindings = new ArrayList<ActionBinding>();
        this.generatorFactory = generatorFactory;
        this.service = service;
    }

    public void generate() throws Exception {
        generateActions();

        String implName = service.getName() + SUFFIX;
        PrintWriter printWriter = getGeneratorUtil().tryCreatePrintWriter(getPackage(), implName);

        if (printWriter != null) {
            mergeTemplate(printWriter, TEMPLATE, implName);
        } else {
            getLogger().debug("Serializer already generated. Returning.");
        }
    }

    @Override
    protected String getPackage() {
        return service.getPackage().getName().replace(SHARED_PACKAGE, CLIENT_PACKAGE);
    }

    @Override
    protected void populateVelocityContext(VelocityContext velocityContext)
            throws UnableToCompleteException {
        velocityContext.put("serviceInterface", service);
        velocityContext.put("actionBindings", actionBindings);
    }

    private void generateActions() throws UnableToCompleteException {
        JMethod[] actionMethods = service.getMethods();
        if (actionMethods != null) {
            for (JMethod actionMethod : actionMethods) {
                generateRestAction(actionMethod);
            }
        }
    }

    private void generateRestAction(JMethod actionMethod) throws UnableToCompleteException {
        RestActionGenerator generator = generatorFactory.createActionGenerator(actionMethod);
        try {
            String baseRestPath = getBaseRestPath();
            actionBindings.add(generator.generate(baseRestPath));
        } catch (Exception e) {
            throw new UnableToCompleteException();
        }
    }

    private String getBaseRestPath() {
        String baseRestPath = "";
        if (service.isAnnotationPresent(Path.class)) {
            baseRestPath = normalizePath(service.getAnnotation(Path.class).value());
        }
        return baseRestPath;
    }
}
