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

public class UserTimingFieldBuilder extends FieldBuilder {
    UserTimingFieldBuilder(final JSONObject jsonObject, final String timingCategory, final String timingVar,
            final int timingValue) {
        super(jsonObject);
        put("timingCategory", new JSONString(timingCategory));
        put("timingVar", new JSONString(timingVar));
        put("timingValue", new JSONNumber(timingValue));
    }

    /**
     * Optional.
     * Specifies the user timing label
     * @param timingLabel<br>
     * Default: none<br>
     * Example Value: label
     */
    public UserTimingFieldBuilder timingLabel(final String timingLabel) {
        put("timingLabel", new JSONString(timingLabel));
        return this;
    }
}
