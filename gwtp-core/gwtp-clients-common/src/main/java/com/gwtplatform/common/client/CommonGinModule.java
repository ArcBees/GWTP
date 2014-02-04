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

package com.gwtplatform.common.client;

import javax.inject.Singleton;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.binder.GinBinder;
import com.gwtplatform.common.shared.UrlUtils;

/**
 * Common client bindings. Because this module can be used in standalone projects, you must install it with
 * {@link #ensureInstalled(GinBinder)}.
 * <p/>
 * ie: <code>CommonGinModule.ensureInstalled(binder());</code>
 */
public class CommonGinModule extends AbstractGinModule {
    private static boolean isInstalled = false;

    protected CommonGinModule() {
    }

    /**
     * Ensure this modules is installed without throwing an exception if called more than once.
     * For example, <i>gwtp-mvp-client</i> and <i>gwtp-dispatch-rest</i> both require this module to be installed. It's
     * possible to use them both together or separately.
     */
    public static void ensureInstalled(GinBinder ginBinder) {
        if (!isInstalled) {
            ginBinder.install(new CommonGinModule());

            isInstalled = true;
        }
    }

    @Override
    protected void configure() {
        bind(UrlUtils.class).to(ClientUrlUtils.class).in(Singleton.class);
    }
}
