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

package com.gwtplatform.dispatch.rest.rebind2;

import java.net.URL;
import java.util.Properties;

import javax.inject.Singleton;

import org.apache.velocity.app.VelocityEngine;

import com.google.common.eventbus.EventBus;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.gwtplatform.dispatch.rest.rebind2.entrypoint.EntryPointModule;
import com.gwtplatform.dispatch.rest.rebind2.gin.GinModule;
import com.gwtplatform.dispatch.rest.rebind2.resource.ResourceModule;
import com.gwtplatform.dispatch.rest.rebind2.utils.Logger;

public class DispatchRestRebindModule extends AbstractModule {
    private static final String VELOCITY_PROPERTIES = "com/gwtplatform/dispatch/rest/rebind/velocity.properties";

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
        install(new EntryPointModule());
        install(new GinModule());
        install(new ResourceModule());

        bind(GeneratorContext.class).toInstance(context);

        bind(EventBus.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    Logger getLogger() {
        return new Logger(treeLogger);
    }

    @Provides
    @VelocityProperties
    Properties getVelocityProperties(Logger logger) throws UnableToCompleteException {
        Properties properties = new Properties();

        try {
            URL url = Resources.getResource(VELOCITY_PROPERTIES);
            ByteSource byteSource = Resources.asByteSource(url);

            properties.load(byteSource.openStream());
        } catch (Exception e) {
            logger.die("Unable to load velocity properties from '%s'.", e, VELOCITY_PROPERTIES);
        }

        return properties;
    }

    @Provides
    @Singleton
    VelocityEngine getVelocityEngine(@VelocityProperties Properties properties) {
        return new VelocityEngine(properties);
    }
}
