/*
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
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.velocity.app.VelocityEngine;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.gwtplatform.dispatch.rest.client.serialization.JacksonMapperProvider;
import com.gwtplatform.dispatch.rest.rebind.AbstractVelocityGenerator;
import com.gwtplatform.dispatch.rest.rebind.events.RegisterGinBindingEvent;
import com.gwtplatform.dispatch.rest.rebind.extension.ExtensionContext;
import com.gwtplatform.dispatch.rest.rebind.extension.ExtensionGenerator;
import com.gwtplatform.dispatch.rest.rebind.extension.ExtensionPoint;
import com.gwtplatform.dispatch.rest.rebind.utils.Arrays;
import com.gwtplatform.dispatch.rest.rebind.utils.ClassDefinition;
import com.gwtplatform.dispatch.rest.rebind.utils.EventBus;
import com.gwtplatform.dispatch.rest.rebind.utils.Logger;

import jakarta.inject.Inject;

public class JacksonMapperProviderGenerator extends AbstractVelocityGenerator
        implements ExtensionGenerator, JacksonMapperDefinitions {
    private static final String TEMPLATE =
            "com/gwtplatform/dispatch/rest/rebind/serialization/JacksonMapperProvider.vm";
    private static final String PACKAGE = JacksonMapperProvider.class.getPackage().getName();
    private static final String CLASS_NAME = JacksonMapperProvider.class.getSimpleName() + IMPL;

    private final Set<SerializationDefinition> serializerDefinitions;
    private final EventBus eventBus;

    @Inject
    JacksonMapperProviderGenerator(
            GeneratorContext context,
            Logger logger,
            VelocityEngine velocityEngine,
            EventBus eventBus) {
        super(logger, context, velocityEngine);

        this.serializerDefinitions = new HashSet<SerializationDefinition>();
        this.eventBus = eventBus;
    }

    @Override
    public void addDefinition(SerializationDefinition definition) {
        serializerDefinitions.add(definition);
    }

    @Override
    public boolean canGenerate(ExtensionContext context) {
        return context.getExtensionPoint() == ExtensionPoint.BEFORE_GIN;
    }

    @Override
    public Collection<ClassDefinition> generate(ExtensionContext input) throws UnableToCompleteException {
        PrintWriter printWriter = tryCreate();

        if (printWriter != null) {
            mergeTemplate(printWriter);
            commit(printWriter);
            registerGinBinding();
        } else {
            getLogger().debug("Jackson Mapper Provider already generated. Returning.");
        }

        return Arrays.asList(getClassDefinition());
    }

    @Override
    protected void populateTemplateVariables(Map<String, Object> variables) {
        variables.put("definitions", serializerDefinitions);
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
        return CLASS_NAME;
    }

    private void registerGinBinding() throws UnableToCompleteException {
        ClassDefinition definition = new ClassDefinition(getType(JacksonMapperProvider.class));
        ClassDefinition implementation = getClassDefinition();

        RegisterGinBindingEvent.postSingleton(eventBus, definition, implementation);
    }
}
