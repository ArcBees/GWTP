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

public class ContentFieldBuilder extends FieldBuilder {
    ContentFieldBuilder(final JSONObject jsonObject,
            final OptionsCallback optionsCallback) {
        super(jsonObject, optionsCallback);
    }

    /**
     * Optional.
     * Specifies the hostname from which content was hosted.
     * @param documentHostName <br>
     * Default: none;
     */
    public ContentFieldBuilder documentHostName(final String documentHostName) {
        put("hostname", new JSONString(documentHostName));
        return this;
    }

    /**
     * Optional.
     * Specifies the full URL (excluding anchor) of the page. This field is initialized by the create command.
     * @param documentLocationURL <br>
     * Default: none;
     */
    public ContentFieldBuilder documentLocationURL(final String documentLocationURL) {
        put("location", new JSONString(documentLocationURL));
        return this;
    }

    /**
     * Optional.
     * The path portion of the page URL. Should begin with '/'. Used to specify virtual page paths.
     * @param viewportSize <br>
     * Default: none;<br>
     */
    public ContentFieldBuilder documentPath(final String documentPath) {
        put("page", new JSONString(documentPath));
        return this;
    }

    /**
     * Optional.
     * The title of the page / document.
     * @param documentTitle <br>
     * Default: document.title<br>
     */
    public ContentFieldBuilder documentTitle(final String documentTitle) {
        put("title", new JSONString(documentTitle));
        return this;
    }

    /**
     * Optional.
     * The ID of a clicked DOM element, used to disambiguate multiple links to the same URL in
     * In-Page Analytics reports when Enhanced Link Attribution is enabled for the property.
     * @param linkId <br>
     * Default: none<br>
     * Example Value: nav_bar
     */
    public ContentFieldBuilder linkId(final String linkId) {
        put("&linkid", new JSONString(linkId));
        return this;
    }

    /**
     * Optional.
     * If not specified, this will default to the unique URL of the page by either using the
     * &dl parameter as-is or assembling it from &dh and &dp. App tracking makes use of this for
     * the 'Screen Name' of the screenview hit.
     * @param screenName <br>
     * Example Value: High Scores<br>
     */
    public ContentFieldBuilder screenName(final String screenName) {
        put("screenName", new JSONString(screenName));
        return this;
    }
}

