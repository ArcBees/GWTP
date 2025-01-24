/*
 * Copyright 2015 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.client.serialization;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.binder.GinBinder;
import com.google.gwt.inject.client.binder.GinLinkedBindingBuilder;

import jakarta.inject.Singleton;

import static com.google.gwt.inject.client.multibindings.GinMultibinder.newSetBinder;

public class SerializationModule extends AbstractGinModule {
    public static GinLinkedBindingBuilder<Serialization> registerSerializationBinding(GinBinder binder) {
        return newSetBinder(binder, Serialization.class).addBinding();
    }

    @Override
    protected void configure() {
        // TODO: Move jackson implementation to an extension
        registerSerializationBinding(binder()).to(JsonSerialization.class).in(Singleton.class);

        newSetBinder(binder(), JacksonMapperProvider.class);
        bind(JacksonMapperProvider.class).to(DispatchingJacksonMapperProvider.class).in(Singleton.class);
    }
}
