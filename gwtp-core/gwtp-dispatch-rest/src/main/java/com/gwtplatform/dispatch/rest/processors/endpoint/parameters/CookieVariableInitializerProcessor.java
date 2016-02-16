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

import com.google.auto.service.AutoService;
import com.gwtplatform.dispatch.rest.client.core.parameters.CookieParameter;
import com.gwtplatform.dispatch.rest.processors.details.HttpVariable;
import com.gwtplatform.dispatch.rest.shared.HttpParameter.Type;
import com.gwtplatform.processors.tools.outputter.CodeSnippet;

@AutoService(HttpVariableInitializerProcessor.class)
public class CookieVariableInitializerProcessor extends AbstractHttpVariableInitializerProcessor {
    @Override
    protected Type getSupportedParameterType() {
        return Type.COOKIE;
    }

    @Override
    protected CodeSnippet process(HttpVariable httpVariable, String httpNameArgument, String variableNameArgument,
            String dateFormatArgument) {
        return buildConstructorCall(CookieParameter.class, httpNameArgument, variableNameArgument, dateFormatArgument);
    }
}
