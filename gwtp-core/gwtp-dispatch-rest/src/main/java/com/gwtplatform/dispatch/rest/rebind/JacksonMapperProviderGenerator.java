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
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.gwtplatform.dispatch.rest.client.serialization.JacksonMapperProvider;
import com.gwtplatform.dispatch.rest.rebind.event.RegisterSerializableTypeEvent;
import com.gwtplatform.dispatch.rest.rebind.util.GeneratorUtil;

public class JacksonMapperProviderGenerator extends AbstractVelocityGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind/JacksonMapperProvider.vm";

    private final Map<JType, String> registeredTypes = Maps.newHashMap();
    private final JacksonMapperGenerator jacksonMapperGenerator;

    @Inject
    JacksonMapperProviderGenerator(TypeOracle typeOracle,
                                   Logger logger,
                                   Provider<VelocityContext> velocityContextProvider,
                                   VelocityEngine velocityEngine,
                                   GeneratorUtil generatorUtil,
                                   EventBus eventBus,
                                   JacksonMapperGenerator jacksonMapperGenerator) {
        super(typeOracle, logger, velocityContextProvider, velocityEngine, generatorUtil);

        this.jacksonMapperGenerator = jacksonMapperGenerator;

        eventBus.register(this);
    }

    public void generate() throws UnableToCompleteException {
        for (JType type : registeredTypes.keySet()) {
            String mapperName = jacksonMapperGenerator.generate(type);
            registeredTypes.put(type, mapperName);
        }

        String implName = JacksonMapperProvider.class.getSimpleName() + SUFFIX;
        PrintWriter printWriter = getGeneratorUtil().tryCreatePrintWriter(getPackage(), implName);
        if (printWriter != null) {
            mergeTemplate(printWriter, TEMPLATE, implName);
        } else {
            getLogger().debug("Jackson Mapper Provider already generated. Returning.");
        }
    }

    @Subscribe
    public void handleRegisterSerializableType(RegisterSerializableTypeEvent event) {
        registeredTypes.put(event.getType(), "");
    }

    @Override
    protected String getPackage() {
        return JacksonMapperProvider.class.getPackage().getName();
    }

    @Override
    protected void populateVelocityContext(VelocityContext velocityContext) throws UnableToCompleteException {
        velocityContext.put("types" , registeredTypes);
    }
}
