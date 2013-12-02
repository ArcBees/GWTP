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

package com.gwtplatform.dispatch.rest.rebind;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.gwtplatform.dispatch.rest.client.ActionMetadataProvider;
import com.gwtplatform.dispatch.rest.rebind.event.RegisterMetadataEvent;
import com.gwtplatform.dispatch.rest.rebind.type.RegisterMetadataHolder;
import com.gwtplatform.dispatch.rest.rebind.util.GeneratorUtil;
import com.gwtplatform.dispatch.rest.shared.MetadataType;

public class ActionMetadataProviderGenerator extends AbstractVelocityGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind/ActionMetadataProvider.vm";

    private final Set<RegisterMetadataHolder> registeredMetadata = new HashSet<RegisterMetadataHolder>();

    @Inject
    ActionMetadataProviderGenerator(TypeOracle typeOracle,
                                    Logger logger,
                                    Provider<VelocityContext> velocityContextProvider,
                                    VelocityEngine velocityEngine,
                                    GeneratorUtil generatorUtil,
                                    EventBus eventBus) {
        super(typeOracle, logger, velocityContextProvider, velocityEngine, generatorUtil);

        eventBus.register(this);
    }

    public void generate() throws UnableToCompleteException {
        String implName = ActionMetadataProvider.class.getSimpleName() + SUFFIX;
        PrintWriter printWriter = getGeneratorUtil().tryCreatePrintWriter(getPackage(), implName);
        if (printWriter != null) {
            try {
                mergeTemplate(printWriter, TEMPLATE, implName);
            } catch (Exception e) {
                getLogger().die(e.getMessage());
            }
        } else {
            getLogger().debug("Action metadata provider already generated. Returning.");
        }
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
    protected String getPackage() {
        return ActionMetadataProvider.class.getPackage().getName();
    }

    @Override
    protected void populateVelocityContext(VelocityContext velocityContext) throws UnableToCompleteException {
        velocityContext.put("metadata", registeredMetadata);
    }
}
