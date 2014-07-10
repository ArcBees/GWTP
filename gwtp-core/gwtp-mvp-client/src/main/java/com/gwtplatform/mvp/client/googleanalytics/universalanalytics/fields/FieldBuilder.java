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
import com.gwtplatform.mvp.client.googleanalytics.universalanalytics.OptionsCallback;

public class FieldBuilder extends AbstractFieldBuilder {
    FieldBuilder(final JSONObject jsonObject) {
        super(jsonObject);
    }

    public FieldBuilder(final OptionsCallback optionsCallback) {
        super(optionsCallback);
    }

    public AppTrackingFieldBuilder appTracking() {
        return new AppTrackingFieldBuilder(build());
    }

    public ContentFieldBuilder content() {
        return new ContentFieldBuilder(build());
    }

    public ContentExperimentsFieldBuilder contentExperiments() {
        return new ContentExperimentsFieldBuilder(build());
    }

    public CreateOnlyFieldBuilder createOnlyFields() {
        return new CreateOnlyFieldBuilder(build());
    }

    public CustomDimensionsFieldBuilder customDimensions() {
        return new CustomDimensionsFieldBuilder(build());
    }

    public EventFieldBuilder event(final String category, final String action) {
        return new EventFieldBuilder(build(), category, action);
    }

    public ExceptionFieldBuilder exception() {
        return new ExceptionFieldBuilder(build());
    }

    public GeneralFieldBuilder general() {
        return new GeneralFieldBuilder(build());
    }

    public HitFieldBuilder hit() {
        return new HitFieldBuilder(build());
    }

    public SessionFieldBuilder session() {
        return new SessionFieldBuilder(build());
    }

    public SocialNetworkFieldBuilder socialNetwork(final String socialNetwork, final String socialAction,
            final String socialTarget) {
        return new SocialNetworkFieldBuilder(build(), socialNetwork, socialAction, socialTarget);
    }

    public SystemInfoFieldBuilder systemInfo() {
        return new SystemInfoFieldBuilder(build());
    }

    public TrafficSourcesFieldBuilder trafficSources() {
        return new TrafficSourcesFieldBuilder(build());
    }

    public UserTimingFieldBuilder userTiming() {
        return new UserTimingFieldBuilder(build());
    }
}
