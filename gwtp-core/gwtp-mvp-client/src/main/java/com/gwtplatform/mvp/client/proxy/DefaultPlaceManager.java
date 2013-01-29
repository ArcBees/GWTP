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

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.annotations.DefaultPlace;
import com.gwtplatform.mvp.client.annotations.ErrorPlace;
import com.gwtplatform.mvp.client.annotations.UnauthorizedPlace;

import javax.inject.Inject;

/**
 * This is a subtype of {@link com.gwtplatform.mvp.client.proxy.PlaceManagerImpl PlaceManagerImpl} that uses
 * custom name tokens to reveal default, error and unauthorized places.
 * <p/>
 * <b!>Important!</b!>If you use this class, don't forget to bind
 * {@link com.gwtplatform.mvp.client.annotations.DefaultPlace DefaultPlace},
 * {@link com.gwtplatform.mvp.client.annotations.ErrorPlace ErrorPlace} and
 * {@link com.gwtplatform.mvp.client.annotations.UnauthorizedPlace UnauthorizedPlace} to Presenter name tokens in
 * your Gin module.
 */
public class DefaultPlaceManager extends PlaceManagerImpl {
    private final PlaceRequest defaultPlaceRequest;
    private final PlaceRequest errorPlaceRequest;
    private final PlaceRequest unauthorizedPlaceRequest;

    @Inject
    public DefaultPlaceManager(EventBus eventBus,
                               TokenFormatter tokenFormatter,
                               @DefaultPlace String defaultPlaceNameToken,
                               @ErrorPlace String errorPlaceNameToken,
                               @UnauthorizedPlace String unauthorizedPlaceNameToken) {
        super(eventBus, tokenFormatter);

        defaultPlaceRequest = new PlaceRequest(defaultPlaceNameToken);
        errorPlaceRequest = new PlaceRequest(errorPlaceNameToken);
        unauthorizedPlaceRequest = new PlaceRequest(unauthorizedPlaceNameToken);
    }

    @Override
    public void revealDefaultPlace() {
        revealPlace(defaultPlaceRequest, false);
    }

    @Override
    public void revealErrorPlace(String invalidHistoryToken) {
        revealPlace(errorPlaceRequest, false);
    }

    @Override
    public void revealUnauthorizedPlace(String unauthorizedHistoryToken) {
        revealPlace(unauthorizedPlaceRequest, false);
    }
}
