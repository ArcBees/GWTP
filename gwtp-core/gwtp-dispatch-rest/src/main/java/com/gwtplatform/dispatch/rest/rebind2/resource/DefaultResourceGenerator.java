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

package com.gwtplatform.dispatch.rest.rebind2.resource;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.gwtplatform.dispatch.rest.rebind2.AbstractVelocityGenerator;
import com.gwtplatform.dispatch.rest.rebind2.events.RegisterGinBindingEvent;
import com.gwtplatform.dispatch.rest.rebind2.utils.ClassDefinition;
import com.gwtplatform.dispatch.rest.rebind2.utils.Logger;

public class DefaultResourceGenerator extends AbstractVelocityGenerator implements ResourceGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind2/resource/Resource.vm";

    private final EventBus eventBus;

    private String packageName;
    private String implName;
    private String resourceType;
    private List<String> imports;
    private JClassType classType;

    @Inject
    DefaultResourceGenerator(
            Logger logger,
            GeneratorContext context,
            EventBus eventBus,
            VelocityEngine velocityEngine) {
        super(logger, context, velocityEngine);

        this.eventBus = eventBus;
    }

    @Override
    public boolean canGenerate(JClassType classType) throws UnableToCompleteException {
        return classType.isInterface() != null && classType.isAnnotationPresent(Path.class);
    }

    @Override
    public ClassDefinition generate(JClassType classType) throws UnableToCompleteException {
        this.classType = classType;
        resourceType = classType.getSimpleSourceName();
        packageName = classType.getPackage().getName();
        implName = resourceType + IMPL;

        PrintWriter printWriter = tryCreate();
        if (printWriter != null) {
            imports = Lists.newArrayList(classType.getQualifiedSourceName());

            mergeTemplate(printWriter);
            getContext().commit(getLogger(), printWriter);

            registerResourceBinding();
        }

        return getClassDefinition();
    }

    private void registerResourceBinding() {
        RegisterGinBindingEvent event =
                new RegisterGinBindingEvent(new ClassDefinition(classType), getClassDefinition(), true);
        eventBus.post(event);
    }

    @Override
    protected Map<String, Object> createTemplateVariables() {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("imports", imports);
        variables.put("resourceType", resourceType);

        // TODO: Stub to make compile work
        List<String> methodDefs = Lists.newArrayList();
        JMethod[] methods = classType.getInheritableMethods();
        if (methods != null) {
            for (JMethod method : methods) {
                methodDefs.add(method.getReadableDeclaration(false, true, true, true, true) + " { return null; }");
            }
        }
        variables.put("methods", methodDefs);
        // end

        return variables;
    }

    @Override
    protected String getTemplate() {
        return TEMPLATE;
    }

    @Override
    protected String getPackageName() {
        return packageName;
    }

    @Override
    protected String getImplName() {
        return implName;
    }
}
