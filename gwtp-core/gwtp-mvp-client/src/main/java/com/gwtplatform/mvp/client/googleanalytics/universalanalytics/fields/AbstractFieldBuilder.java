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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.gwtplatform.mvp.client.googleanalytics.universalanalytics.HitCallback;
import com.gwtplatform.mvp.client.googleanalytics.universalanalytics.OptionsCallback;

public abstract class AbstractFieldBuilder {
    private final JSONObject jsonObject;

    AbstractFieldBuilder(final JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    AbstractFieldBuilder(final OptionsCallback callback) {
        this(new JSONObject());
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            @Override
            public void execute() {
                callback.onCallback(build());
            }
        });
    }

    public void addHitCallback(final HitCallback callback) {
        addHitCallback(jsonObject, callback);
    }

    private native void addHitCallback(JSONObject jsonObject, HitCallback callback) /*-{
        jsonObject.hitCallback = function() {
            callback.@com.gwtplatform.mvp.client.googleanalytics.universalanalytics.HitCallback::callback()();
        }
    }-*/;

    JSONObject build() {
        return jsonObject;
    }

    public void put(final String fieldName, final JSONValue value) {
        jsonObject.put(fieldName, value);
    }
}
