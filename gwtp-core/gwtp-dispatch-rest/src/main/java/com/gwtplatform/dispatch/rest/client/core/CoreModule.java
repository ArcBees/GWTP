/**
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

package com.gwtplatform.dispatch.rest.client.core;

import javax.inject.Singleton;

import com.google.gwt.inject.client.AbstractGinModule;

public class CoreModule extends AbstractGinModule {
    @Override
    protected void configure() {
        // TODO: Move all core classes to this package
        // parameters.* should become core.parameters.*
        // Classes used only by code-gen should go in core.codegen.*
        // Root package should be for the public API

        bind(CookieManager.class).to(DefaultCookieManager.class).in(Singleton.class);
    }
}
