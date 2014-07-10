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

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class EventFieldBuilder extends FieldBuilder {
    EventFieldBuilder(final JSONObject jsonObject,
            final String category,
            final String action) {
        super(jsonObject);
        put("eventCategory", new JSONString(category));
        put("eventAction", new JSONString(action));
    }

    /**
     * Optional.
     * Specifies the event label.
     * @param eventLabel
     * Default: none;<br>
     */
    public EventFieldBuilder eventLabel(final String eventLabel) {
        put("eventLabel", new JSONString(eventLabel));
        return this;
    }

    /**
     * Optional.
     * Specifies the event value. Values must be non-negative
     * @param eventValue
     * Default: none;<br>
     */
    public EventFieldBuilder eventValue(final int eventValue) {
        put("eventValue", new JSONNumber(eventValue));
        return this;
    }
}
