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
import com.gwtplatform.mvp.client.googleanalytics.universalanalytics.OptionsCallback;

public class CustomDimensionsFieldBuilder extends FieldBuilder {

    CustomDimensionsFieldBuilder(final JSONObject jsonObject, final OptionsCallback optionsCallback) {
        super(jsonObject, optionsCallback);
    }

    /**
     * Optional.
     * Each custom dimension has an associated index. There is a maximum of 20 custom
     * dimensions (200 for Premium accounts). The name suffix must be a positive integer between 1 and 200, inclusive.
     */
    public CustomDimensionsFieldBuilder customDimension(final int dimension, final String value) {
        put("dimension" + dimension, new JSONString(value));
        return this;
    }

    /**
     * Optional.
     * Each custom metric has an associated index. There is a maximum of 20 custom metrics
     *  (200 for Premium accounts). The name suffix must be a positive integer between 1 and 200, inclusive.
     */
    public CustomDimensionsFieldBuilder customMetric(final int index, final int value) {
        put("metric" + index, new JSONNumber(value));
        return this;
    }

}
