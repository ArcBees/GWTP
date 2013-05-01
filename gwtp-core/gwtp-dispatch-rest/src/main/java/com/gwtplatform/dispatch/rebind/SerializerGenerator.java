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
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.common.base.Strings;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.inject.assistedinject.Assisted;
import com.gwtplatform.dispatch.client.rest.NoResultSerializer;
import com.gwtplatform.dispatch.shared.NoResult;

public class SerializerGenerator extends AbstractVelocityGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rebind/Serializer.vm";
    private static final String PACKAGE = "com.gwtplatform.dispatch.shared.serializer";

    private JClassType type;

    @Inject
    public SerializerGenerator(
            TypeOracle typeOracle,
            Logger logger,
            Provider<VelocityContext> velocityContextProvider,
            VelocityEngine velocityEngine,
            GeneratorUtil generatorUtil,
            Set<JClassType> registeredClasses,
            @Assisted JClassType type) {
        super(typeOracle, logger, velocityContextProvider, velocityEngine, generatorUtil);

        this.type = type;

        registeredClasses.add(type);
    }

    public String generate() throws Exception {
        if (isNoResult()) {
            getLogger().debug("No Result Serializer required.");

            return NoResultSerializer.class.getName();
        }

        String implName = generateSerializerId();
        PrintWriter printWriter = getGeneratorUtil().tryCreatePrintWriter(getPackage(), implName);

        if (printWriter != null) {
            mergeTemplate(printWriter, TEMPLATE, implName);
        } else {
            getLogger().debug("Serializer already generated. Returning.");
        }

        return getPackage() + "." + implName;
    }

    @Override
    protected String getPackage() {
        if (isPackageRestricted()) {
            return PACKAGE;
        } else {
            return type.getPackage().getName();
        }
    }

    @Override
    protected void populateVelocityContext(VelocityContext velocityContext) throws UnableToCompleteException {
        velocityContext.put("resultClass", type);
    }

    private boolean isPackageRestricted() {
        Package aPackage = Package.getPackage(type.getPackage().getName());
        return aPackage != null && !Strings.isNullOrEmpty(aPackage.getSpecificationTitle());
    }

    private String generateSerializerId() throws UnableToCompleteException {
        String qualifiedName;

        if (isNoResult()) {
            qualifiedName = NoResultSerializer.class.getName();
        } else {
            qualifiedName = type.getParameterizedQualifiedSourceName();
        }

        qualifiedName = qualifiedName.replace(" ", "").replaceAll("[.,<>]", "_");

        return qualifiedName + "Serializer";
    }

    private boolean isNoResult() throws UnableToCompleteException {
        String name = type.getName();

        return name.equals(NoResult.class.getName());
    }
}
