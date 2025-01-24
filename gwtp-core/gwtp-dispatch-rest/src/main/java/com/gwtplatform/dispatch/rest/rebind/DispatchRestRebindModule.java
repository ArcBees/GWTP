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

package com.gwtplatform.dispatch.rest.rebind;

import org.apache.velocity.app.VelocityEngine;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.gwtplatform.dispatch.rest.rebind.action.ActionModule;
import com.gwtplatform.dispatch.rest.rebind.entrypoint.EntryPointModule;
import com.gwtplatform.dispatch.rest.rebind.extension.ExtensionModule;
import com.gwtplatform.dispatch.rest.rebind.gin.GinModule;
import com.gwtplatform.dispatch.rest.rebind.resource.ResourceModule;
import com.gwtplatform.dispatch.rest.rebind.serialization.SerializationModule;
import com.gwtplatform.dispatch.rest.rebind.subresource.SubResourceModule;
import com.gwtplatform.dispatch.rest.rebind.utils.EventBus;
import com.gwtplatform.dispatch.rest.rebind.utils.Logger;
import com.gwtplatform.dispatch.rest.rebind.utils.VelocityProperties;

import jakarta.inject.Singleton;

public class DispatchRestRebindModule extends AbstractModule {
    private final TreeLogger treeLogger;
    private final GeneratorContext context;

    public DispatchRestRebindModule(
            TreeLogger treeLogger,
            GeneratorContext context) {
        this.treeLogger = treeLogger;
        this.context = context;
    }

    @Override
    protected void configure() {
        install(new ActionModule());
        install(new EntryPointModule());
        install(new ExtensionModule());
        install(new GinModule());
        install(new ResourceModule());
        install(new SerializationModule());
        install(new SubResourceModule());

        bind(GeneratorContext.class).toInstance(context);

        bind(VelocityProperties.class).in(Singleton.class);
        bind(EventBus.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    Logger getLogger() {
        // TODO: Consider not injecting it or some automated way to take advantage of branching
        return new Logger(treeLogger);
    }

    @Provides
    @Singleton
    VelocityEngine getVelocityEngine(VelocityProperties properties) throws UnableToCompleteException {
        return new VelocityEngine(properties.asProperties());
    }
}
