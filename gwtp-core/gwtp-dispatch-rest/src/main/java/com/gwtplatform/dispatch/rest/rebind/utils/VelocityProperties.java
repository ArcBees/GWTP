/*
 * Copyright 2015 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.rebind.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.Properties;

import com.google.gwt.core.ext.UnableToCompleteException;

import jakarta.inject.Inject;

public class VelocityProperties {
    private static final String VELOCITY_PROPERTIES = "com/gwtplatform/dispatch/rest/rebind/velocity.properties";

    private final Logger logger;

    private Properties properties;

    @Inject
    VelocityProperties(Logger logger) {
        this.logger = logger;
    }

    public Properties asProperties() throws UnableToCompleteException {
        if (properties == null) {
            readProperties(VELOCITY_PROPERTIES);
        }

        return properties;
    }

    private void readProperties(String fileName) throws UnableToCompleteException {
        ClassLoader loader = VelocityProperties.class.getClassLoader();
        properties = new Properties();

        try {
            readProperties(fileName, loader);
        } catch (Exception e) {
            logger.die("Unable to load properties from '%s'.", e, fileName);
        }
    }

    private void readProperties(String fileName, ClassLoader loader) throws IOException, MissingResourceException {
        InputStream resource = null;

        try {
            resource = loader.getResourceAsStream(fileName);
            maybeLoadProperties(resource, fileName);
        } finally {
            if (resource != null) {
                resource.close();
            }
        }
    }

    private void maybeLoadProperties(InputStream stream, String fileName) throws IOException, MissingResourceException {
        if (stream != null) {
            properties.load(stream);
        } else {
            throw new MissingResourceException("Unable to load properties", VelocityProperties.class.getName(),
                    fileName);
        }
    }
}
