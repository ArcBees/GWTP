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

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.common.eventbus.EventBus;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.gwtplatform.dispatch.rest.client.serialization.JacksonMapperProvider;
import com.gwtplatform.dispatch.rest.rebind.util.GeneratorUtil;

public class JacksonMapperGenerator extends AbstractVelocityGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind/JacksonMapper.vm";

    private JType type;

    @Inject
    JacksonMapperGenerator(TypeOracle typeOracle,
                           Logger logger,
                           Provider<VelocityContext> velocityContextProvider,
                           VelocityEngine velocityEngine,
                           GeneratorUtil generatorUtil,
                           EventBus eventBus) {
        super(typeOracle, logger, velocityContextProvider, velocityEngine, generatorUtil);

        eventBus.register(this);
    }

    public String generate(JType type) throws UnableToCompleteException {
        this.type = type;

        String implName = type.getParameterizedQualifiedSourceName();
        implName = implName.replaceAll("[ ,<>\\.\\?]", "_");
        implName += "Mapper";

        PrintWriter printWriter = getGeneratorUtil().tryCreatePrintWriter(getPackage(), implName);

        if (printWriter != null) {
            try {
                mergeTemplate(printWriter, TEMPLATE, implName);
            } catch (Exception e) {
                getLogger().die(e.getMessage());
            }
        } else {
            getLogger().debug("Jackson Mapper already generated. Returning.");
        }

        return implName;
    }

    @Override
    protected String getPackage() {
        return JacksonMapperProvider.class.getPackage().getName() + ".mappers";
    }

    @Override
    protected void populateVelocityContext(VelocityContext velocityContext) throws UnableToCompleteException {
        velocityContext.put("type", type.getParameterizedQualifiedSourceName());
    }
}
