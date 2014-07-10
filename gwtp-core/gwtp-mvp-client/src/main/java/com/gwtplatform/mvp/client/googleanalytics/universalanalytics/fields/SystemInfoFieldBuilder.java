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
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class SystemInfoFieldBuilder extends FieldBuilder {
    SystemInfoFieldBuilder(final JSONObject jsonObject) {
        super(jsonObject);
    }

    /**
     * Optional.
     * Specifies the character set used to encode the page / document. This field is initialized by the create command.
     * @param documentEncoding - default: UTF-8
     */
    public SystemInfoFieldBuilder documentEncoding(final String documentEncoding) {
        put("encoding", new JSONString(documentEncoding));
        return this;
    }

    /**
     * Optional.
     * Specifies the flash version. This field is initialized by the create command.
     * @param flashVersion <br>
     * Default: none;<br>
     * Example Value: 10 1 r103
     */
    public SystemInfoFieldBuilder flashVersion(final String flashVersion) {
        put("flashVersion", new JSONString(flashVersion));
        return this;
    }

    /**
     * Optional.
     * Specifies whether Java was enabled. This field is initialized by the create command.
     * @param javaEnabled <br>
     * Default: none;<br>
     */
    public SystemInfoFieldBuilder javaEnabled(final boolean javaEnabled) {
        put("javaEnabled", JSONBoolean.getInstance(javaEnabled));
        return this;
    }

    /**
     * Optional.
     * Specifies the screen color depth. This field is initialized by the create command.
     * @param screenColorDepth <br>
     * Default: none;<br>
     * Example Value: 24-bits
     */
    public SystemInfoFieldBuilder screenColorDepth(final String screenColorDepth) {
        put("screenColors", new JSONString(screenColorDepth));
        return this;
    }

    /**
     * Optional.
     * Specifies the screen resolution. This field is initialized by the create command.
     * @param screenResolution -<br>
     *     Default: none;<br>
     *     Example Value: 800x600
     */
    public SystemInfoFieldBuilder screenResolution(final String screenResolution) {
        put("screeenResolution", new JSONString(screenResolution));
        return this;
    }

    /**
     * Optional.
     * Specifies the language. This field is initialized by the create command.
     * @param userLanguage <br>
     * Default: none<br>
     * Example Value: en-us
     */
    public SystemInfoFieldBuilder userLanguage(final String userLanguage) {
        put("language", new JSONString(userLanguage));
        return this;
    }

    /**
     * Optional.
     * Specifies the viewable area of the browser / device. This field is initialized by the create command.
     * @param viewportSize <br>
     * Default: none<br>
     * Example Value: 123x456
     */
    public SystemInfoFieldBuilder viewportSize(final String viewportSize) {
        put("viewportSize", new JSONString(viewportSize));
        return this;
    }
}
