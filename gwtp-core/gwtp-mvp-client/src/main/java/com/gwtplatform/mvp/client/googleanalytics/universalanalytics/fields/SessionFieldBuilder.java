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

public class SessionFieldBuilder extends FieldBuilder {

    SessionFieldBuilder(final JSONObject jsonObject, final OptionsCallback optionsCallback) {
        super(jsonObject, optionsCallback);
    }

    /**
     * Used to control the session duration. Forces the current session to end with this hit.
     */
    public SessionFieldBuilder end() {
        put("sessionControl", new JSONString("end"));
        return this;
    }

    /**
     * Used to control the session duration. Forces a new session to start with this hit.
     */
    public SessionFieldBuilder start() {
        put("sessionControl", new JSONString("start"));
        return this;
    }

}
