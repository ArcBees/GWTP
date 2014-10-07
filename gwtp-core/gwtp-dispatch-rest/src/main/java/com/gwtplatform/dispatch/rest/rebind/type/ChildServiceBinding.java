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

package com.gwtplatform.dispatch.rest.rebind.type;

import java.util.List;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JParameter;

public class ChildServiceBinding extends ServiceBinding {
    private final String methodName;

    public ChildServiceBinding(
            String resourcePath,
            String implPackage,
            String implName,
            JClassType service,
            String methodName,
            List<JParameter> parameters) {
        super(resourcePath, implPackage, implName, service);

        setCtorParameters(parameters);

        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }
}
