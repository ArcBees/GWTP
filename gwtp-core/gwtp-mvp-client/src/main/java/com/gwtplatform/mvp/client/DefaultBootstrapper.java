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

package com.gwtplatform.mvp.client;

import com.gwtplatform.mvp.client.proxy.PlaceManager;

import jakarta.inject.Inject;

/**
 * The default Bootstrapper that is used if none is specified in {@link
 * com.gwtplatform.mvp.client.annotations.UseBootstrapper @UseBootstrapper}.
 */
public class DefaultBootstrapper implements Bootstrapper {
    private final PlaceManager placeManager;

    @Inject
    public DefaultBootstrapper(PlaceManager placeManager) {
        this.placeManager = placeManager;
    }

    @Override
    public void onBootstrap() {
        placeManager.revealCurrentPlace();
    }
}
