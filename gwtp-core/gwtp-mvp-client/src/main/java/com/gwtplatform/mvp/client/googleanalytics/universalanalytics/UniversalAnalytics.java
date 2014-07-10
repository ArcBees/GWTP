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
package com.gwtplatform.mvp.client.googleanalytics.universalanalytics;

import com.gwtplatform.mvp.client.googleanalytics.GoogleAnalytics;
import com.gwtplatform.mvp.client.googleanalytics.universalanalytics.fields.ContentFieldBuilder;
import com.gwtplatform.mvp.client.googleanalytics.universalanalytics.fields.CreateOnlyFieldBuilder;
import com.gwtplatform.mvp.client.googleanalytics.universalanalytics.fields.EventFieldBuilder;
import com.gwtplatform.mvp.client.googleanalytics.universalanalytics.fields.FieldBuilder;
import com.gwtplatform.mvp.client.googleanalytics.universalanalytics.fields.SocialNetworkFieldBuilder;
import com.gwtplatform.mvp.client.googleanalytics.universalanalytics.fields.UserTimingFieldBuilder;

public interface UniversalAnalytics extends GoogleAnalytics {

    CreateOnlyFieldBuilder create();

    CreateOnlyFieldBuilder create(String userAccount);

    FieldBuilder send(HitType hitType);

    FieldBuilder send(String trackerName, HitType hitType);

    EventFieldBuilder sendEvent(String category, String action);

    EventFieldBuilder sendEvent(String trackerName, String category, String action);

    ContentFieldBuilder sendPageView();

    ContentFieldBuilder sendPageView(String trackerName);

    SocialNetworkFieldBuilder sendSocial(String socialNetwork, String socialAction, String socialTarget);

    SocialNetworkFieldBuilder sendSocial(String trackerName, String socialNetwork, String socialAction,
            String socialTarget);

    UserTimingFieldBuilder sendTiming();

    UserTimingFieldBuilder sendTiming(String trackerName);

    FieldBuilder setGlobalSettings();
}
