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

package com.gwtplatform.dispatch.rest.rebind.serialization;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.Multibinder;

import jakarta.inject.Singleton;

import static com.gwtplatform.dispatch.rest.rebind.extension.ExtensionModule.addExtensionGenerator;

public class SerializationModule extends AbstractModule {
    public static LinkedBindingBuilder<SerializationGenerator> addSerialisationGenerator(Binder binder) {
        return Multibinder.newSetBinder(binder, SerializationGenerator.class).addBinding();
    }

    @Override
    protected void configure() {
        addSerialisationGenerator(binder()).to(JacksonMapperGenerator.class);
        addExtensionGenerator(binder()).to(JacksonMapperProviderGenerator.class);

        bind(JacksonMapperDefinitions.class).to(JacksonMapperProviderGenerator.class);
        bind(JacksonMapperProviderGenerator.class).in(Singleton.class);
    }
}
