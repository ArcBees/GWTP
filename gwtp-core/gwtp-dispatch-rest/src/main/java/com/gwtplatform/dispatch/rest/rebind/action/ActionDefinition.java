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

package com.gwtplatform.dispatch.rest.rebind.action;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.gwtplatform.dispatch.rest.rebind.Parameter;
import com.gwtplatform.dispatch.rest.rebind.parameter.HttpParameter;
import com.gwtplatform.dispatch.rest.rebind.utils.ClassDefinition;
import com.gwtplatform.dispatch.rest.shared.HttpMethod;

public class ActionDefinition extends ClassDefinition {
    private final HttpMethod verb;
    private final String path;
    private final boolean secured;
    private final String contentType;
    private final JClassType resultType;
    private final List<HttpParameter> httpParameters;
    private final Parameter bodyParameter;

    public ActionDefinition(
            String packageName,
            String className,
            HttpMethod verb,
            String path,
            boolean secured,
            String contentType,
            JClassType resultType,
            List<HttpParameter> httpParameters,
            Parameter bodyParameter) {
        super(packageName, className);

        this.verb = verb;
        this.path = path;
        this.secured = secured;
        this.contentType = contentType;
        this.resultType = resultType;
        this.httpParameters = httpParameters;
        this.bodyParameter = bodyParameter;
    }

    public HttpMethod getVerb() {
        return verb;
    }

    public String getPath() {
        return path;
    }

    public boolean isSecured() {
        return secured;
    }

    public String getContentType() {
        return contentType;
    }

    public JClassType getResultType() {
        return resultType;
    }

    public List<HttpParameter> getHttpParameters() {
        return Lists.newArrayList(httpParameters);
    }

    public Parameter getBodyParameter() {
        return bodyParameter;
    }

    public boolean hasBody() {
        return bodyParameter != null;
    }
}
