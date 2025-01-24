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

package com.gwtplatform.common.client;

import jakarta.inject.Provider;

/**
 * This class can be used to contain a group of providers that should all sit
 * behind the same split point. That is, if the code for one provided object is
 * loaded, so is the code for the others. This is useful when the provided
 * objects share the bulk of their code, which would end up in the left-overs if
 * the objects were behind their individual split points.
 * GWTP automatically handles ProviderBundles when used with ApplicationController
 * and Ginjector generation.
 * <p>
 * Here is an example use of a manual {@link ProviderBundle}:
 * <pre>
 * public class MyPresenterBundle extends ProviderBundle {
 *   public final static int ID_Object1 = 0;
 *   public final static int ID_Object2 = 1;
 *   public final static int BUNDLE_SIZE = 2;
 *
 *   &#064;Inject
 *   MyPresenterBundle(
 *       final Provider&lt;Object1&gt; object1Provider,
 *       final Provider&lt;Object2&gt; object2Provider) {
 *     super( BUNDLE_SIZE );
 *     providers[ID_Object1] = object1Provider;
 *     providers[ID_Object2] = object2Provider;
 *   }
 * }</pre>
 */
public abstract class ProviderBundle {
    protected final Provider<?> providers[];

    /**
     * Constructs a {@link ProviderBundle} containing a given number of providers.
     * After calling this constructor you should set the provider manually like
     * so:
     * <pre>
     *     providers[0] = object1Provider;
     *     providers[1] = object2Provider;
     *     ...
     *     providers[bundleSize-1] = objectNProvider;</pre>
     *
     * @param bundleSize The number of providers in the bundle.
     */
    protected ProviderBundle(int bundleSize) {
        providers = new Provider<?>[bundleSize];
    }

    /**
     * Accesses a provider given its id.
     *
     * @param providerId The id of the provider to access.
     * @return The provider.
     */
    public Provider<?> get(int providerId) {
        return providers[providerId];
    }
}
