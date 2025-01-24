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

package com.gwtplatform.common.client;

import com.google.gwt.inject.client.AbstractGinModule;
import com.gwtplatform.common.shared.UrlUtils;

import jakarta.inject.Singleton;

/**
 * Common client bindings.
 * You can safely install this module multiple times as the bindings will be performed only on the first install.
 */
public class CommonGinModule extends AbstractGinModule {
    private static boolean isInstalled;

    @Override
    protected void configure() {
        if (!isInstalled) {
            bind(UrlUtils.class).to(ClientUrlUtils.class).in(Singleton.class);

            isInstalled = true;
        }
    }
}
