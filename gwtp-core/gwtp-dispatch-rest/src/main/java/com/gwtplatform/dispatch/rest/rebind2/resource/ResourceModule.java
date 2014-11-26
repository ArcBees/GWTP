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

package com.gwtplatform.dispatch.rest.rebind2.resource;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.multibindings.Multibinder;

public class ResourceModule extends AbstractModule {
    public static void addResourceGenerator(Binder binder,
            Class<? extends ResourceGenerator> generatorClass) {
        Multibinder<ResourceGenerator> multibinder = Multibinder.newSetBinder(binder, ResourceGenerator.class);
        multibinder.addBinding().to(generatorClass);
    }

    public static void addResourceMethodGenerator(Binder binder,
            Class<? extends ResourceMethodGenerator> generatorClass) {
        Multibinder<ResourceMethodGenerator> multibinder =
                Multibinder.newSetBinder(binder, ResourceMethodGenerator.class);
        multibinder.addBinding().to(generatorClass);
    }

    @Override
    protected void configure() {
        addResourceGenerator(binder(), TopLevelResourceGenerator.class);
    }
}
