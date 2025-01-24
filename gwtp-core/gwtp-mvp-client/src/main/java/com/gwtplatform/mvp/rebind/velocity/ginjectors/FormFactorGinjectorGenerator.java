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

package com.gwtplatform.mvp.rebind.velocity.ginjectors;

import java.io.PrintWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.gwt.core.ext.ConfigurationProperty;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.inject.assistedinject.Assisted;
import com.gwtplatform.mvp.rebind.velocity.AbstractVelocityGenerator;
import com.gwtplatform.mvp.rebind.velocity.GeneratorUtil;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

public class FormFactorGinjectorGenerator extends AbstractVelocityGenerator {
    private static final String GIN_MODULE = "ginmodule";
    private static final String GIN_MODULE_CLASS = "%s.class";

    private final String velocityTemplate;
    private final String propertyName;
    private final String implName;

    @Inject
    FormFactorGinjectorGenerator(
            Provider<VelocityContext> velocityContextProvider,
            VelocityEngine velocityEngine,
            GeneratorUtil generatorUtil,
            @Assisted("velocityTemplate") String velocityTemplate,
            @Assisted("propertyName") String propertyName,
            @Assisted("implName") String implName) {
        super(velocityContextProvider, velocityEngine, generatorUtil);

        this.velocityTemplate = velocityTemplate;
        this.propertyName = propertyName;
        this.implName = implName;
    }

    public String generate() throws Exception {
        PrintWriter printWriter = getGeneratorUtil().tryCreatePrintWriter(PACKAGE, implName);

        if (printWriter != null) {
            try {
                mergeTemplate(printWriter, velocityTemplate);
            } finally {
                printWriter.close();
            }
        }

        return PACKAGE + "." + implName;
    }

    @Override
    protected void populateVelocityContext(VelocityContext velocityContext) throws UnableToCompleteException {
        velocityContext.put(IMPL_NAME, implName);

        ConfigurationProperty configurationProperty =
                getGeneratorUtil().findConfigurationProperty(propertyName);

        String moduleClass = configurationProperty.getValues().get(0);
        if (!moduleClass.isEmpty()) {
            moduleClass = String.format(GIN_MODULE_CLASS, moduleClass);
        }

        velocityContext.put(GIN_MODULE, moduleClass);
    }
}
