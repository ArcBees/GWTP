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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JType;
import com.gwtplatform.dispatch.rest.client.serialization.JacksonMapperProvider;
import com.gwtplatform.dispatch.rest.rebind.AbstractVelocityGenerator;
import com.gwtplatform.dispatch.rest.rebind.events.RegisterGinBindingEvent;
import com.gwtplatform.dispatch.rest.rebind.events.RegisterSerializableTypeEvent;
import com.gwtplatform.dispatch.rest.rebind.extension.ExtensionContext;
import com.gwtplatform.dispatch.rest.rebind.extension.ExtensionGenerator;
import com.gwtplatform.dispatch.rest.rebind.extension.ExtensionPoint;
import com.gwtplatform.dispatch.rest.rebind.utils.ClassDefinition;
import com.gwtplatform.dispatch.rest.rebind.utils.Logger;

public class JacksonMapperProviderGenerator extends AbstractVelocityGenerator implements ExtensionGenerator {
    private static final String TEMPLATE =
            "com/gwtplatform/dispatch/rest/rebind/serialization/JacksonMapperProvider.vm";

    private final Map<JType, ClassDefinition> registeredTypes;
    private final EventBus eventBus;
    private final JacksonMapperGenerator jacksonMapperGenerator;

    @Inject
    JacksonMapperProviderGenerator(
            GeneratorContext context,
            Logger logger,
            VelocityEngine velocityEngine,
            EventBus eventBus,
            JacksonMapperGenerator jacksonMapperGenerator) {
        super(logger, context, velocityEngine);

        this.registeredTypes = Maps.newHashMap();
        this.eventBus = eventBus;
        this.jacksonMapperGenerator = jacksonMapperGenerator;

        eventBus.register(this);
    }

    @Override
    public boolean canGenerate(ExtensionContext context) {
        return context.getExtensionPoint() == ExtensionPoint.BEFORE_GIN;
    }

    @Override
    public Collection<ClassDefinition> generate(ExtensionContext input) throws UnableToCompleteException {
        PrintWriter printWriter = tryCreate();

        if (printWriter != null) {
            for (JType type : registeredTypes.keySet()) {
                ClassDefinition mapperDefinition = jacksonMapperGenerator.generate(type);
                registeredTypes.put(type, mapperDefinition);
            }

            mergeTemplate(printWriter);
            commit(printWriter);
            registerGinBinding();
        } else {
            getLogger().debug("Jackson Mapper Provider already generated. Returning.");
        }

        List<ClassDefinition> definitions = Lists.newArrayList(getClassDefinition());
        definitions.addAll(registeredTypes.values());
        return definitions;
    }

    @Subscribe
    public void onRegisterSerializableType(RegisterSerializableTypeEvent event) {
        registeredTypes.put(event.getType(), null);
    }

    @Override
    protected void populateTemplateVariables(Map<String, Object> variables) {
        variables.put("types", registeredTypes);
    }

    @Override
    protected String getTemplate() {
        return TEMPLATE;
    }

    @Override
    protected String getPackageName() {
        return JacksonMapperProvider.class.getPackage().getName();
    }

    @Override
    protected String getImplName() {
        return JacksonMapperProvider.class.getSimpleName() + IMPL;
    }

    private void registerGinBinding() throws UnableToCompleteException {
        ClassDefinition definition = new ClassDefinition(getType(JacksonMapperProvider.class));
        ClassDefinition implementation = getClassDefinition();

        RegisterGinBindingEvent.postSingleton(eventBus, definition, implementation);
    }
}
