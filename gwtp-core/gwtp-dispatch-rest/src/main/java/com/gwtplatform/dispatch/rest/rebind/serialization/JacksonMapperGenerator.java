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

package com.gwtplatform.dispatch.rest.rebind.serialization;

import java.io.PrintWriter;
import java.util.Map;

import javax.inject.Inject;

import org.apache.velocity.app.VelocityEngine;

import com.google.common.eventbus.EventBus;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JType;
import com.gwtplatform.dispatch.rest.client.serialization.JacksonMapperProvider;
import com.gwtplatform.dispatch.rest.rebind.AbstractVelocityGenerator;
import com.gwtplatform.dispatch.rest.rebind.GeneratorWithInput;
import com.gwtplatform.dispatch.rest.rebind.utils.ClassDefinition;
import com.gwtplatform.dispatch.rest.rebind.utils.Logger;

public class JacksonMapperGenerator extends AbstractVelocityGenerator
        implements GeneratorWithInput<JType, ClassDefinition> {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind/serialization/JacksonMapper.vm";

    private JType type;

    @Inject
    JacksonMapperGenerator(
            Logger logger,
            GeneratorContext context,
            VelocityEngine velocityEngine,
            EventBus eventBus) {
        super(logger, context, velocityEngine);

        eventBus.register(this);
    }

    @Override
    public boolean canGenerate(JType input) {
        return true;
    }

    @Override
    public ClassDefinition generate(JType type) throws UnableToCompleteException {
        this.type = type;

        PrintWriter printWriter = tryCreate();

        if (printWriter != null) {
            mergeTemplate(printWriter);
            commit(printWriter);
        } else {
            getLogger().debug("Jackson Mapper already generated. Returning.");
        }

        return getClassDefinition();
    }

    @Override
    protected void populateTemplateVariables(Map<String, Object> variables) {
        ClassDefinition typeDefinition = new ClassDefinition(type);

        variables.put("import", typeDefinition.getQualifiedName());
        variables.put("type", typeDefinition.getParameterizedClassName());
    }

    @Override
    protected String getTemplate() {
        return TEMPLATE;
    }

    @Override
    protected String getPackageName() {
        return JacksonMapperProvider.class.getPackage().getName() + ".mappers";
    }

    @Override
    protected String getImplName() {
        String implName = type.getParameterizedQualifiedSourceName();
        implName = implName.replaceAll("[ ,<>\\.\\?]", "_");
        implName += "Mapper";

        return implName;
    }
}
