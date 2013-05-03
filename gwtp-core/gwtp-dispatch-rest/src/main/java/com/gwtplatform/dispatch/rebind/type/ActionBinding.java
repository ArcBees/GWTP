/**
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.dispatch.rebind.type;

import com.google.gwt.core.ext.typeinfo.JParameter;

public class ActionBinding {
    private final String actionClass;
    private final String methodName;
    private final String resultClass;
    private final JParameter[] parameters;

    public ActionBinding(String actionClass, String methodName, String resultClass, JParameter[] parameters) {
        this.actionClass = actionClass;
        this.methodName = methodName;
        this.resultClass = resultClass;
        this.parameters = parameters;
    }

    public String getActionClass() {
        return actionClass;
    }

    public String getResultClass() {
        return resultClass;
    }

    public JParameter[] getParameters() {
        return parameters;
    }

    public String getMethodName() {
        return methodName;
    }
}
