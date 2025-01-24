/*
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.rebind.gin;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.gwtplatform.dispatch.rest.rebind.AbstractVelocityGenerator;
import com.gwtplatform.dispatch.rest.rebind.events.RegisterGinBindingEvent;
import com.gwtplatform.dispatch.rest.rebind.events.RegisterGinBindingEvent.RegisterGinBindingListener;
import com.gwtplatform.dispatch.rest.rebind.utils.ClassDefinition;
import com.gwtplatform.dispatch.rest.rebind.utils.EventBus;
import com.gwtplatform.dispatch.rest.rebind.utils.Logger;

import jakarta.inject.Inject;

public class DefaultGinModuleGenerator extends AbstractVelocityGenerator implements GinModuleGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind/gin/GinModule.vm";
    private static final String GIN_MODULE_PROPERTY = "gwtp.dispatch.rest.ginModule";

    private final List<RegisterGinBindingEvent> bindings;

    private String packageName;
    private String implName;

    @Inject
    DefaultGinModuleGenerator(
            Logger logger,
            GeneratorContext context,
            EventBus eventBus,
            VelocityEngine velocityEngine) {
        super(logger, context, velocityEngine);

        bindings = new ArrayList<RegisterGinBindingEvent>();

        eventBus.register(RegisterGinBindingEvent.class, (RegisterGinBindingListener) this);
    }

    @Override
    public boolean canGenerate() {
        loadGinModuleProperty();

        return !implName.isEmpty();
    }

    @Override
    public ClassDefinition generate() throws UnableToCompleteException {
        PrintWriter printWriter = tryCreate();
        if (printWriter != null) {
            mergeTemplate(printWriter);
            commit(printWriter);
        }

        return getClassDefinition();
    }

    @Override
    public void onGinBindingRegistered(RegisterGinBindingEvent event) {
        bindings.add(event);
    }

    @Override
    protected void populateTemplateVariables(Map<String, Object> variables) {
        variables.put("bindings", bindings);
    }

    @Override
    protected String getTemplate() {
        return TEMPLATE;
    }

    @Override
    protected String getPackageName() {
        return packageName;
    }

    @Override
    protected String getImplName() {
        return implName;
    }

    private void loadGinModuleProperty() {
        try {
            List<String> ginModuleValues =
                    getContext().getPropertyOracle().getConfigurationProperty(GIN_MODULE_PROPERTY).getValues();
            if (ginModuleValues.isEmpty()) {
                logGinModulePropertyNotFound();
            } else {
                String ginModuleName = ginModuleValues.get(0);

                if (ginModuleName.isEmpty()) {
                    logGinModulePropertyNotFound();
                } else {
                    int lastDotIndex = ginModuleName.lastIndexOf('.');
                    packageName = ginModuleName.substring(0, lastDotIndex);
                    implName = ginModuleName.substring(lastDotIndex + 1);
                }
            }
        } catch (BadPropertyValueException e) {
            logGinModulePropertyNotFound();
        }
    }

    private void logGinModulePropertyNotFound() {
        getLogger().error("Unable to read the Gin Module name."
                + "Make sure the configuration property '%s' is properly set in your GWT configuration.");
    }
}
