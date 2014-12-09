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

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class is used by {@link com.gwtplatform.dispatch.rest.client.AbstractRestAction AbstractRestAction} to associate
 * a parameter name to a value.
 */
public class HttpParameter {
    public enum Type {
        /** Represent {@link javax.ws.rs.CookieParam @CookieParam} parameters. */
        COOKIE,
        /** Represent {@link javax.ws.rs.FormParam @FormParam} parameters. */
        FORM,
        /** Represent {@link javax.ws.rs.HeaderParam @HeaderParam} parameters. */
        HEADER,
        /** Represent {@link javax.ws.rs.MatrixParam @MatrixParam} parameters. */
        MATRIX,
        /** Represent {@link javax.ws.rs.PathParam @PathParam} parameters. */
        PATH,
        /** Represent {@link javax.ws.rs.QueryParam @QueryParam} parameters. */
        QUERY
    }

    private final Type type;
    private final String name;
    private final Object object;

    public HttpParameter(
            Type type,
            String name,
            Object object) {
        this.type = type;
        this.name = name;
        this.object = object;
    }

    public Type getType() {
        return type;
    }

    /**
     * @return the name of this parameter.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the object associated with this parameter.
     */
    public Object getObject() {
        return object;
    }

    // TODO: To be removed
    public String getStringValue() {
        return String.valueOf(object);
    }

    @Override
    public String toString() {
        return "HttpParameter{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", object=" + object +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HttpParameter that = (HttpParameter) o;

        return name.equals(that.name)
                || (object == null ? that.object == null : object.equals(that.object));
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (object != null ? object.hashCode() : 0);
        return result;
    }
}
