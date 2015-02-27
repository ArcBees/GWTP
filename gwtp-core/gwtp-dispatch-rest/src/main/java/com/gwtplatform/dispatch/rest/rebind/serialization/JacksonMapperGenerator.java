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
import java.util.Set;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.apache.velocity.app.VelocityEngine;

import com.google.common.eventbus.EventBus;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JType;
import com.gwtplatform.dispatch.rest.client.serialization.JacksonMapperProvider;
import com.gwtplatform.dispatch.rest.rebind.AbstractVelocityGenerator;
import com.gwtplatform.dispatch.rest.rebind.utils.ClassDefinition;
import com.gwtplatform.dispatch.rest.rebind.utils.Logger;

import static com.gwtplatform.dispatch.rest.rebind.utils.JPrimitives.classTypeOrConvertToBoxed;

public class JacksonMapperGenerator extends AbstractVelocityGenerator implements SerializationGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind/serialization/JacksonMapper.vm";
    private static final Pattern SANITIZE_NAME_PATTERN = Pattern.compile("[^a-zA-Z0-9_]");
    private static final String PACKAGE = JacksonMapperProvider.class.getPackage().getName() + ".mappers";
    private static final String NAME_SUFFIX = "Mapper";
    private static final String APPLICATION_WILDCARD = "application/*";

    private final JacksonMapperProviderGenerator jacksonMapperProviderGenerator;

    private JType type;

    @Inject
    JacksonMapperGenerator(
            Logger logger,
            GeneratorContext context,
            VelocityEngine velocityEngine,
            EventBus eventBus,
            JacksonMapperProviderGenerator jacksonMapperProviderGenerator) {
        super(logger, context, velocityEngine);

        this.jacksonMapperProviderGenerator = jacksonMapperProviderGenerator;

        eventBus.register(this);
    }

    @Override
    public boolean canGenerate(SerializationContext context) {
        Set<String> contentTypes = context.getContentTypes();
        return contentTypes.contains(MediaType.WILDCARD)
                || contentTypes.contains(APPLICATION_WILDCARD)
                || contentTypes.contains(MediaType.APPLICATION_JSON);
    }

    @Override
    public SerializationDefinition generate(SerializationContext context) throws UnableToCompleteException {
        this.type = classTypeOrConvertToBoxed(getContext().getTypeOracle(), context.getType());

        PrintWriter printWriter = tryCreate();
        if (printWriter != null) {
            mergeTemplate(printWriter);
            commit(printWriter);

            jacksonMapperProviderGenerator.addDefinition(getClassDefinition());
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
    protected SerializationDefinition getClassDefinition() {
        return new SerializationDefinition(getPackageName(), getImplName(), type);
    }

    @Override
    protected String getTemplate() {
        return TEMPLATE;
    }

    @Override
    protected String getPackageName() {
        return PACKAGE;
    }

    @Override
    protected String getImplName() {
        String implName = type.getParameterizedQualifiedSourceName();
        implName = SANITIZE_NAME_PATTERN.matcher(implName).replaceAll("_");
        implName += NAME_SUFFIX;

        return implName;
    }
}
