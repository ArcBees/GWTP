/**
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

package com.gwtplatform.mvp.client.proxy;

/**
 * Specialized {@link PlaceWithGatekeeper} which uses a {@link GatekeeperWithParams} and an array of parameters to
 * protect the {@link Place}.
 */
public class PlaceWithGatekeeperWithParams extends PlaceImpl {
    private final GatekeeperWithParams gatekeeper;
    private final String[] params;

    public PlaceWithGatekeeperWithParams(
            String nameToken,
            GatekeeperWithParams gatekeeper,
            String[] params) {
        this(new String[] { nameToken }, gatekeeper, params);
    }

    public PlaceWithGatekeeperWithParams(
            String[] nameTokens,
            GatekeeperWithParams gatekeeper,
            String[] params) {
        super(nameTokens);

        this.gatekeeper = gatekeeper;
        this.params = params;
    }

    @Override
    public boolean canReveal() {
        return gatekeeper.withParams(params).canReveal();
    }
}
