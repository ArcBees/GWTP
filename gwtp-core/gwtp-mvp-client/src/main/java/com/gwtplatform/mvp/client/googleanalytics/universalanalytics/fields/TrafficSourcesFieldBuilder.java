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

public class TrafficSourcesFieldBuilder extends FieldBuilder {
    TrafficSourcesFieldBuilder(final JSONObject jsonObject,
            final OptionsCallback optionsCallback) {
        super(jsonObject, optionsCallback);
    }

    /**
     * Optional.
     * Specifies the campaign content.
     * @param campaignContent - default: none
     */
    public TrafficSourcesFieldBuilder campaignContent(final String campaignContent) {
        put("campaignContent", new JSONString(campaignContent));
        return this;
    }

    /**
     * Optional.
     * Specifies the campaign ID.
     * @param campaignId - default: none
     */
    public TrafficSourcesFieldBuilder campaignId(final String campaignId) {
        put("campaignId", new JSONString(campaignId));
        return this;
    }

    /**
     * Optional.
     * Specifies the campaign keyword.
     * @param campaignKeyword - default: none
     */
    public TrafficSourcesFieldBuilder campaignKeyword(final String campaignKeyword) {
        put("campaignKeyword", new JSONString(campaignKeyword));
        return this;
    }

    /**
     * Optional.
     * Specifies the campaign medium.
     * @param campaignMedium - default: none
     */
    public TrafficSourcesFieldBuilder campaignMedium(final String campaignMedium) {
        put("campaignMedium", new JSONString(campaignMedium));
        return this;
    }

    /**
     * Optional.
     * Specifies the campaign name.
     * @param campaignName - default: none
     */
    public TrafficSourcesFieldBuilder campaignName(final String campaignName) {
        put("campaignName", new JSONString(campaignName));
        return this;
    }

    /**
     * Optional.
     * Specifies the campaign source.
     * @param campaignSource - default: none
     */
    public TrafficSourcesFieldBuilder campaignSource(final String campaignSource) {
        put("campaignSource", new JSONString(campaignSource));
        return this;
    }

    /**
     * Optional.
     * Specifies which referral source brought traffic to a website.
     * This value is also used to compute the traffic source.
     * The format of this value is a URL. This field is initialized by the create command and is only
     * set when the current hostname differs from the referrer hostname,
     * unless the 'alwaysSendReferrer' field is set to true.
     * @param documentReferrer - default: document.referrer
     */
    public TrafficSourcesFieldBuilder documentReferrer(final String documentReferrer) {
        put("referrer", new JSONString(documentReferrer));
        return this;
    }
}
