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

package com.gwtplatform.dispatch.rest.rebind.action;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.Multibinder;

import static com.gwtplatform.dispatch.rest.rebind.resource.ResourceModule.addMethodGenerator;

public class ActionModule extends AbstractModule {
    public static LinkedBindingBuilder<ActionGenerator> addActionGenerator(Binder binder) {
        return Multibinder.newSetBinder(binder, ActionGenerator.class).addBinding();
    }

    @Override
    protected void configure() {
        addActionGenerator(binder()).to(RestActionGenerator.class);

        addMethodGenerator(binder()).to(ActionMethodGenerator.class);
    }
}
