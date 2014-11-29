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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.gwtplatform.dispatch.rest.client.ActionMetadataProvider;
import com.gwtplatform.dispatch.rest.client.MetadataType;
import com.gwtplatform.dispatch.rest.rebind.AbstractVelocityGenerator;
import com.gwtplatform.dispatch.rest.rebind.events.RegisterGinBindingEvent;
import com.gwtplatform.dispatch.rest.rebind.events.RegisterMetadataEvent;
import com.gwtplatform.dispatch.rest.rebind.extension.ExtensionContext;
import com.gwtplatform.dispatch.rest.rebind.extension.ExtensionGenerator;
import com.gwtplatform.dispatch.rest.rebind.extension.ExtensionPoint;
import com.gwtplatform.dispatch.rest.rebind.utils.ClassDefinition;
import com.gwtplatform.dispatch.rest.rebind.utils.Logger;

public class ActionMetadataProviderGenerator extends AbstractVelocityGenerator implements ExtensionGenerator {
    private static final String TEMPLATE =
            "com/gwtplatform/dispatch/rest/rebind/serialization/ActionMetadataProvider.vm";

    private final Set<RegisterMetadataHolder> registeredMetadata;
    private final EventBus eventBus;

    @Inject
    ActionMetadataProviderGenerator(
            Logger logger,
            GeneratorContext context,
            VelocityEngine velocityEngine,
            EventBus eventBus) {
        super(logger, context, velocityEngine);

        this.registeredMetadata = new HashSet<RegisterMetadataHolder>();
        this.eventBus = eventBus;

        eventBus.register(this);
    }

    @Override
    public boolean canGenerate(ExtensionContext context) {
        return context.getExtensionPoint() == ExtensionPoint.BEFORE_GIN;
    }

    @Override
    public Collection<ClassDefinition> generate(ExtensionContext context) throws UnableToCompleteException {
        PrintWriter printWriter = tryCreate();
        if (printWriter != null) {
            mergeTemplate(printWriter);
            commit(printWriter);
            registerGinBinding();
        } else {
            getLogger().debug("Action metadata provider already generated. Returning.");
        }

        return Lists.newArrayList(getClassDefinition());
    }

    @Subscribe
    public void handleRegisterMetadata(RegisterMetadataEvent event) {
        String metadata = event.getMetadata();
        MetadataType metadataType = event.getMetadataType();
        String actionClass = event.getActionClass();

        RegisterMetadataHolder metadataHolder = new RegisterMetadataHolder(actionClass, metadataType, metadata);
        getLogger().debug("Metadata " + metadata + " registered.");

        registeredMetadata.add(metadataHolder);
    }

    @Override
    protected String getTemplate() {
        return TEMPLATE;
    }

    @Override
    protected String getImplName() {
        return ActionMetadataProvider.class.getSimpleName() + IMPL;
    }

    @Override
    protected String getPackageName() {
        return ActionMetadataProvider.class.getPackage().getName();
    }

    @Override
    protected void populateTemplateVariables(Map<String, Object> variables) {
        variables.put("metadata", registeredMetadata);
    }

    private void registerGinBinding() throws UnableToCompleteException {
        ClassDefinition definition = new ClassDefinition(getType(ActionMetadataProvider.class));
        ClassDefinition implementation = getClassDefinition();

        RegisterGinBindingEvent.postSingleton(eventBus, definition, implementation);
    }
}
