/**
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

package com.gwtplatform.dispatch.rest.rebind;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.inject.assistedinject.Assisted;
import com.gwtplatform.dispatch.rest.client.ResourceDelegate;
import com.gwtplatform.dispatch.rest.rebind.type.ActionBinding;
import com.gwtplatform.dispatch.rest.rebind.type.ChildServiceBinding;
import com.gwtplatform.dispatch.rest.rebind.type.ServiceBinding;
import com.gwtplatform.dispatch.rest.rebind.util.GeneratorUtil;

public class ResourceDelegateGenerator extends AbstractVelocityGenerator {
    private static final String TEMPLATE = "com/gwtplatform/dispatch/rest/rebind/ResourceDelegate.vm";
    private static final String SUFFIX = ResourceDelegate.class.getSimpleName();

    private final ServiceBinding serviceBinding;
    private final List<ActionBinding> actionBindings;
    private final List<ServiceBinding> serviceBindings;

    @Inject
    ResourceDelegateGenerator(
            TypeOracle typeOracle,
            Logger logger,
            Provider<VelocityContext> velocityContextProvider,
            VelocityEngine velocityEngine,
            GeneratorUtil generatorUtil,
            @Assisted ServiceBinding serviceBinding,
            @Assisted List<ActionBinding> actionBindings,
            @Assisted List<ServiceBinding> serviceBindings) {
        super(typeOracle, logger, velocityContextProvider, velocityEngine, generatorUtil);

        this.serviceBinding = serviceBinding;
        this.actionBindings = actionBindings;
        this.serviceBindings = serviceBindings;
    }

    public void generate() throws UnableToCompleteException {
        String implName = serviceBinding.getServiceInterface() + SUFFIX;
        mergeTemplate(TEMPLATE, implName);
    }

    @Override
    protected void populateVelocityContext(VelocityContext velocityContext) throws UnableToCompleteException {
        velocityContext.put("injectable", !(serviceBinding instanceof ChildServiceBinding));
        velocityContext.put("service", serviceBinding);
        velocityContext.put("resources", serviceBindings);
        velocityContext.put("actions", actionBindings);
    }

    @Override
    protected String getPackage() {
        return serviceBinding.getImplPackage();
    }
}
