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

import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Singleton;

import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.gwtplatform.dispatch.rebind.type.SerializerDefinitions;

/**
 * TODO: Documentation.
 */
public class RebindModule extends AbstractModule {
    private static final String VELOCITY_PROPERTIES = "com/gwtplatform/dispatch/rebind/velocity.properties";

    private final Logger logger;
    private final GeneratorContext generatorContext;

    public RebindModule(Logger logger, GeneratorContext generatorContext) {
        super();

        this.logger = logger;
        this.generatorContext = generatorContext;
    }

    @Provides
    public Logger getLogger() {
        return logger;
    }

    @Provides
    public TypeOracle getTypeOracle() {
        return generatorContext.getTypeOracle();
    }

    @Provides
    public GeneratorContext getGeneratorContext() {
        return generatorContext;
    }

    @Provides
    @Singleton
    public VelocityEngine getVelocityEngine(@VelocityProperties String velocityProperties, Logger logger)
            throws UnableToCompleteException {
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(velocityProperties);
            Properties properties = new Properties();
            try {
                properties.load(inputStream);
                return new VelocityEngine(properties);
            } catch (Exception e) {
                logger.die("Cannot load velocity properties from " + velocityProperties);
            }
        } catch (Exception e) {
            logger.getTreeLogger().log(TreeLogger.Type.ERROR, e.getMessage(), e);
        }

        return null;
    }

    @Override
    protected void configure() {
        bindConstant().annotatedWith(VelocityProperties.class).to(VELOCITY_PROPERTIES);

        bind(SerializerDefinitions.class).in(Singleton.class);
        bind(GeneratorUtil.class).in(Singleton.class);
        bind(EventBus.class).in(Singleton.class);

        install(new FactoryModuleBuilder().build(GeneratorFactory.class));
    }

    @Provides
    @Singleton
    Set<JClassType> getRegisteredClassTypes() {
        ConcurrentMap<JClassType, Boolean> backingConcurrentMap = Maps.newConcurrentMap();
        return Collections.newSetFromMap(backingConcurrentMap);
    }
}
