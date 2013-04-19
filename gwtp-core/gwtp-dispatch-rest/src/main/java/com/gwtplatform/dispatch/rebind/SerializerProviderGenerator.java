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

package com.gwtplatform.dispatch.rebind;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.gwtplatform.dispatch.client.rest.SerializedType;
import com.gwtplatform.dispatch.client.rest.SerializerProvider;
import com.gwtplatform.dispatch.rebind.event.ChildSerializer;
import com.gwtplatform.dispatch.rebind.event.RegisterSerializerEvent;
import com.gwtplatform.dispatch.rebind.type.RegisterSerializerBinding;

/**
 * TODO: Documentation.
 */
public class SerializerProviderGenerator extends AbstractVelocityGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rebind/SerializerProvider.vm";

    private final List<RegisterSerializerBinding> registeredSerializers = new ArrayList<RegisterSerializerBinding>();
    private final Set<String> childSerializers = new HashSet<String>();

    @Inject
    public SerializerProviderGenerator(
            TypeOracle typeOracle,
            Logger logger,
            Provider<VelocityContext> velocityContextProvider,
            VelocityEngine velocityEngine,
            GeneratorUtil generatorUtil,
            EventBus eventBus) {
        super(typeOracle, logger, velocityContextProvider, velocityEngine, generatorUtil);

        eventBus.register(this);
    }

    public void generate() throws UnableToCompleteException {
        processChildSerializers();

        JClassType type = getGeneratorUtil().getType(SerializerProvider.class.getName());
        String implName = type.getName() + SUFFIX;
        PrintWriter printWriter = getGeneratorUtil().tryCreatePrintWriter(getPackage(), implName);
        if (printWriter != null) {
            try {
                mergeTemplate(printWriter, TEMPLATE, implName);
            } catch (Exception e) {
                getLogger().die(e.getMessage());
            }
        } else {
            getLogger().debug("Serializer already generated. Returning.");
        }
    }

    private void processChildSerializers() {
        for (String serializerClass : childSerializers) {
            if (!isAlreadyRegistered(serializerClass)) {
                RegisterSerializerBinding binding = new RegisterSerializerBinding(null, null, serializerClass);
                registeredSerializers.add(binding);
                getLogger().debug("Serializer " + serializerClass + " registered.");
            }
        }
    }

    @Subscribe
    public void handleRegisterSerializer(RegisterSerializerEvent event) {
        String serializerClass = event.getSerializerClass();
        SerializedType serializedType = event.getSerializedType();
        String actionClass = event.getActionClass();

        getLogger().debug("Serializer " + serializerClass + " registered.");

        RegisterSerializerBinding binding = new RegisterSerializerBinding(actionClass, serializedType, serializerClass);

        registeredSerializers.add(binding);
    }

    @Subscribe
    public void handleChildSerializerEvent(ChildSerializer event) {
        childSerializers.add(event.getSerializerClassName());
    }

    @Override
    protected String getPackage() {
        return SerializerProvider.class.getPackage().getName();
    }

    @Override
    protected void populateVelocityContext(VelocityContext velocityContext)
            throws UnableToCompleteException {
        velocityContext.put("serializers", registeredSerializers);
    }

    private boolean isAlreadyRegistered(String serializerClassName) {
        for (RegisterSerializerBinding binding : registeredSerializers) {
            if (binding.getSerializerClass().equals(serializerClassName)) {
                return true;
            }
        }

        return false;
    }
}
