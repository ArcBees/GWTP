/*
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

package com.gwtplatform.dispatch.rest.client.core.parameters;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.logging.client.LogConfiguration;
import com.gwtplatform.common.shared.UrlUtils;
import com.gwtplatform.dispatch.rest.shared.DateFormat;

public class PathParameter extends ClientHttpParameter {
    private static final Logger LOGGER = Logger.getLogger(PathParameter.class.getName());

    private final String regex;
    private final UrlUtils urlUtils;

    public PathParameter(
            String name,
            Object object,
            String regex,
            UrlUtils urlUtils) {
        this(name, object, DateFormat.DEFAULT, regex, urlUtils);
    }

    public PathParameter(
            String name,
            Object object,
            String dateFormat,
            String regex,
            UrlUtils urlUtils) {
        super(Type.PATH, name, object, dateFormat);

        this.regex = regex;
        this.urlUtils = urlUtils;

        validate();
    }

    private void validate() {
        if (LogConfiguration.loggingIsEnabled() && regex != null) {
            String value = parseObject(getObject());
            if (!value.matches(regex)) {
                LOGGER.log(Level.WARNING, "Path parameter '" + getName() + "': Value '" + value + "' does not match "
                        + "regular expression '" + regex + "'.");
            }
        }
    }

    @Override
    protected String encodeValue(String value) {
        return urlUtils.encodePathSegment(value);
    }
}
