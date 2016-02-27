/*
 * Copyright 2016 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.processors.endpoint.parameters;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.gwtplatform.dispatch.rest.processors.details.HttpAnnotation;
import com.gwtplatform.dispatch.rest.processors.details.HttpVariable;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;
import com.gwtplatform.processors.tools.AbstractContextProcessor;
import com.gwtplatform.processors.tools.outputter.CodeSnippet;

import static java.util.Arrays.asList;

public abstract class AbstractHttpVariableInitializerProcessor
        extends AbstractContextProcessor<HttpVariable, CodeSnippet>
        implements HttpVariableInitializerProcessor {
    @Override
    public boolean canProcess(HttpVariable variable) {
        return variable.getHttpAnnotation().get().getParameterType() == getSupportedParameterType();
    }

    protected abstract Type getSupportedParameterType();

    @Override
    public CodeSnippet process(HttpVariable httpVariable) {
        HttpAnnotation annotation = httpVariable.getHttpAnnotation().get();
        String httpNameArgument = '"' + annotation.getName() + '"';
        String variableNameArgument = httpVariable.getName();
        String dateFormatArgument = parseOptionalStringArgument(httpVariable.getDateFormat(), "defaultDateFormat");

        return process(httpVariable, httpNameArgument, variableNameArgument, dateFormatArgument);
    }

    protected abstract CodeSnippet process(HttpVariable httpVariable, String httpNameArgument,
            String variableNameArgument, String dateFormatArgument);

    protected String parseOptionalStringArgument(Optional<String> optional, String defaultValue) {
        if (optional.isPresent()) {
            return '"' + optional.get() + '"';
        }
        return defaultValue;
    }

    protected CodeSnippet buildConstructorCall(Class<?> clazz, String... arguments) {
        StringBuilder output = new StringBuilder("new " + clazz.getSimpleName() + "(");

        if (arguments != null) {
            Joiner.on(", ").appendTo(output, arguments);
        }

        output.append(")");

        return new CodeSnippet(output.toString(), asList(clazz.getCanonicalName()));
    }
}
