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

package com.gwtplatform.dispatch.shared.rest;

import java.util.Collection;

import com.google.common.base.Joiner;

public class RestParameter {
    private String name;
    private String stringValue;

    RestParameter() {
    }

    public RestParameter(String name, Object object) {
        this.name = name;
        if (object instanceof Collection) {
            stringValue = Joiner.on(',').join((Collection) object);
        } else {
            this.stringValue = object.toString();
        }
    }

    public String getName() {
        return name;
    }

    public String getStringValue() {
        return stringValue;
    }
}
