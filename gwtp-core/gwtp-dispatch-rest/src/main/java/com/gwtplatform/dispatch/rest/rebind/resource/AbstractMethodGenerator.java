/*
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

package com.gwtplatform.dispatch.rest.rebind.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.app.VelocityEngine;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.gwtplatform.dispatch.rest.rebind.AbstractVelocityGenerator;
import com.gwtplatform.dispatch.rest.rebind.Parameter;
import com.gwtplatform.dispatch.rest.rebind.subresource.SubResourceDefinition;
import com.gwtplatform.dispatch.rest.rebind.utils.Arrays;
import com.gwtplatform.dispatch.rest.rebind.utils.Logger;

public abstract class AbstractMethodGenerator extends AbstractVelocityGenerator implements MethodGenerator {
    private MethodContext methodContext;

    protected AbstractMethodGenerator(
            Logger logger,
            GeneratorContext context,
            VelocityEngine velocityEngine) {
        super(logger, context, velocityEngine);
    }

    @Override
    protected String getPackageName() {
        return getParentDefinition().getPackageName();
    }

    @Override
    protected String getImplName() {
        return getParentDefinition().getClassName() + "#" + getMethod().getName();
    }

    protected void setContext(MethodContext methodContext) {
        this.methodContext = methodContext;
    }

    protected MethodContext getMethodContext() {
        return methodContext;
    }

    protected JMethod getMethod() {
        return methodContext.getMethod();
    }

    protected ResourceDefinition getParentDefinition() {
        return methodContext.getResourceDefinition();
    }

    protected List<Parameter> resolveParameters() {
        List<JParameter> jParameters = Arrays.asList(getMethod().getParameters());
        List<Parameter> parameters = new ArrayList<Parameter>();

        for (JParameter jParameter : jParameters) {
            parameters.add(new Parameter(jParameter));
        }

        return parameters;
    }

    protected List<Parameter> resolveInheritedParameters() {
        List<Parameter> inheritedParameters;

        if (getParentDefinition() instanceof SubResourceDefinition) {
            inheritedParameters = ((SubResourceDefinition) getParentDefinition()).getParameters();
        } else {
            inheritedParameters = new ArrayList<Parameter>();
        }

        return inheritedParameters;
    }
}
