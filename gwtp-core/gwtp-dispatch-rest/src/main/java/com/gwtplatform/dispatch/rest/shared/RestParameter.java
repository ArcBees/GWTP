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

package com.gwtplatform.dispatch.rest.shared;

import java.util.Collection;

import com.google.common.base.Joiner;

/**
 * This class is used by {@link com.gwtplatform.dispatch.rest.client.AbstractRestAction} to associate a parameter name
 * to a value.
 */
public class RestParameter {
    private String name;
    private String stringValue;

    RestParameter() {
    }

    public RestParameter(String name, Object object) {
        this.name = name;
        if (object instanceof Collection) {
            stringValue = Joiner.on(',').join((Collection) object);
        } else if (object != null) {
            this.stringValue = object.toString();
        } else {
            stringValue = "";
        }
    }

    /**
     * @return the name of this parameter.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the value of this parameter, as a string.
     */
    public String getStringValue() {
        return stringValue;
    }

    @Override
    public String toString() {
        return "{\"key\": \"" + name + "\", \"value\": \"" + stringValue + "\"}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        RestParameter that = (RestParameter) o;

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }

        if (stringValue != null ? !stringValue.equals(that.stringValue) : that.stringValue != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (stringValue != null ? stringValue.hashCode() : 0);
        return result;
    }
}
