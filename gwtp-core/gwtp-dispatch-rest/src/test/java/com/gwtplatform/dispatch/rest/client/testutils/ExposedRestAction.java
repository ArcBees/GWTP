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

package com.gwtplatform.dispatch.rest.client.testutils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.gwtplatform.dispatch.rest.client.codegen.AbstractRestAction;
import com.gwtplatform.dispatch.rest.client.core.parameters.HttpParameterFactory;
import com.gwtplatform.dispatch.rest.rebind.utils.PathResolver;
import com.gwtplatform.dispatch.rest.shared.ContentType;
import com.gwtplatform.dispatch.rest.shared.DateFormat;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;

/**
 * Used by test code to expose protected methods from {@link com.gwtplatform.dispatch.rest.client.codegen
 * .AbstractRestAction AbstractRestAction}. The goal is to help clean up the test code.
 */
public abstract class ExposedRestAction<R> extends AbstractRestAction<R> {
    private String bodyClass;
    private String resultClass;
    private List<ContentType> produced = new ArrayList<ContentType>();
    private List<ContentType> consumed = new ArrayList<ContentType>();

    protected ExposedRestAction(
            HttpParameterFactory factory,
            HttpMethod httpMethod,
            String rawServicePath) {
        super(factory, DateFormat.DEFAULT, httpMethod, rawServicePath, PathResolver.resolvePath(rawServicePath));

        // also resolve the regular expressions add them to the newly created instance
        Map<String, String> regex = PathResolver.extractPathParameterRegex(rawServicePath);
        if (regex != null) {
            for (Entry<String, String> entry : regex.entrySet()) {
                addParameterExpression(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void setBodyParam(Object value) {
        super.setBodyParam(value);
    }

    @Override
    public void addParam(Type type, String name, Object value) {
        super.addParam(type, name, value);
    }

    public void setBodyClass(String bodyClass) {
        this.bodyClass = bodyClass;
    }

    public void setResultClass(String resultClass) {
        this.resultClass = resultClass;
    }

    @Override
    public String getBodyClass() {
        return bodyClass;
    }

    @Override
    public String getResultClass() {
        return resultClass;
    }

    @Override
    public List<ContentType> getClientProducedContentTypes() {
        return produced;
    }

    @Override
    public List<ContentType> getClientConsumedContentTypes() {
        return consumed;
    }

    public void setClientProducedContentTypes(List<ContentType> produced) {
        this.produced = produced;
    }

    public void setClientConsumedContentTypes(List<ContentType> consumed) {
        this.consumed = consumed;
    }
}
