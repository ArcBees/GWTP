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

public class AppTrackingFieldBuilder extends FieldBuilder {

    AppTrackingFieldBuilder(final JSONObject jsonObject, final OptionsCallback optionsCallback) {
        super(jsonObject, optionsCallback);
    }

    /**
     * Optional.
     * Specifies the application identifier.
     * @param applicationId <br>
     * Default: none<br>
     * Example Value: com.company.app
     */
    public AppTrackingFieldBuilder applicationId(final String applicationId) {
        put("appId", new JSONString(applicationId));
        return this;
    }

    /**
     * Optional.
     * Specifies the application installer identifier.
     * @param applicationInstallerID <br>
     * Default: none<br>
     * Example Value: com.platform.vending
     */
    public AppTrackingFieldBuilder applicationInstallerID(final String applicationInstallerID) {
        put("appInstallerId", new JSONString(applicationInstallerID));
        return this;
    }

    /**
     * Optional.
     * Specifies the application name.
     * @param applicationName <br>
     * Default: none<br>
     * Example Value: My App
     */
    public AppTrackingFieldBuilder applicationName(final String applicationName) {
        put("appName", new JSONString(applicationName));
        return this;
    }

    /**
     * Optional.
     * Specifies the application version.
     * @param applicationVersion <br>
     * Default: none<br>
     * Example Value: 1.2
     */
    public AppTrackingFieldBuilder applicationVersion(final String applicationVersion) {
        put("appVersion", new JSONString(applicationVersion));
        return this;
    }

}
