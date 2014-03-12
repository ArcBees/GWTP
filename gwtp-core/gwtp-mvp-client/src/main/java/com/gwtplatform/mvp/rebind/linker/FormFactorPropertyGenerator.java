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

package com.gwtplatform.mvp.rebind.linker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.SortedSet;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.ConfigurationProperty;
import com.google.gwt.core.ext.linker.PropertyProviderGenerator;

public class FormFactorPropertyGenerator implements PropertyProviderGenerator {
    private static final String QUERY_PARAM_CONFIGURATION = "gwtp.formFactorQueryParam";
    private static final String DEFAULT_QUERY_PARAM_NAME = "formfactor";
    private static final String FORM_FACTOR_JS = "/com/gwtplatform/mvp/rebind/linker/formFactor.js";

    private static final String OUTPUT = "{\n%s\nreturn findFormFactor('%s', location, navigator);\n}";

    @Override
    public String generate(TreeLogger logger, SortedSet<String> possibleValues, String fallback,
            SortedSet<ConfigurationProperty> configProperties) throws UnableToCompleteException {
        String queryParamName = getQueryParamName(configProperties);

        String detectionCode;
        try {
            detectionCode = getFormFactorDetectionCode();
        } catch (IOException e) {
            logger.log(Type.ERROR, "Unable to retrieve Form Factor detection code.", e);
            throw new UnableToCompleteException();
        }

        return String.format(OUTPUT, detectionCode, queryParamName);
    }

    private String getQueryParamName(SortedSet<ConfigurationProperty> configProperties) {
        String queryParam = DEFAULT_QUERY_PARAM_NAME;

        for (ConfigurationProperty configProperty : configProperties) {
            if (configProperty.getName().equals(QUERY_PARAM_CONFIGURATION)) {
                queryParam = configProperty.getValues().get(0);
                break;
            }
        }

        return queryParam;
    }

    private String getFormFactorDetectionCode() throws IOException {
        StringBuilder code = new StringBuilder();
        URL formFactorJs = getClass().getResource(FORM_FACTOR_JS);
        InputStream stream = null;

        try {
            stream = formFactorJs.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String line;
            while ((line = reader.readLine()) != null) {
                code.append(line).append('\n');
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return code.toString();
    }
}
