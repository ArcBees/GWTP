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

import java.io.InputStream;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.gwtplatform.common.rebind.Logger;
import com.gwtplatform.mvp.rebind.velocity.ginjectors.FormFactorGinjectorFactory;
import com.gwtplatform.mvp.rebind.velocity.proxy.VelocityPlacetokenGenerator;

import jakarta.inject.Singleton;

public class RebindModule extends AbstractModule {
    private static final String VELOCITY_PROPERTIES = "com/gwtplatform/mvp/rebind/velocity.properties";

    private final Logger logger;
    private final GeneratorContext generatorContext;

    public RebindModule(Logger logger, GeneratorContext generatorContext) {
        super();

        this.logger = logger;
        this.generatorContext = generatorContext;
    }

    @Override
    protected void configure() {
        bindConstant().annotatedWith(VelocityProperties.class).to(VELOCITY_PROPERTIES);

        bind(GeneratorUtil.class).in(Singleton.class);
        bind(GenerateFormFactorGinjectors.class).in(Singleton.class);

        install(new FactoryModuleBuilder().build(FormFactorGinjectorFactory.class));
        install(new FactoryModuleBuilder().build(VelocityPlacetokenGenerator.Factory.class));
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
            InputStream inputStream = null;
            try {
                inputStream = this.getClass().getClassLoader().getResourceAsStream(velocityProperties);
                Properties properties = new Properties();
                properties.load(inputStream);
                return new VelocityEngine(properties);
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        } catch (Exception e) {
            logger.die("Cannot load velocity properties from " + velocityProperties);
            return null;
        }
    }
}
