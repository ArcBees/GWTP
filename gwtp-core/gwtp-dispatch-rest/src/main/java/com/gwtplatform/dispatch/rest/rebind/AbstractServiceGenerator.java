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

import java.util.List;

import javax.inject.Provider;
import javax.ws.rs.Path;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.Lists;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.gwtplatform.dispatch.rest.rebind.type.ActionBinding;
import com.gwtplatform.dispatch.rest.rebind.type.ServiceBinding;
import com.gwtplatform.dispatch.rest.rebind.util.GeneratorUtil;
import com.gwtplatform.dispatch.rest.shared.RestAction;

public abstract class AbstractServiceGenerator extends AbstractVelocityGenerator {
    protected static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind/RestService.vm";

    private final List<ActionBinding> actionBindings = Lists.newArrayList();
    private final List<ServiceBinding> serviceBindings = Lists.newArrayList();
    private final GeneratorFactory generatorFactory;
    private final JClassType service;

    protected AbstractServiceGenerator(
            TypeOracle typeOracle,
            Logger logger,
            Provider<VelocityContext> velocityContextProvider,
            VelocityEngine velocityEngine,
            GeneratorUtil generatorUtil,
            GeneratorFactory generatorFactory,
            JClassType service) {
        super(typeOracle, logger, velocityContextProvider, velocityEngine, generatorUtil);

        this.generatorFactory = generatorFactory;
        this.service = service;
    }

    @Override
    protected String getPackage() {
        return service.getPackage().getName().replace(SHARED_PACKAGE, CLIENT_PACKAGE);
    }

    protected abstract ServiceBinding getServiceBinding();

    @Override
    protected void populateVelocityContext(VelocityContext velocityContext) throws UnableToCompleteException {
        velocityContext.put("serviceInterface", service);
        velocityContext.put("actionBindings", actionBindings);
        velocityContext.put("serviceBindings", serviceBindings);
    }

    protected void generateMethods() throws UnableToCompleteException {
        JMethod[] methods = service.getInheritableMethods();
        if (methods != null) {
            for (JMethod method : methods) {
                generateMethodHierarchy(method);
            }
        }
    }

    protected void generateMethodHierarchy(JMethod method) throws UnableToCompleteException {
        if (isAction(method)) {
            generateRestAction(method);
        } else if (isSubService(method)) {
            generateChildRestService(method);
        } else {
            String methodName = method.getEnclosingType().getQualifiedSourceName() + "#" + method.getName() + "(...)";
            getLogger().die(methodName + " should return either a RestAction<> or a Sub-Resource.");
        }
    }

    protected boolean isAction(JMethod method) throws UnableToCompleteException {
        JClassType actionClass = getTypeOracle().findType(RestAction.class.getName());
        JClassType returnClass = method.getReturnType().isClassOrInterface();

        return returnClass != null && returnClass.isAssignableTo(actionClass);
    }

    protected boolean isSubService(JMethod method) throws UnableToCompleteException {
        JClassType returnInterface = method.getReturnType().isInterface();

        return returnInterface != null && method.isAnnotationPresent(Path.class);
    }

    protected void generateChildRestService(JMethod method) throws UnableToCompleteException {
        ChildServiceGenerator generator = generatorFactory.createChildServiceGenerator(method, getServiceBinding());
        serviceBindings.add(generator.generate());
    }

    protected void generateRestAction(JMethod method) throws UnableToCompleteException {
        ActionGenerator generator = generatorFactory.createActionGenerator(method, getServiceBinding());
        actionBindings.add(generator.generate());
    }
}
