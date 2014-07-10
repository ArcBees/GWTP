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

public class ContentExperimentsFieldBuilder extends FieldBuilder {
    ContentExperimentsFieldBuilder(final JSONObject jsonObject) {
        super(jsonObject);
    }

    /**
     * Optional.
     * This parameter specifies that this user has been exposed to an experiment with the given ID.
     * It should be sent in conjunction with the Experiment Variant parameter.
     * @param experimentId (Max Length 40 bytes)<br>
     * Default: none<br>
     * Example Value: Qp0gahJ3RAO3DJ18b0XoUQ
     */
    public ContentExperimentsFieldBuilder experimentID(final String experimentID) {
        put("expId", new JSONString(experimentID));
        return this;
    }

    /**
     * Optional.
     * This parameter specifies that this user has been exposed to a particular variation of an experiment.
     * It should be sent in conjunction with the Experiment ID parameter.
     * @param experimentVariant<br>
     * Default: none<br>
     * Example Value: 1
     */
    public ContentExperimentsFieldBuilder experimentVariant(final String experimentVariant) {
        put("expId", new JSONString(experimentVariant));
        return this;
    }
}
