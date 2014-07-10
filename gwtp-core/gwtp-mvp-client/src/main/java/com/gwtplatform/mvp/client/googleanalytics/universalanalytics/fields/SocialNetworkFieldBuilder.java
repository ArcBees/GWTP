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

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.gwtplatform.mvp.client.googleanalytics.universalanalytics.OptionsCallback;

public class SocialNetworkFieldBuilder extends FieldBuilder {
    SocialNetworkFieldBuilder(final JSONObject jsonObject,
            final OptionsCallback optionsCallback,
            final String socialNetwork,
            final String socialAction,
            final String socialTarget) {
        super(jsonObject, optionsCallback);
        put("socialNetwork", new JSONString(socialNetwork));
        put("socialAction", new JSONString(socialAction));
        put("socialTarget", new JSONString(socialTarget));
    }
}
