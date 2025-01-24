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

package com.gwtplatform.mvp.client.proxy;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.gwtplatform.mvp.shared.proxy.TokenFormatter;

import jakarta.inject.Inject;

/**
 * This place manager overrides all the methods that use
 * GWT-dependent classes and can be used for testing without
 * having to rely on a {@code GWTTestCase}.
 */
class PlaceManagerTestUtil extends PlaceManagerImpl {

    private final PlaceRequest defaultPlaceRequest = new PlaceRequest.Builder().nameToken("defaultPlace").build();
    private final PlaceManagerWindowMethodsTestUtil gwtWindowMethods;

    @Inject
    PlaceManagerTestUtil(EventBus eventBus, TokenFormatter tokenFormatter,
            PlaceManagerWindowMethodsTestUtil gwtWindowMethods) {
        super(eventBus, tokenFormatter);
        this.gwtWindowMethods = gwtWindowMethods;
    }

    @Override
    public void revealDefaultPlace() {
        revealPlace(defaultPlaceRequest);
    }

    @Override
    void registerTowardsHistory() {
        if (gwtWindowMethods != null) {
            gwtWindowMethods.registerTowardsHistory();
        }
    }

    @Override
    String getBrowserHistoryToken() {
        return gwtWindowMethods.getBrowserHistoryToken();
    }

    @Override
    public void revealCurrentPlace() {
        gwtWindowMethods.revealCurrentPlace();
    }

    @Override
    void setBrowserHistoryToken(String historyToken, boolean issueEvent) {
        gwtWindowMethods.setBrowserHistoryToken(historyToken, issueEvent);
    }
}
