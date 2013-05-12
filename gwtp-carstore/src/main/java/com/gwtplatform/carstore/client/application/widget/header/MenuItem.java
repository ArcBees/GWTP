/**
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.carstore.client.application.widget.header;

import com.gwtplatform.carstore.client.place.NameTokens;

public enum MenuItem {
    MANUFACTURER("Manufacturers", NameTokens.manufacturer),
    CAR("Cars", NameTokens.cars),
    RATING("Ratings", NameTokens.rating),
    REPORT("Reports", NameTokens.report);

    private String label;
    private String placeToken;

    private MenuItem(String label, String placeToken) {
        this.label = label;
        this.placeToken = placeToken;
    }

    public String getPlaceToken() {
        return placeToken;
    }

    public String toString() {
        return label;
    }
}

