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

package com.gwtplatform.dispatch.rest.rebind.resource;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.Sets;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.gwtplatform.dispatch.rest.rebind.AbstractVelocityGenerator;
import com.gwtplatform.dispatch.rest.rebind.utils.Arrays;
import com.gwtplatform.dispatch.rest.rebind.utils.ClassDefinition;
import com.gwtplatform.dispatch.rest.rebind.utils.Logger;

import static com.gwtplatform.dispatch.rest.rebind.utils.Generators.findGenerator;
import static com.gwtplatform.dispatch.rest.rebind.utils.Generators.getGenerator;

public abstract class AbstractResourceGenerator extends AbstractVelocityGenerator implements ResourceGenerator {
    private final Set<MethodGenerator> methodGenerators;

    private Set<String> imports;
    private ResourceContext resourceContext;

    protected AbstractResourceGenerator(
            Logger logger,
            GeneratorContext context,
            VelocityEngine velocityEngine, Set<MethodGenerator> methodGenerators) {
        super(logger, context, velocityEngine);

        this.methodGenerators = methodGenerators;
    }

    @Override
    public boolean canGenerate(ResourceContext resourceContext) {
        return canGenerateAllMethods();
    }

    @Override
    public ResourceDefinition generate(ResourceContext context) throws UnableToCompleteException {
        setContext(context);

        PrintWriter printWriter = tryCreate();
        if (printWriter != null) {
            imports = Sets.newTreeSet();
            imports.add(getResourceType().getQualifiedSourceName());

            generateMethods();

            mergeTemplate(printWriter);
            commit(printWriter);
        }

        return getResourceDefinition();
    }

    @Override
    protected void populateTemplateVariables(Map<String, Object> variables) {
        variables.put("imports", imports);
        variables.put("resourceType", new ClassDefinition(getResourceType()).getParameterizedClassName());
        variables.put("methods", getResourceDefinition().getMethodDefinitions());
    }

    @Override
    protected String getPackageName() {
        return getResourceType().getPackage().getName();
    }

    protected void setContext(ResourceContext resourceContext) {
        this.resourceContext = resourceContext;
    }

    protected ResourceContext getResourceContext() {
        return resourceContext;
    }

    protected JClassType getResourceType() {
        return resourceContext.getResourceType();
    }

    protected void generateMethods() throws UnableToCompleteException {
        List<JMethod> methods = Arrays.asList(getResourceType().getInheritableMethods());

        for (JMethod enclosedMethod : methods) {
            generateMethod(enclosedMethod);
        }
    }

    protected void generateMethod(JMethod method) throws UnableToCompleteException {
        MethodContext methodContext =
                new MethodContext(getResourceDefinition(), getResourceContext(), method);
        MethodGenerator generator = getGenerator(getLogger(), methodGenerators, methodContext);
        MethodDefinition methodDefinition = generator.generate(methodContext);

        getResourceDefinition().addMethodDefinition(methodDefinition);
        imports.addAll(methodDefinition.getImports());
    }

    protected abstract ResourceDefinition getResourceDefinition();

    private boolean canGenerateAllMethods() {
        List<JMethod> methods = Arrays.asList(getResourceType().getInheritableMethods());
        boolean canGenerate = true;

        for (JMethod enclosedMethod : methods) {
            MethodContext methodContext = new MethodContext(null, getResourceContext(), enclosedMethod);
            MethodGenerator generator = findGenerator(methodGenerators, methodContext);

            if (generator == null) {
                canGenerate = false;
                getLogger().debug("Cannot find a resource method generator for `%s#%s`.",
                        getResourceType().getQualifiedSourceName(), enclosedMethod.getName());
            }
        }

        return canGenerate;
    }
}
