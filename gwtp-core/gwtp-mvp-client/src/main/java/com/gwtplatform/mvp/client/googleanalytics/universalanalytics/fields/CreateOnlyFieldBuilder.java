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

import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.gwtplatform.mvp.client.googleanalytics.universalanalytics.OptionsCallback;

public class CreateOnlyFieldBuilder extends FieldBuilder {

    CreateOnlyFieldBuilder(final JSONObject jsonObject, 
            final OptionsCallback optionsCallback) {
        super(jsonObject, optionsCallback);
    }

    /**
     * Optional. This may only be set in the create method.
     * By default, analytics.js will search for custom campaign parameters such as
     * utm_source, utm_medium, etc. in both the query string and anchor of the current
     * page's URL. Setting this field to false will result in ignoring any custom
     * campaign parameters that appear in the anchor.
     * @param allowAnchorParamters - default: true
     */
    public CreateOnlyFieldBuilder allowAnchorParameters(final boolean allowAnchorParameters) {
        put("allowAnchor", JSONBoolean.getInstance(allowAnchorParameters));
        return this;
    }

    /**
     * Optional. This may only be set in the create method.
     * Setting this field to true will enables the parsing of cross-domain
     * linker parameters used to transfer state across domains.
     * @param allowLinker - default: false
     */
    public CreateOnlyFieldBuilder allowLinkerParameters(final boolean allowLinker) {
        put("allowLinker", JSONBoolean.getInstance(allowLinker));
        return this;
    }

    /**
     * Optional. This may only be set in the create method.
     * By default the HTTP referrer URL, which is used to attribute traffic sources,
     * is only sent when the hostname of the referring site differs from the hostname
     * of the current page. Enable this setting only if you want to process other pages
     * from your current host as referrals.
     * @param alwaysSendReferrer - default: false
     */
    public CreateOnlyFieldBuilder alwaysSendReferrer(final boolean alwaysSendReferrer) {
        put("alwaysSendReferrer", JSONBoolean.getInstance(alwaysSendReferrer));
        return this;
    }

    /**
     * Required for all hit types.
     * Anonymously identifies a browser instance. By default, this value is
     * stored as part of the first-party analytics tracking cookie with a two-year expiration.
     * @param clientId - default: Randomly Generated
     */
    public CreateOnlyFieldBuilder clientId(final String clientId) {
        put("clientId", new JSONString(clientId));
        return this;
    }

    /**
     * Optional. This may only be set in the create method.
     * Specifies the domain used to store the analytics cookie. Setting this to
     * 'none' sets the cookie without specifying a domain.
     * @param cookieDomain - default: document.location.hostname (normalized)
     */
    public CreateOnlyFieldBuilder cookieDomain(final String cookieDomain) {
        put("cookieDomain", new JSONString(cookieDomain));
        return this;
    }

    /**
     * Optional. This may only be set in the create method.
     * Specifies the cookie expiration, in seconds.
     * @param cookieExpires - default: 63072000 (two years)
     */
    public CreateOnlyFieldBuilder cookieExpiration(final int cookieExpires) {
        put("cookieExpires", new JSONNumber(cookieExpires));
        return this;
    }

    /**
     * Optional. This may only be set in the create method.
     * Name of the cookie used to store analytics data
     * @param cookieName - default: _ga
     */
    public CreateOnlyFieldBuilder cookieName(final String cookieName) {
        put("cookieName", new JSONString(cookieName));
        return this;
    }

    /**
     * Optional. This may only be set in the create method.
     * This field is used to configure how analytics.js searches for cookies generated
     * by earlier Google Analytics tracking scripts such as ga.js and urchin.js.
     * @param legacyCookieDomain - default: none
     */
    public CreateOnlyFieldBuilder legacyCookieDomain(final String legacyCookieDomain) {
        put("legacyCookieDomain", new JSONString(legacyCookieDomain));
        return this;
    }

    /**
     * Optional. This may only be set in the create method.
     * @param name Name of the tracker object. t0 if not set
     */
    public CreateOnlyFieldBuilder name(final String name) {
        put("name", new JSONString(name));
        return this;
    }

    /**
     * Optional. This may only be set in the create method.
     * Specifies what percentage of users should be tracked. This defaults to 100
     * (no users are sampled out) but large sites may need to use a lower sample rate to stay
     * within Google Analytics processing limits.
     * @param sampleRate - default: 100
     */
    public CreateOnlyFieldBuilder sampleRate(final float sampleRate) {
        put("sampleRate", new JSONNumber(sampleRate));
        return this;
    }

    /**
     * Optional. This may only be set in the create method.
     * This setting determines how often site speed tracking beacons will be sent. By default,
     * 1% of users will be automatically be tracked.Note: Analytics restricts Site Speed collection
     * hits for a single property to the greater of 1% of users or 10K hits per day in order to ensure
     * an equitable distribution of system resources for this feature.
     * @param siteSpeedSampleRate - default: 1
     */
    public CreateOnlyFieldBuilder siteSpeedSampleRate(final int siteSpeedSampleRate) {
        put("siteSpeedSampleRate", new JSONNumber(siteSpeedSampleRate));
        return this;
    }

    /**
     * Optional. This may only be set in the create method.
     * This is intended to be a known identifier for a user provided by the site owner/tracking
     * library user. It may not itself be PII. The value should never be persisted in GA cookies
     * or other Analytics provided storage.
     * @param userId - default: none
     */
    public CreateOnlyFieldBuilder userId(final String userId) {
        put("userId", new JSONString(userId));
        return this;
    }
}
