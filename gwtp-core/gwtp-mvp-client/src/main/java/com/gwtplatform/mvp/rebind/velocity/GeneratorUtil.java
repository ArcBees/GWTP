/*
 * Copyright 2011 ArcBees Inc.
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

package com.gwtplatform.mvp.rebind.velocity;

import java.io.PrintWriter;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.ConfigurationProperty;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.PropertyOracle;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.gwtplatform.common.rebind.Logger;

import jakarta.inject.Inject;

public class GeneratorUtil {
    private static final String CANNOT_FIND_PROPERTY = "Cannot find %s property in your module.gwt.xml file.";
    private static final String CANNOT_FIND_TYPE = "Cannot find %s";

    private final TypeOracle typeOracle;
    private final Logger logger;
    private final GeneratorContext generatorContext;
    private final PropertyOracle propertyOracle;

    @Inject
    public GeneratorUtil(TypeOracle typeOracle, Logger logger, GeneratorContext generatorContext) {
        this.typeOracle = typeOracle;
        this.logger = logger;
        this.generatorContext = generatorContext;
        propertyOracle = generatorContext.getPropertyOracle();
    }

    public JClassType getType(String typeName) throws UnableToCompleteException {
        try {
            return typeOracle.getType(typeName);
        } catch (NotFoundException e) {
            logger.die(String.format(CANNOT_FIND_TYPE, typeName));
        }

        return null;
    }

    public void closeDefinition(PrintWriter printWriter) {
        generatorContext.commit(logger.getTreeLogger(), printWriter);
    }

    public PrintWriter tryCreatePrintWriter(String packageName, String className)
            throws UnableToCompleteException {
        return generatorContext.tryCreate(logger.getTreeLogger(), packageName, className);
    }

    public ConfigurationProperty findConfigurationProperty(String prop) throws UnableToCompleteException {
        try {
            return propertyOracle.getConfigurationProperty(prop);
        } catch (BadPropertyValueException e) {
            logger.die(String.format(CANNOT_FIND_PROPERTY, prop));
        }

        return null;
    }
}
