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
package com.gwtplatform.mvp.client.googleanalytics.universalanalytics.fields;

import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.gwtplatform.mvp.client.googleanalytics.universalanalytics.OptionsCallback;

public class GeneralFieldBuilder extends FieldBuilder {
    GeneralFieldBuilder(final JSONObject jsonObject,
            final OptionsCallback optionsCallback) {
        super(jsonObject, optionsCallback);
    }

    /**
     * Optional.
     * When present, the IP address of the sender will be anonymized.
     */
    public GeneralFieldBuilder anonymizeIp(final boolean anonymizeIp) {
        put("anonymizeIp", JSONBoolean.getInstance(anonymizeIp));
        return this;
    }

    /**
     * Optional.
     * By default, tracking beacons sent from https pages will be sent using https while beacons sent
     * from http pages will be sent using http. Setting forceSSL to true will force http pages to also
     * send all beacons using https.
     * @param forceSSL - default: false
     */
    public GeneralFieldBuilder forceSSL(final boolean forceSSL) {
        put("forceSSL", JSONBoolean.getInstance(forceSSL));
        return this;
    }
}
