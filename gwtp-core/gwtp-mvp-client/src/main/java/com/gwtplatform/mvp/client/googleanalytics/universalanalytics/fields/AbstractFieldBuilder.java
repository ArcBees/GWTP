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
import com.google.gwt.json.client.JSONValue;
import com.gwtplatform.mvp.client.googleanalytics.universalanalytics.HasFields;
import com.gwtplatform.mvp.client.googleanalytics.universalanalytics.HitCallback;
import com.gwtplatform.mvp.client.googleanalytics.universalanalytics.OptionsCallback;

public abstract class AbstractFieldBuilder implements HasFields {

    private final JSONObject jsonObject;
    private final OptionsCallback callback;

    AbstractFieldBuilder(final JSONObject jsonObject, final OptionsCallback callback) {
        this.jsonObject = jsonObject;
        this.callback = callback;
    }

    AbstractFieldBuilder(final OptionsCallback callback) {
        this(new JSONObject(), callback);
    }

    JSONObject build() {
        return jsonObject;
    }

    OptionsCallback getCallback() {
        return callback;
    }

    public void go() {
        callback.onCallback(build());
    }

    public void go(final HitCallback callback) {
        go(jsonObject, callback);
    };

    private native void go(JSONObject jsonObject, HitCallback callback) /*-{
        jsonObject.hitCallback = function() {
            callback.@com.gwtplatform.mvp.client.googleanalytics.universalanalytics.HitCallback::onCallback()();
        }

    }-*/;

    @Override
    public void put(final String fieldName, final JSONValue value) {
        jsonObject.put(fieldName, value);
    }

}
