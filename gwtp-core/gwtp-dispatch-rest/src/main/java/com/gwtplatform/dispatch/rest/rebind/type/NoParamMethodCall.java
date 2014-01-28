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

package com.gwtplatform.dispatch.rest.rebind.type;

public class NoParamMethodCall implements MethodCall {
    private final String methodName;
    private final String fieldName;
    private final String value;

    public NoParamMethodCall(String methodName, String fieldName, String value) {
        this.methodName = methodName;
        this.fieldName = fieldName;
        this.value = value;
    }

    @Override
    public boolean isFromParam() {
        return false;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getValue() {
        return value;
    }
}
